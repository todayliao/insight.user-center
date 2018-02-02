package com.insight.usercenter.auth;

import com.insight.usercenter.common.dto.RefreshToken;
import com.insight.usercenter.common.dto.UserInfo;
import com.insight.usercenter.common.Verify;
import com.insight.usercenter.common.dto.Reply;
import com.insight.usercenter.common.entity.Device;
import com.insight.usercenter.common.utils.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author 宣炳刚
 * @date 2017/9/7
 * @remark 用户鉴权控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/authapi")
public class AuthController {
    @Autowired
    private AuthService service;

    /**
     * 获取Code
     *
     * @param account 用户登录账号
     * @param type    登录类型(0:密码登录、1:验证码登录)
     * @return Reply
     * 正常：返回接口调用成功,通过data返回Code
     * 异常：查找不到用户时返回用户账号不存在的错误
     */
    @GetMapping("/v1.1/tokens/codes")
    public Reply getCode(@RequestParam String account, @RequestParam(defaultValue = "0") int type) {
        return service.getCode(account, type);
    }

    /**
     * 获取Token
     *
     * @param account     登录账号
     * @param signature   签名
     * @param appId       应用ID
     * @param deviceId    设备ID
     * @param deviceModel 设备型号
     * @return Reply
     * 正常：返回接口调用成功,通过data返回Token数据
     * 异常：签名不正确时返回用户名或密码错误的错误
     * 异常：签名正确但获取不到用户ID或Token时返回服务器解析错误的错误
     */
    @GetMapping("/v1.1/tokens")
    public Reply getToken(@RequestParam String account, @RequestParam String signature, @RequestParam(required = false) String appId,
                          @RequestParam(required = false) String deviceId, @RequestParam(required = false) String deviceModel) {
        return service.getToken(account, signature, appId, deviceId, deviceModel);
    }

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
    @GetMapping("/v1.1/wechat/tokens")
    public Reply getTokenWithWeChat(@RequestParam String code, @RequestParam String weChatAppId, @RequestParam(required = false) String appId,
                                    @RequestParam(required = false) String deviceId, @RequestParam(required = false) String deviceModel) {
        return service.getTokenWithWeChat(code, weChatAppId, appId, deviceId, deviceModel);
    }

    /**
     * 通过微信用户信息获取访问令牌
     *
     * @param info 用户信息对象实体
     * @return Reply
     */
    @PostMapping("/v1.1/wechat/tokens")
    public Reply getTokenWithUserInfo(@RequestBody UserInfo info) {
        return service.getTokenWithUserInfo(info);
    }

    /**
     * 刷新Token，延长过期时间至2小时后
     *
     * @param token 刷新令牌
     * @return Reply
     * 正常：返回接口调用成功,通过data返回新的访问令牌过期时间
     * 异常：无法解析刷新令牌、不存在的用户或令牌验证未通过时，返回非法的令牌的错误
     * 异常：请求间隔低于5分钟时返回请求过于频繁的错误
     */
    @PutMapping("/v1.1/tokens")
    public Reply refreshToken(@RequestHeader("Authorization") String token) {
        RefreshToken refreshToken = Json.toBeanFromBase64(token, RefreshToken.class);
        return service.refreshToken(refreshToken);
    }

    /**
     * 用户账号离线
     *
     * @param token 访问令牌
     * @return Reply
     */
    @DeleteMapping("/v1.1/tokens")
    public Reply deleteToken(@RequestHeader(value = "Authorization") String token) {
        Verify verify = new Verify(token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) {
            return reply;
        }

        return service.deleteToken(verify.getBasis(), verify.getTokenId());
    }

    /**
     * 为当前用户绑定当前使用的设备信息
     *
     * @param token  访问令牌
     * @param device 设备信息
     * @return Reply
     */
    @PutMapping("/v1.1/tokens/device")
    public Reply setDevice(@RequestHeader(value = "Authorization") String token, @RequestBody Device device) {
        Verify verify = new Verify(token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) {
            return reply;
        }

        return service.setDevice(verify.getBasis().getUserId(), device);
    }

    /**
     * 为当前Token设置租户ID
     *
     * @param token    访问令牌
     * @param tenantId 租户ID
     * @return Reply
     */
    @PutMapping("/v1.1/tokens/tenant")
    public Reply setTenantId(@RequestHeader("Authorization") String token, @RequestBody String tenantId, @RequestParam(required = false) String deptId) {
        Verify verify = new Verify(token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) {
            return reply;
        }

        return service.setTenantId(verify.getBasis(), tenantId, deptId);
    }

