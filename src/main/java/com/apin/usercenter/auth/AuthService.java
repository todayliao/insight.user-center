package com.apin.usercenter.auth;

import com.apin.util.pojo.Reply;


/**
 * @author 宣炳刚
 * @date 2017/9/7
 * @remark 用户鉴权服务接口
 */
public interface AuthService {

    /**
     * 获取Code
     *
     * @param appId   应用ID
     * @param account 用户登录账号
     * @param type    登录类型(0:密码登录、1:验证码登录)
     * @param appName 应用名称
     * @return Reply
     */
    Reply getCode(String appId, String account, int type, String appName);

    /**
     * 获取Token数据
     *
     * @param appId     应用ID
     * @param account   登录账号
     * @param signature 签名
     * @param deptId    登录部门ID
     * @return Reply
     */
    Reply getToken(String appId, String account, String signature, String deptId);

    /**
     * 刷新访问令牌过期时间
     *
     * @param token 刷新令牌字符串
     * @return Reply
     */
    Reply refreshToken(String token);

    /**
     * 用户身份验证及鉴权(需要传入function)
     *
     * @param token    访问令牌字符串
     * @param function 功能ID或URL
     * @return Reply
     */
    Reply verifyToken(String token, String function);

    /**
     * 验证支付密码
     *
     * @param token       访问令牌
     * @param payPassword 支付密码(MD5)
     * @return Reply
     */
    Reply verifyPayPassword(String token, String payPassword);

    /**
     * 获取用户导航栏
     *
     * @param token 访问令牌
     * @param appId 应用ID
     * @return Reply
     */
    Reply getNavigators(String token, String appId);

    /**
     * 获取业务模块的功能(及对用户的授权情况)
     *
     * @param token    访问令牌
     * @param moduleId 业务模块ID
     * @return Reply
     */
    Reply getModuleFunctions(String token, String moduleId);

    /**
     * 生成短信验证码
     *
     * @param token   访问令牌
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码)
     * @param mobile  手机号
     * @param minutes 验证码有效时长(分钟)
     * @param length  验证码长度
     * @return Reply
     */
    Reply getSmsCode(String token, int type, String mobile, int minutes, int length);

    /**
     * 验证短信验证码
     *
     * @param token   访问令牌
     * @param type    验证码类型(0:验证手机号;1:注册用户账号;2:重置密码;3:修改支付密码;4:登录验证码)
     * @param mobile  手机号
     * @param code    验证码
     * @param isCheck 是否检验模式(true:检验模式,验证后验证码不失效;false:验证模式,验证后验证码失效)
     * @return Reply
     */
    Reply verifySmsCode(String token, int type, String mobile, String code, Boolean isCheck);

}
