package com.insight.usercenter.auth;


import com.insight.usercenter.common.Token;
import com.insight.usercenter.common.dto.RefreshToken;
import com.insight.usercenter.common.dto.Reply;
import com.insight.usercenter.common.dto.UserInfo;
import com.insight.usercenter.common.entity.Device;

import java.io.IOException;

/**
 * @author 宣炳刚
 * @date 2017/9/7
 * @remark 用户鉴权服务接口
 */
public interface AuthService {

    /**
     * 获取Code
     *
     * @param account 用户登录账号
     * @param type    登录类型(0:密码登录、1:验证码登录)
     * @return Reply
     */
    Reply getCode(String account, int type);

    /**
     * 获取Token数据
     *
     * @param account     登录账号
     * @param signature   签名
     * @param appId       应用ID
     * @param deviceId    设备ID
     * @param deviceModel 设备型号
     * @return Reply
     */
    Reply getToken(String account, String signature, String appId, String deviceId, String deviceModel);

    /**
     * 通过微信授权码获取访问令牌
     *
     * @param code        微信授权码
     * @param weChatAppId 微信appId
     * @param appId       应用ID
     * @param deviceId    设备ID
     * @param deviceModel 设备型号
     * @return Reply
     */
    Reply getTokenWithWeChat(String code, String weChatAppId, String appId, String deviceId, String deviceModel);

    /**
     * 通过微信用户信息获取访问令牌
     *
     * @param info 用户信息对象实体
     * @return Reply
     */
    Reply getTokenWithUserInfo(UserInfo info);

    /**
     * 刷新访问令牌过期时间
     *
     * @param token 刷新令牌字符串
     * @return Reply
     */
    Reply refreshToken(RefreshToken token);

    /**
     * 用户账号离线
     *
     * @param token   Token
     * @param tokenId 令牌ID
     * @return Reply
     */
    Reply deleteToken(Token token, String tokenId);

    /**
     * 为当前用户绑定当前使用的设备信息
     *
     * @param userId 用户ID
     * @param device 设备信息
     * @return Reply
     */
    Reply setDevice(String userId, Device device);

    /**
     * 为当前Token设置租户ID
     *
     * @param token    Token
     * @param tenantId 租户ID
     * @param deptId   登录部门ID
     * @return Reply
     */
    Reply setTenantId(Token token, String tenantId, String deptId);

    /**
     * 验证支付密码
     *
     * @param token       Token
     * @param payPassword 支付密码(MD5)
     * @return Reply
     */
    Reply verifyPayPassword(Token token, String payPassword);

    /**
     * 获取用户导航栏
     *
     * @param token Token
     * @param appId 应用ID
     * @return Reply
     */
    Reply getNavigators(Token token, String appId);

    /**
     * 获取业务模块的功能(及对用户的授权情况)
     *
     * @param token       Token
     * @param navigatorId 导航ID
     * @return Reply
     */
    Reply getModuleFunctions(Token token, String navigatorId);

    /**
     * 生成短信验证码
     *
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码;5:修改手机号)
     * @param key     手机号或手机号+验证答案的Hash值
     * @param minutes 验证码有效时长(分钟)
     * @param length  验证码长度
     * @return Reply
     */
    Reply getSmsCode(int type, String key, int minutes, int length);

    /**
     * 验证短信验证码
     *
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码;5:修改手机号)
     * @param mobile  手机号
     * @param code    验证码
     * @param isCheck 是否检验模式(true:检验模式,验证后验证码不失效;false:验证模式,验证后验证码失效)
     * @return Reply
     */
    Reply verifySmsCode(int type, String mobile, String code, Boolean isCheck);

    /**
     * 获取图形验证图片
     *
     * @param mobile 手机号
     * @return Reply
     * @throws IOException IO异常
     */
    Reply getVerifyPic(String mobile) throws IOException;

    /**
     * 验证图形验证答案
     *
     * @param key 手机号+验证答案的Hash值
     * @return Reply
     */
    Reply verifyPicCode(String key);
}