    /**
     * 验证访问令牌合法性及鉴权
     *
     * @param token    访问令牌
     * @param function 功能ID或URL
     * @return Reply
     * 正常：返回接口调用成功
     * 异常：无法解析访问令牌、用户不存在、用户失效或令牌失效时返回令牌非法的错误
     * 异常：令牌过期时返回令牌过期的错误
     */
    @GetMapping("/v1.1/tokens/secret")
    public Reply verifyToken(@RequestHeader("Authorization") String token, @RequestParam(value = "function", required = false) String function) {
        Verify verify = new Verify(token);

        return verify.compare(function);
    }

    /**
     * 验证支付密码
     *
     * @param token       访问令牌
     * @param payPassword 支付密码(MD5)
     * @return Reply
     * 正常：根据验证结果返回接口调用成功或密码错误的错误
     * 异常：无法解析访问令牌、用户不存在、用户失效或令牌失效时返回令牌非法的错误
     */
    @GetMapping("/v1.1/tokens/paypw")
    public Reply verifyPayPassword(@RequestHeader("Authorization") String token, @RequestParam("paypw") String payPassword) {
        Verify verify = new Verify(token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) {
            return reply;
        }

        return service.verifyPayPassword(verify.getBasis(), payPassword);
    }

    /**
     * 获取用户导航栏
     *
     * @param token 访问令牌
     * @param appId 应用ID
     * @return Reply
     * 正常：返回接口调用成功,通过data返回导航数据集合
     * 异常：无法解析访问令牌、用户不存在、用户失效或令牌失效时返回令牌非法的错误
     */
    @GetMapping("/v1.1/navigators")
    public Reply getNavigators(@RequestHeader("Authorization") String token, @RequestParam("appId") String appId) {
        Verify verify = new Verify(token);
        Reply result = verify.compare();
        if (!result.getSuccess()) {
            return result;
        }

        return service.getNavigators(verify.getBasis(), appId);
    }

    /**
     * 获取业务模块的功能(及对用户的授权情况)
     *
     * @param token       访问令牌
     * @param navigatorId 导航ID
     * @return Reply
     * 正常：返回接口调用成功,通过data返回功能数据集合
     * 异常：无法解析访问令牌、用户不存在、用户失效或令牌失效时返回令牌非法的错误
     */
    @GetMapping("/v1.1/navigators/{id}/functions")
    public Reply getModuleFunctions(@RequestHeader("Authorization") String token, @PathVariable("id") String navigatorId) {
        Verify verify = new Verify(token);
        Reply reply = verify.compare();
        if (!reply.getSuccess()) {
            return reply;
        }

        return service.getModuleFunctions(verify.getBasis(), navigatorId);
    }

    /**
     * 生成短信验证码
     *
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码;5:修改手机号)
     * @param key     手机号或手机号+验证答案的Hash值
     * @param length  验证码长度
     * @param minutes 验证码有效时长(分钟)
     * @return Reply
     * 正常：返回接口调用成功,通过data返回验证码
     * 异常：一个手机号同类型请求间隔低于1分钟时返回请求过于频繁的错误
     */
    @GetMapping("/v1.1/smscodes/{type}/{key}")
    public Reply getSmsCode(@PathVariable("type") int type, @PathVariable("key") String key, @RequestParam("minutes") int minutes,
                            @RequestParam(value = "length", defaultValue = "6") int length) {
        return service.getSmsCode(type, key, minutes, length);
    }

    /**
     * 验证短信验证码
     *
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码;5:修改手机号)
     * @param mobile  手机号
     * @param code    验证码
     * @param isCheck 是否检验模式(true:检验模式,验证后验证码不失效;false:验证模式,验证后验证码失效)
     * @return Reply
     * 正常：根据验证结果返回接口调用成功或验证码错误的错误
     */
    @DeleteMapping("/v1.1/smscodes/{type}/{mobile}/{code}")
    public Reply verifySmsCode(@PathVariable("type") int type, @PathVariable("mobile") String mobile, @PathVariable("code") String code,
                               @RequestParam(value = "check", defaultValue = "false") Boolean isCheck) {
        return service.verifySmsCode(type, mobile, code, isCheck);
    }

    /**
     * 获取图形验证图片
     *
     * @param mobile 手机号
     * @return Reply
     */
    @GetMapping("/v1.1/piccodes")
    public Reply getVerifyPic(@RequestParam("mobile") String mobile) throws IOException {
        return service.getVerifyPic(mobile);
    }

    /**
     * 验证图形验证答案
     *
     * @param key 手机号+验证答案的Hash值
     * @return Reply
     */
    @GetMapping("/v1.1/piccodes/{key}")
    public Reply verifyPicCode(@PathVariable("key") String key) {
        return service.verifyPicCode(key);
    }
}
