package com.compete.mis.util;

import com.compete.mis.models.viewmodels.Result;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.UncategorizedSQLException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.util.*;

public final class Global {

    private static final String symmetricEncryptionName = "AES/CBC/PKCS5Padding";

    private static final String encryptionName = "RSA";

    private static final String transformation = encryptionName + "/ECB/OAEPPadding";// + "/ECB/OAEPWithSHA3-512AndMGF1Padding";

    private static KeyPair keyPair;

    private static Key secretKey;

    private static Key privateSecretKey;

    private static String localPath;

    private Global() {
    }

    static {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(encryptionName);
            keyPairGenerator.initialize(4096);
            keyPair = keyPairGenerator.generateKeyPair();

//            KeyFactory keyFactory = KeyFactory.getInstance(encryptionName);
//            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(new byte[] {
//                    48, -126, 2, 118, 2, 1, 0, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 4, -126, 2, 96,
//                    48, -126, 2, 92, 2, 1, 0, 2, -127, -127, 0, -96, 123, 30, 48, 58, -53, 94, -85, -119, -121, 93, 46,
//                    35, -7, 83, 122, -43, 46, -47, -17, -45, 88, -101, 118, -126, -70, -95, 73, 69, 38, 114, 19, 22, 71,
//                    23, 31, -114, -106, 65, 0, -15, -37, -92, -48, 51, 118, -120, 91, -88, -47, 60, 4, 50, -43, -50,
//                    -35, -123, 29, 98, 59, 83, 61, 50, 31, 81, 63, 58, -119, -69, -121, 60, 54, 123, 20, -86, -13, 28,
//                    -92, -16, -12, -86, -38, -25, 61, 105, -10, -61, 102, -8, -114, -25, -79, -55, -91, 127, -114, 63,
//                    110, 22, 100, -53, -37, -122, -77, -80, 48, -107, 87, -77, 36, -95, -31, -120, -34, -24, -54, 77,
//                    49, -54, -105, 7, 100, -96, -36, -118, -113, 36, -81, 2, 3, 1, 0, 1, 2, -127, -128, 107, 111, 64,
//                    -36, -124, -33, -43, 54, -3, 81, 19, -66, 105, -14, -26, 66, 27, 41, -58, -3, -56, -3, 78, -58, 93,
//                    -22, 87, 67, 87, -7, -50, -16, -100, 63, 103, 105, -122, -23, 108, 7, 23, -29, 53, 72, -12, -54,
//                    -21, -86, -127, 47, -31, -1, -27, -112, 97, 118, -57, 64, -50, -100, 108, -77, 81, 51, -70, 14, -31,
//                    40, -55, -89, -32, 125, -111, -88, -116, -8, -39, 82, -33, -18, 30, 30, -112, 15, 123, 89, 110, 9,
//                    -4, -120, -91, -29, -29, 48, -70, -114, 63, -8, -99, -22, -7, -90, 12, 42, 118, -122, -118, 124,
//                    110, 92, -83, -41, -111, -39, 18, -10, 45, 29, 127, 67, -112, -104, -93, 68, 91, -38, 114, -15,
//                    2, 65, 0, -40, -60, 49, -123, 94, -40, 14, 127, -46, -99, 28, -50, -52, -122, -4, 20, 100, 17, 75,
//                    -98, 61, -125, 51, -70, -126, 83, -47, 99, -88, 56, -50, -67, -77, 28, 47, 1, 74, 61, 0, -40, 23,
//                    9, -127, -43, -48, 68, 24, 42, 30, 72, -82, 45, -57, 81, -36, -99, 85, -110, -30, 92, 33, -117, 95,
//                    43, 2, 65, 0, -67, -122, -12, 99, 5, -58, -118, 101, -20, -9, -54, 113, 111, -45, 79, 46, -57, 98,
//                    114, 43, -94, 115, 111, -71, -88, 50, 117, -17, -103, 89, -16, 86, -41, 69, 23, 81, -95, 120, -59,
//                    -80, 67, -86, 102, -22, -45, -33, -3, 0, 51, -35, 12, 82, 95, 105, -68, 95, 98, -54, 85, -86, -120,
//                    22, 46, -115, 2, 65, 0, -120, 76, 20, -73, 63, -61, -86, -102, 68, -37, -11, 89, 50, -47, 77, 127,
//                    -92, -52, 27, 56, 12, -123, -118, -23, 35, 57, -128, -41, -96, -42, -41, 104, 0, 79, 32, 95, 55, 70,
//                    -44, 89, 11, 99, 54, -8, 50, 84, 30, -67, -100, -3, 38, 72, 50, -92, 14, -117, -1, 90, -76, 106, 41,
//                    -51, 112, -79, 2, 64, 97, -41, -114, -56, -92, 13, -88, 27, 16, 103, -89, -86, 66, 41, -101, -126,
//                    49, -54, 127, -40, 72, -24, 65, 48, 94, 69, -106, -98, -41, -45, 23, -127, 45, 102, -20, -81, -26,
//                    -91, 59, -95, 80, 125, 98, -34, 45, -3, 63, 86, 96, 111, -24, 81, -118, 18, 66, -2, 23, 57, 15, 90,
//                    -105, 32, -92, 109, 2, 64, 79, -14, -70, -30, -124, -14, -67, 26, 70, 86, 96, 50, -10, -80, 25, 21,
//                    -67, -57, 80, -68, -74, -82, 110, -18, 15, -93, 118, 41, 22, 49, 16, -97, 35, 77, 105, -17, 57, -24,
//                    -12, -76, 126, -55, -125, -81, 49, 71, -115, 96, -49, 102, 68, -38, 26, -24, -39, -77, 110, -109,
//                    44, 73, -14, -106, -78, -52
//            }));
//            publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(new byte[] {
//                    48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127, -115, 0, 48, -127,
//                    -119, 2, -127, -127, 0, -96, 123, 30, 48, 58, -53, 94, -85, -119, -121, 93, 46, 35, -7, 83, 122,
//                    -43, 46, -47, -17, -45, 88, -101, 118, -126, -70, -95, 73, 69, 38, 114, 19, 22, 71, 23, 31, -114,
//                    -106, 65, 0, -15, -37, -92, -48, 51, 118, -120, 91, -88, -47, 60, 4, 50, -43, -50, -35, -123, 29,
//                    98, 59, 83, 61, 50, 31, 81, 63, 58, -119, -69, -121, 60, 54, 123, 20, -86, -13, 28, -92, -16, -12,
//                    -86, -38, -25, 61, 105, -10, -61, 102, -8, -114, -25, -79, -55, -91, 127, -114, 63, 110, 22, 100,
//                    -53, -37, -122, -77, -80, 48, -107, 87, -77, 36, -95, -31, -120, -34, -24, -54, 77, 49, -54, -105,
//                    7, 100, -96, -36, -118, -113, 36, -81, 2, 3, 1, 0, 1
//            }));

//            byte[] key = new byte[32];
//            Random random = new Random();
//            for (int i = 0; i < key.length; i++)
//                key[i] = (byte)(random.nextInt(256) - 128);
//
//            secretKey = new SecretKeySpec(SecureRandom.getInstanceStrong().generateSeed(32), "AES");
            secretKey = new SecretKeySpec(new byte[] {
                    -15, -5, -59, -17, -63, -27, -29, -15, -57, -45, -33, -23, -15, -27, -33, -57, -63, -23, -27, -27, -57, -33, -63, -23, -59, -63, -59, -45, -15, -59, -23, -27
            }, "AES");

            // 创建AES密钥生成器（256位）
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // 可改为128或192位
            privateSecretKey = keyGen.generateKey();

            Properties properties = new Properties();
            // 读取配置文件。
            try (InputStream stream = (new ClassPathResource("/application.properties")).getInputStream()) {
                properties.load(stream);
            }
            localPath = properties.getProperty("localPath", "./");
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String getLocalPath() {
        return localPath;
    }

    public static final String getLocalPath(final String path) {
        return "%s%s".formatted(localPath, path);
    }

    public static final OAEPParameterSpec oaepParams = new OAEPParameterSpec(
            "SHA3-512",
            "MGF1",
            MGF1ParameterSpec.SHA3_512,
//            new MGF1ParameterSpec("SHA3-512"),
            PSource.PSpecified.DEFAULT
    );

    public static byte[] encrypt(final byte[] plaintext)
            throws BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());//, oaepParams
        return cipher.doFinal(plaintext);
    }

    public static byte[] decrypt(final byte[] ciphertext)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());//, oaepParams
        return cipher.doFinal(ciphertext);
    }

    public static byte[] getPublic() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        // 获取公钥DER编码字节数组
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();

        // Base64编码并添加PEM头尾标记
        String base64Key = Base64.getEncoder().encodeToString(publicKeyBytes);
        StringBuilder pem = new StringBuilder("-----BEGIN PUBLIC KEY-----\n");

        int length = base64Key.length();
        // 按64字符分行
        for (int i = 0; i < length; i += 64)
            pem.append(base64Key, i, Math.min(i + 64, length))
                    .append("\n");

        pem.append("-----END PUBLIC KEY-----\n");

        return symmetricEncrypt(pem.toString().getBytes(StandardCharsets.UTF_8));
