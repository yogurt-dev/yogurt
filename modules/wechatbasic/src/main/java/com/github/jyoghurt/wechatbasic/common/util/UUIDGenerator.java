package com.github.jyoghurt.wechatbasic.common.util;

import java.util.UUID;

/**
 * <p>
 * Description: UUID生成器
 * </p>
 */
public final class UUIDGenerator {

    private UUIDGenerator() {
    }

    /**
     * 获得一个UUID
     *
     * @return String UUID
     */
    public static String getUUID() {
        String uuidString = UUID.randomUUID().toString();
        return uuidString;
    }

    /**
     * 获得一个32位UUID
     *
     * @return String UUID
     */
    public static String getUUID32() {
        String uuidString = UUID.randomUUID().toString().replaceAll("-", "");
        return uuidString;
    }

}
