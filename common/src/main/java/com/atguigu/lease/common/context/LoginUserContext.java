package com.atguigu.lease.common.context;

import com.mysql.cj.log.Log;

public class LoginUserContext {

    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();


    public static void setLoginUser(LoginUser loginUser) {
             threadLocal.set(loginUser);
    }


    public static LoginUser getLoginUser() {

        return threadLocal.get();
    }

    public static void removeUser() {
        threadLocal.remove();
    }
}