//        return symmetricEncrypt(publicKeyBytes);
    }

    private static byte[] symmetricEncrypt(final byte[] plaintext, Key key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] iv = SecureRandom.getInstanceStrong().generateSeed(16);
        Cipher cipher = Cipher.getInstance(symmetricEncryptionName);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
//        System.out.println("plaintext字节数组（内容）"+Arrays.toString(plaintext));
//        System.out.println("plaintext字节数组（长度"+plaintext.length);
//        System.out.println("iv字节数组（内容）"+Arrays.toString(iv));
//        System.out.println("iv字节数组（长度"+iv.length);
        return merge(iv, cipher.doFinal(plaintext));
    }

    private static byte[] symmetricDecrypt(final byte[] ciphertext, Key key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] iv = new byte[16];
        System.arraycopy(ciphertext, 0, iv, 0, 16);
        byte[] data = new byte[ciphertext.length - 16];
        System.arraycopy(ciphertext, 16, data, 0, data.length);
        Cipher cipher = Cipher.getInstance(symmetricEncryptionName);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(data);
    }

    public static byte[] privateSymmetricEncrypt(final byte[] plaintext)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return symmetricEncrypt(plaintext, privateSecretKey);
    }

    public static byte[] privateSymmetricDecrypt(final byte[] ciphertext)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        return symmetricDecrypt(ciphertext, privateSecretKey);
    }

    public static byte[] symmetricEncrypt(final byte[] plaintext)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return symmetricEncrypt(plaintext, secretKey);
    }

    public static byte[] symmetricDecrypt(final byte[] ciphertext)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        return symmetricDecrypt(ciphertext, secretKey);
    }

