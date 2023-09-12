package com.glimmer.context;

/**
 * 全局上下文，在这里可以存入登录用户的id、角色role，可以在其他时候获取
 */
public class BaseContext {

    public static ThreadLocal<Integer> threadLocal1 = new ThreadLocal<>();
    public static ThreadLocal<Integer> threadLocal2 = new ThreadLocal<>();

    public static void setCurrentId(Integer id) {
        threadLocal1.set(id);
    }

    public static Integer getCurrentId() {
        return threadLocal1.get();
    }

    public static void removeCurrentId() {
        threadLocal1.remove();
    }

    public static void setCurrentRole(Integer role) {
        threadLocal2.set(role);
    }

    public static Integer getCurrentRole() {
        return threadLocal2.get();
    }

    public static void removeCurrentRole() {
        threadLocal2.remove();
    }
}
