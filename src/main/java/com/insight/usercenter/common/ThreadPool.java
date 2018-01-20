package com.insight.usercenter.common;

import com.insight.usercenter.common.dto.UserDTO;
import com.insight.usercenter.common.entity.Device;
import com.insight.usercenter.common.entity.User;
import com.insight.usercenter.common.entity.UserOpenId;
import com.insight.usercenter.common.mapper.UserMapper;
import com.insight.usercenter.common.utils.QiniuHelper;
import com.insight.usercenter.common.utils.httpClient.HttpClientUtil;
import com.insight.usercenter.common.utils.message.Message;
import com.insight.usercenter.common.utils.message.SmsUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author 宣炳刚
 * @date 2018/1/10
 * @remark 线程池
 */
@Component
public class ThreadPool {
    private final ScheduledExecutorService executorService;
    private final UserMapper mapper;
    private Logger logger;

    /**
     * 构造方法
     *
     * @param mapper 自动注入的UserMapper
     */
    @Autowired
    public ThreadPool(UserMapper mapper) {
        this.mapper = mapper;

        Integer nThreads = Runtime.getRuntime().availableProcessors() * 2;
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("CallMsgCenter-Pool-%d").daemon(true).build();
        executorService = new ScheduledThreadPoolExecutor(nThreads, threadFactory);
        logger = LoggerFactory.getLogger(ThreadPool.class);
    }

    /**
     * 新增用户
     *
     * @param user 用户对象实体
     */
    public void addUser(UserDTO user) {
        executorService.execute(() -> {
            String url = getHeadImg(user.getHeadImg());
            user.setHeadImg(url);

            mapper.addUser(user);
        });
    }

    /**
     * 更新用户微信用户信息
     *
     * @param dto 用户对象实体
     */
    public void updateUser(UserDTO dto) {
        executorService.execute(() -> {
            User user = mapper.getUserById(dto.getId());
            if (!user.getName().matches("^1[3-9]\\d{9}")) {
                dto.setName(user.getName());
            }

            String url = getHeadImg(user.getHeadImg());
            dto.setHeadImg(url);

            mapper.updateWeChatInfo(dto);
        });
    }

    /**
     * 读取微信头像并上传七牛
     *
     * @param url 微信头像URL
     * @return 七牛头像URL
     */
    private String getHeadImg(String url) {
        byte[] data = HttpClientUtil.getByteFromUrl(url);
        return data == null ? null : QiniuHelper.upAndDownload(data);
    }

    /**
     * 绑定设备到用户
     *
     * @param userId      用户ID
     * @param deviceId    设备ID
     * @param deviceModel 设备型号
     */
    public void bindDeviceToUser(String userId, String deviceId, String deviceModel) {
        executorService.execute(() -> {
            //使之前该用户的在线设备离线
            mapper.logOffUser(userId);
            if (deviceId == null || deviceId.isEmpty()) {
                return;
            }

            //绑定设备到用户，若该设备已被绑定，则更新设备绑定信息，反之新增
            Device device = mapper.getDevice(deviceId);
            if (device == null) {
                device = new Device();
                device.setId(deviceId);
                device.setUserId(userId);
                device.setDeviceModel(deviceModel);
                device.setInvalid(false);
                device.setUpdateTime(new Date());
                mapper.addDevice(device);
            } else {
                device.setUserId(userId);
                device.setInvalid(false);
                mapper.updateDevice(device);
            }
        });
    }

    /**
     * 自动绑定租户ID和登录部门ID
     *
     * @param token Token
     */
    public void setTenantIdAndDeptId(Token token) {
        executorService.execute(() -> {
            List<String> list = mapper.getTenantIds(token.getUserId());
            if (list != null && list.size() == 1) {
                token.setTenantId(list.get(0));
                token.setChanged();
            }

            list = mapper.getDeptIds(token.getUserId());
            if (list != null && list.size() == 1) {
                token.setDeptId(list.get(0));
                token.setChanged();
            }

            token.setRoleList(mapper.getRoleIds(token.getUserId(), token.getTenantId(), token.getDeptId()));
        });
    }

    /**
     * 记录用户绑定的微信OpenID
     *
     * @param userId 用户ID
     * @param openId 微信OpenID
     * @param appId  微信AppID
     * @return 是否绑定成功
     */
    public void bindOpenId(String userId, String openId, String appId) {
        executorService.execute(() -> {
            UserOpenId userOpenId = new UserOpenId();
            userOpenId.setId(openId);
            userOpenId.setUserId(userId);
            userOpenId.setAppId(appId);

            Integer count = mapper.addUserOpenId(userOpenId);
            if (count.equals(0)) {
                logger.error("写入数据到数据库失败!");
            }
        });
    }

    /**
     * 短信发送
     *
     * @param message 消息对象实体
     */
    public void sendSms(Message message) {
        executorService.execute(() -> SmsUtils.sendSms(message));
    }
}