//    public static byte[] aesEncrypt(byte[] data, byte[] key) throws Exception {
//        // 生成随机IV
//        byte[] iv = new byte[16];
//        new SecureRandom().nextBytes(iv);
//
//        // 设置密钥和IV
//        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
//        IvParameterSpec ivSpec = new IvParameterSpec(iv);
//
//        // 加密处理
//        Cipher cipher = Cipher.getInstance(ALGORITHM);
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
//        byte[] encrypted = cipher.doFinal(data);
//
//        // 合并IV和密文
//        byte[] result = new byte[iv.length + encrypted.length];
//        System.arraycopy(iv, 0, result, 0, iv.length);
//        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
//        return result;
//    }
//
//    public static byte[] aesDecrypt(byte[] encryptedData, byte[] key) throws Exception {
//        // 分离IV和密文
//        byte[] iv = Arrays.copyOfRange(encryptedData, 0, 16);
//        byte[] cipherText = Arrays.copyOfRange(encryptedData, 16, encryptedData.length);
//
//        // 设置密钥和IV
//        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
//        IvParameterSpec ivSpec = new IvParameterSpec(iv);
//
//        // 解密处理
//        Cipher cipher = Cipher.getInstance(ALGORITHM);
//        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
//        return cipher.doFinal(cipherText);
//    }

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

//    private static final byte[] zero =  ByteBuffer.allocate(Long.BYTES).putLong(0L).array();

//    public static byte[] Convert(final long value) {
////        return merge(ByteBuffer.allocate(Long.BYTES).putLong(value).array(), zero);
//        return ByteBuffer.allocate(Long.BYTES).putLong(value).array();
//    }

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

    public static String encrypt(final String password, final byte[] salt) throws NoSuchAlgorithmException {
        byte[] plaintext = password.getBytes();
        for (int index = 0; index < salt.length && index < plaintext.length; index++)
            plaintext[index] += salt[index];

        return Base64.getEncoder()
                .encodeToString(merge(salt, MessageDigest.getInstance("SHA3-512").digest(plaintext)));
    }

    public static String encrypt(final String password) throws NoSuchAlgorithmException {
        UUID uuid = UUID.randomUUID();
        return encrypt(password,
                merge(Convert(uuid.getLeastSignificantBits()), Convert(uuid.getMostSignificantBits())));
    }

    public static boolean verify(final String password, final String ciphertext) {
        try {
            return encrypt(password, split(Base64.getDecoder().decode(ciphertext), 0, 16))
                    .equals(ciphertext);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    public static Map<String, ?> merge(final Map<String, ?> ... maps) {
        int length = maps.length;

        if (1 == length)
            return maps[0];

        Map<String, Object> result = new HashMap<>();
        if (0 == length)
            return result;

        List<Map<String, ?>> mapList = new ArrayList<>();
        for (Map<String, ?> item : maps)
            if (null != item)
                mapList.add(item);

        length = mapList.size();

        if (1 == length)
            return maps[0];

        if (0 == length)
            return result;

        for (Map<String, ?> item : mapList)
            for (Map.Entry<String, ?> entry : item.entrySet())
                if (!result.containsKey(entry.getKey()))
                    result.put(entry.getKey(), entry.getValue());

        return result;
    }

    public static void extractMessage(UncategorizedSQLException e, Result result) {
        String message = e.getMessage().replace(e.getSql(), "");
        int index = message.indexOf("SQL state [") + 12;
        result.setErrorNo(-Integer.parseInt(message.substring(index, index + 4)));
        index = message.indexOf("ERROR: ") + 7;
        result.setMessage(message.substring(index, message.indexOf("\n")));
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
