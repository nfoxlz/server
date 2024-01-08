package com.compete.mis.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class CommonUtils {
    /**
     * 租户令牌。（保存在Cookies中的名称。）
     */
    private static final String TenantToken = "TENANT_TOKEN";

    /**
     * 用户令牌。（保存在Cookies中的名称。）
     */
    private static final String UserToken = "USER_TOKEN";

    /**
     * 构造函数。私有，不能实例化。
     */
    private CommonUtils() {
    }

//    private static byte[] Convert(long value) {
//        byte[] result = new byte[8];
//        result[0] = (byte) (value & 0xFF);
//        result[1] = (byte) (value >> 8 & 0xFF);
//        result[2] = (byte) (value >> 16 & 0xFF);
//        result[3] = (byte) (value >> 24 & 0xFF);
//        result[4] = (byte) (value >> 32 & 0xFF);
//        result[5] = (byte) (value >> 40 & 0xFF);
//        result[6] = (byte) (value >> 48 & 0xFF);
//        result[7] = (byte) (value >> 56 & 0xFF);
//        return result;
//    }

    private static long Convert(final byte[] values) {
        long result = 0L;
        int index = 0;
        for (byte value: values) {
            result += value << 8 * index;
            index++;
        }
        return result;
    }

    public static void saveCookie(HttpServletResponse response, final long tenantId, final long userId)
            throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {

        // 租户存入Cookies。
        Cookie tenantCookie = new Cookie(TenantToken, Base64.getEncoder().encodeToString(Global.Encrypt(Global.Convert(tenantId))));
        tenantCookie.setPath("/");
        response.addCookie(tenantCookie);

        // 用户存入Cookies。
        Cookie userCookie = new Cookie(UserToken, Base64.getEncoder().encodeToString(Global.Encrypt(Global.Convert(userId))));
        userCookie.setPath("/");
        response.addCookie(userCookie);
    }

    private static long loadCookie(final Cookie cookie)
            throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return Convert(Global.Decrypt(Base64.getDecoder().decode(cookie.getValue())));
    }

    public static boolean loadCookie(HttpServletRequest request, ReferenceValue<Long> tenantId, ReferenceValue<Long> userId) {
        Cookie[] cookies = request.getCookies();    // 取得Cookies。
        if (null == cookies || 0 == cookies.length)
            return false;

        boolean hasTenant = false, hasUser = false;
        try {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(TenantToken)) {
                    tenantId.set(loadCookie(cookie));   // 取得租户内码。
                    hasTenant = true;
                }
                else if (cookie.getName().equals(UserToken)) {
                    userId.set(loadCookie(cookie));     // 取得用户内码。
                    hasUser = true;
                }
            }
        } catch (IllegalBlockSizeException
                 | NoSuchPaddingException
                 | BadPaddingException
                 | NoSuchAlgorithmException
                 | InvalidKeyException e) {
            return false;
        }

        return hasTenant && hasUser;
    }
}
