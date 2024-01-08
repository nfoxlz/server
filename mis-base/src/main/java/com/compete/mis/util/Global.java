package com.compete.mis.util;

import org.springframework.core.io.ClassPathResource;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public final class Global {

    private static final String encryptionName = "RSA";

    private static PrivateKey privateKey;

    private static PublicKey publicKey;

    private static String localPath;

    private Global() {
    }

    static {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(encryptionName);
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(new byte[] {
                    48, -126, 2, 118, 2, 1, 0, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 4, -126, 2, 96,
                    48, -126, 2, 92, 2, 1, 0, 2, -127, -127, 0, -96, 123, 30, 48, 58, -53, 94, -85, -119, -121, 93, 46,
                    35, -7, 83, 122, -43, 46, -47, -17, -45, 88, -101, 118, -126, -70, -95, 73, 69, 38, 114, 19, 22, 71,
                    23, 31, -114, -106, 65, 0, -15, -37, -92, -48, 51, 118, -120, 91, -88, -47, 60, 4, 50, -43, -50,
                    -35, -123, 29, 98, 59, 83, 61, 50, 31, 81, 63, 58, -119, -69, -121, 60, 54, 123, 20, -86, -13, 28,
                    -92, -16, -12, -86, -38, -25, 61, 105, -10, -61, 102, -8, -114, -25, -79, -55, -91, 127, -114, 63,
                    110, 22, 100, -53, -37, -122, -77, -80, 48, -107, 87, -77, 36, -95, -31, -120, -34, -24, -54, 77,
                    49, -54, -105, 7, 100, -96, -36, -118, -113, 36, -81, 2, 3, 1, 0, 1, 2, -127, -128, 107, 111, 64,
                    -36, -124, -33, -43, 54, -3, 81, 19, -66, 105, -14, -26, 66, 27, 41, -58, -3, -56, -3, 78, -58, 93,
                    -22, 87, 67, 87, -7, -50, -16, -100, 63, 103, 105, -122, -23, 108, 7, 23, -29, 53, 72, -12, -54,
                    -21, -86, -127, 47, -31, -1, -27, -112, 97, 118, -57, 64, -50, -100, 108, -77, 81, 51, -70, 14, -31,
                    40, -55, -89, -32, 125, -111, -88, -116, -8, -39, 82, -33, -18, 30, 30, -112, 15, 123, 89, 110, 9,
                    -4, -120, -91, -29, -29, 48, -70, -114, 63, -8, -99, -22, -7, -90, 12, 42, 118, -122, -118, 124,
                    110, 92, -83, -41, -111, -39, 18, -10, 45, 29, 127, 67, -112, -104, -93, 68, 91, -38, 114, -15,
                    2, 65, 0, -40, -60, 49, -123, 94, -40, 14, 127, -46, -99, 28, -50, -52, -122, -4, 20, 100, 17, 75,
                    -98, 61, -125, 51, -70, -126, 83, -47, 99, -88, 56, -50, -67, -77, 28, 47, 1, 74, 61, 0, -40, 23,
                    9, -127, -43, -48, 68, 24, 42, 30, 72, -82, 45, -57, 81, -36, -99, 85, -110, -30, 92, 33, -117, 95,
                    43, 2, 65, 0, -67, -122, -12, 99, 5, -58, -118, 101, -20, -9, -54, 113, 111, -45, 79, 46, -57, 98,
                    114, 43, -94, 115, 111, -71, -88, 50, 117, -17, -103, 89, -16, 86, -41, 69, 23, 81, -95, 120, -59,
                    -80, 67, -86, 102, -22, -45, -33, -3, 0, 51, -35, 12, 82, 95, 105, -68, 95, 98, -54, 85, -86, -120,
                    22, 46, -115, 2, 65, 0, -120, 76, 20, -73, 63, -61, -86, -102, 68, -37, -11, 89, 50, -47, 77, 127,
                    -92, -52, 27, 56, 12, -123, -118, -23, 35, 57, -128, -41, -96, -42, -41, 104, 0, 79, 32, 95, 55, 70,
                    -44, 89, 11, 99, 54, -8, 50, 84, 30, -67, -100, -3, 38, 72, 50, -92, 14, -117, -1, 90, -76, 106, 41,
                    -51, 112, -79, 2, 64, 97, -41, -114, -56, -92, 13, -88, 27, 16, 103, -89, -86, 66, 41, -101, -126,
                    49, -54, 127, -40, 72, -24, 65, 48, 94, 69, -106, -98, -41, -45, 23, -127, 45, 102, -20, -81, -26,
                    -91, 59, -95, 80, 125, 98, -34, 45, -3, 63, 86, 96, 111, -24, 81, -118, 18, 66, -2, 23, 57, 15, 90,
                    -105, 32, -92, 109, 2, 64, 79, -14, -70, -30, -124, -14, -67, 26, 70, 86, 96, 50, -10, -80, 25, 21,
                    -67, -57, 80, -68, -74, -82, 110, -18, 15, -93, 118, 41, 22, 49, 16, -97, 35, 77, 105, -17, 57, -24,
                    -12, -76, 126, -55, -125, -81, 49, 71, -115, 96, -49, 102, 68, -38, 26, -24, -39, -77, 110, -109,
                    44, 73, -14, -106, -78, -52
            }));
            publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(new byte[] {
                    48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127, -115, 0, 48, -127,
                    -119, 2, -127, -127, 0, -96, 123, 30, 48, 58, -53, 94, -85, -119, -121, 93, 46, 35, -7, 83, 122,
                    -43, 46, -47, -17, -45, 88, -101, 118, -126, -70, -95, 73, 69, 38, 114, 19, 22, 71, 23, 31, -114,
                    -106, 65, 0, -15, -37, -92, -48, 51, 118, -120, 91, -88, -47, 60, 4, 50, -43, -50, -35, -123, 29,
                    98, 59, 83, 61, 50, 31, 81, 63, 58, -119, -69, -121, 60, 54, 123, 20, -86, -13, 28, -92, -16, -12,
                    -86, -38, -25, 61, 105, -10, -61, 102, -8, -114, -25, -79, -55, -91, 127, -114, 63, 110, 22, 100,
                    -53, -37, -122, -77, -80, 48, -107, 87, -77, 36, -95, -31, -120, -34, -24, -54, 77, 49, -54, -105,
                    7, 100, -96, -36, -118, -113, 36, -81, 2, 3, 1, 0, 1
            }));

            Properties properties = new Properties();
            // 读取配置文件。
            try (InputStream stream = (new ClassPathResource("/application.properties")).getInputStream()) {
                properties.load(stream);
            }
            localPath = properties.getProperty("localPath", "./");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String getLocalPath() {
        return localPath;
    }

    public static final String getLocalPath(final String path) {
        return "%s%s".formatted(localPath, path);
    }

    public static byte[] Encrypt(final byte[] plaintext)
            throws BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(encryptionName);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext);
    }

    public static byte[] Decrypt(final byte[] ciphertext)
            throws BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(encryptionName);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(ciphertext);
    }

    public static byte[] Convert(final long value) {
        byte[] result = new byte[8];
        result[0] = (byte) (value & 0xFF);
        result[1] = (byte) (value >> 8 & 0xFF);
        result[2] = (byte) (value >> 16 & 0xFF);
        result[3] = (byte) (value >> 24 & 0xFF);
        result[4] = (byte) (value >> 32 & 0xFF);
        result[5] = (byte) (value >> 40 & 0xFF);
        result[6] = (byte) (value >> 48 & 0xFF);
        result[7] = (byte) (value >> 56 & 0xFF);
        return result;
    }

    private static byte[] merge(final byte[] first, final byte[] second) {
        int firstLength = first.length;
        int secondLength = second.length;
        byte[] result = new byte[firstLength + secondLength];

        for (int index = 0; index < firstLength; index++)
            result[index] = first[index];
        for (int index = 0; index < secondLength; index++)
            result[index + firstLength] = second[index];
        return result;
    }

    private static byte[] split(final byte[] source, final int beginIndex, final int length) {
        if (beginIndex + length > source.length)
            throw new ArrayIndexOutOfBoundsException();

        byte[] result = new byte[length];
        for (int index = 0; index < length; index++)
            result[index] = source[beginIndex + index];
        return result;
    }

    public static String Encrypt(final String password, final byte[] salt) throws NoSuchAlgorithmException {
        byte[] plaintext = password.getBytes();
        for (int index = 0; index < salt.length && index < plaintext.length; index++)
            plaintext[index] += salt[index];

        return Base64.getEncoder()
                .encodeToString(merge(salt, MessageDigest.getInstance("MD5").digest(plaintext)));
    }

    public static String Encrypt(final String password) throws NoSuchAlgorithmException {
        UUID uuid = UUID.randomUUID();
        return Encrypt(password,
                merge(Convert(uuid.getLeastSignificantBits()), Convert(uuid.getMostSignificantBits())));
    }

    public static boolean verify(final String password, final String ciphertext) {
        try {
            return Encrypt(password, split(Base64.getDecoder().decode(ciphertext), 0, 16))
                    .equals(ciphertext);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    public static Map<String, Object> merge(final Map<String, Object> ... maps) {
        int length = maps.length;

        if (1 == length)
            return maps[0];

        Map<String, Object> result = new HashMap<>();
        if (0 == length)
            return result;

        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Map<String, Object> item : maps)
            if (null != item)
                mapList.add(item);

        length = mapList.size();

        if (1 == length)
            return maps[0];

        if (0 == length)
            return result;

        for (Map<String, Object> item : mapList)
            for (Map.Entry<String, Object> entry : item.entrySet())
                if (!result.containsKey(entry.getKey()))
                    result.put(entry.getKey(), entry.getValue());

        return result;
    }

//    public static String format(final String formatString, final Object ... arguments) {
//        int length = arguments.length;
//        if (0 == length)
//            return formatString;
//
//        String result = formatString;
//        for (int index = 0; index < length; index++)
//            if (arguments[index] != null)
//                result = result.replace("{" + index + "}", arguments[index].toString());
//
//        return result;
//    }

//    public static String format(final String formatString, final Map<String, String> arguments) {
//        if (null == arguments || 0 = arguments.size())
//            return formatString;
//
//        String result = formatString;
//        for (Map.Entry<String, String> entry : arguments.entrySet())
//            result = result.replace("{" + entry.getKey() + "}", entry.getValue().toString());
//
//        return result;
//    }
}
