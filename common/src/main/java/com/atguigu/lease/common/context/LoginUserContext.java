package com.atguigu.lease.common.context;

import com.mysql.cj.log.Log;

public class LoginUserContext {

    public static ThreadLocal<LoginUser> loginUser = new ThreadLocal<>();


    public static void setLoginUser(Long userId, String username) {
        LoginUser user = new LoginUser();
        user.setUserId(userId);
        user.setUsername(username);
        loginUser.set(user);
    }


    public static LoginUser getLoginUser() {
        return loginUser.get();
    }


    public static void clear() {
        loginUser.remove();
    }

}
