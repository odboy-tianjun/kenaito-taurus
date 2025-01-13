package me.zhengjie.util;

import org.junit.jupiter.api.Test;

import static me.zhengjie.util.EncryptUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncryptUtilTest {

    /**
     * 对称加密
     */
    @Test
    public void testDesEncrypt() {
        try {
            assertEquals("7772841DC6099402", desEncrypt("123456"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对称解密
     */
    @Test
    public void testDesDecrypt() {
        try {
            assertEquals("123456", desDecrypt("7772841DC6099402"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
