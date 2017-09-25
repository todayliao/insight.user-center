package com.apin.usercenter.auth;

import com.apin.util.pojo.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 宣炳刚
 * @date 2017/9/7
 * @remark 用户鉴权控制器
 */
@RestController
@RequestMapping("/authapi")
public class AuthController {
    @Autowired
    private AuthService service;

    /**
     * 获取Code
     *
     * @param appId   应用ID
     * @param account 用户登录账号
     * @param type    登录类型(0:密码登录、1:验证码登录)
     * @param appName 应用名称(只在验证码登录时需要)
     * @return Reply
     * 正常：返回接口调用成功,通过data返回Code
     * 异常：查找不到用户时返回用户账号不存在的错误
     */
    @GetMapping("/v1.1/tokens/codes")
    public Reply getCode(@RequestParam(value = "appid", required = false) String appId, @RequestParam String account,
                         @RequestParam(defaultValue = "0") int type, @RequestParam(required = false) String appName) throws Exception {
        return service.getCode(appId, account, type, appName);
    }

    /**
     * 获取Token
     *
     * @param appId     应用ID
     * @param account   登录账号
     * @param signature 签名
     * @param deptId    登录部门ID
     * @return Reply
     * 正常：返回接口调用成功,通过data返回Token数据
     * 异常：签名不正确时返回用户名或密码错误的错误
     * 异常：签名正确但获取不到用户ID或Token时返回服务器解析错误的错误
     */
    @GetMapping(value = "/v1.1/tokens")
    public Reply getToken(@RequestParam(value = "appid", required = false) String appId, @RequestParam String account,
                          @RequestParam String signature, @RequestParam(required = false) String deptId) throws Exception {
        return service.getToken(appId, account, signature, deptId);
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
    @PutMapping(value = "/v1.1/tokens")
    public Reply refreshToken(@RequestHeader("Authorization") String token) throws Exception {
        return service.refreshToken(token);
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
    public Reply verifyToken(@RequestHeader("Authorization") String token,
                             @RequestParam(value = "function", required = false) String function) throws Exception {
        return service.verifyToken(token, function);
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
    public Reply verifyPayPassword(@RequestHeader("Authorization") String token,
                                   @RequestParam("paypw") String payPassword) throws Exception {
        return service.verifyPayPassword(token, payPassword);
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
    @GetMapping("/v1.1/modules")
    public Reply getNavigators(@RequestHeader("Authorization") String token,
                               @RequestHeader("Application") String appId) throws Exception {
        return service.getNavigators(token, appId);
    }

    /**
     * 获取业务模块的功能(及对用户的授权情况)
     *
     * @param token    访问令牌
     * @param moduleId 业务模块ID
     * @return Reply
     * 正常：返回接口调用成功,通过data返回功能数据集合
     * 异常：无法解析访问令牌、用户不存在、用户失效或令牌失效时返回令牌非法的错误
     */
    @GetMapping("/v1.1/modules/{id}/functions")
    public Reply getModuleFunctions(@RequestHeader("Authorization") String token,
                                    @PathVariable("id") String moduleId) throws Exception {
        return service.getModuleFunctions(token, moduleId);
    }

    /**
     * 生成短信验证码
     *
     * @param token   访问令牌
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码)
     * @param mobile  手机号
     * @param length  验证码长度
     * @param minutes 验证码有效时长(分钟)
     * @return Reply
     * 正常：返回接口调用成功,通过data返回验证码
     * 异常：一个手机号同类型请求间隔低于1分钟时返回请求过于频繁的错误
     */
    @GetMapping("/v1.1/smscodes/{type}/{mobile}")
    public Reply getSmsCode(@RequestHeader("Authorization") String token, @PathVariable("type") int type,
                            @PathVariable("mobile") String mobile, @RequestParam("minutes") int minutes,
                            @RequestParam(value = "length", defaultValue = "6") int length) throws Exception {
        return service.getSmsCode(token, type, mobile, minutes, length);
    }

    /**
     * 验证短信验证码
     *
     * @param token   访问令牌
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码)
     * @param mobile  手机号
     * @param code    验证码
     * @param isCheck 是否检验模式(true:检验模式,验证后验证码不失效;false:验证模式,验证后验证码失效)
     * @return Reply
     * 正常：根据验证结果返回接口调用成功或验证码错误的错误
     */
    @DeleteMapping("/v1.1/smscodes/{type}/{mobile}/{code}")
    public Reply verifySmsCode(@RequestHeader("Authorization") String token, @PathVariable("type") int type,
                               @PathVariable("mobile") String mobile, @PathVariable("code") String code,
                               @RequestParam(value = "check", defaultValue = "false") Boolean isCheck) throws Exception {
        return service.verifySmsCode(token, type, mobile, code, isCheck);
    }

}
