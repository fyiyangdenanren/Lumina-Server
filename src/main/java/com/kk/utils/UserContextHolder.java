package com.kk.utils;

/**
 * 线程隔离工具类
 */
public class UserContextHolder {
    private final static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    // 新增用户线程
    public static void setUserId(String userId) {
        threadLocal.set(userId);
    }

    // 获取用户线程
    public static String getUserId() {
        return threadLocal.get();
    }

    // 删除用户线程
    public static void clear() {
        threadLocal.remove();
    }
}
