package io.github.hzhilong.bilibili.backup.app.utils;

import io.github.hzhilong.bilibili.backup.api.bean.Question;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AesCbcDecrypt {
    /**
     * 解密 AES-CBC/PKCS5Padding（兼容 CryptoJS AES-CBC + Pkcs7）
     *
     * @param base64Cipher Base64 编码的密文
     * @param keyUtf8      key 字符串，按 UTF-8 转字节（示例："2b72cc3d14qwe5sc481a16e13f6a249c"）
     * @param ivUtf8       iv 字符串，按 UTF-8 转字节（示例："1033567590123790"）
     * @return 解密后的明文（UTF-8）
     * @throws Exception 若解密失败抛出异常
     */
    public static String decrypt(String base64Cipher, String keyUtf8, String ivUtf8) throws Exception {
        byte[] cipherBytes = Base64.getDecoder().decode(base64Cipher);
        byte[] keyBytes = keyUtf8.getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = ivUtf8.getBytes(StandardCharsets.UTF_8);

        // 验证长度
        if (!(keyBytes.length == 16 || keyBytes.length == 24 || keyBytes.length == 32)) {
            throw new IllegalArgumentException("Key length must be 16, 24 or 32 bytes (UTF-8 bytes). Actual: " + keyBytes.length);
        }
        if (ivBytes.length != 16) {
            throw new IllegalArgumentException("IV length must be 16 bytes (UTF-8 bytes). Actual: " + ivBytes.length);
        }

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // PKCS5Padding 对应 CryptoJS 的 PKCS7
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] plainBytes = cipher.doFinal(cipherBytes);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    /**
     * 解密B站答题相关文本
     */
    public static String decryptBiliAnswer(String base64Cipher) throws Exception {
        return decrypt(base64Cipher, "2b72cc3d14qwe5sc481a16e13f6a249c", "1033567590123790");
    }

    /**
     * 解密B站答题相关文本
     */
    public static List<String> decryptBiliAnswers(List<Question.QuestionBean.AnsBean> base64Ciphers) throws Exception {
        List<String> result = new ArrayList<>();
        for (Question.QuestionBean.AnsBean ansBean : base64Ciphers) {
            result.add(decrypt(ansBean.getTitle(), "2b72cc3d14qwe5sc481a16e13f6a249c", "1033567590123790"));
        }
        return result;
    }

    public static void main(String[] args) {
        try {
            String c1 = "pfbvzhVOusArnZwdo1Xt+i/zQvEWpOGq1CQPzLTaVvyqqDPY97275V5JrT0SJW/jhP3kkFlCDVyLDxh3D6I6ng==";
            String c2 = "SqPf3MTONmzzAUBGFsSLeH4decSFgjTXpWvUD1+L/TuapOeLCLu0dzbEQGnDfZOP";

            System.out.println("dec1: " + decryptBiliAnswer(c1));
            System.out.println("dec2: " + decryptBiliAnswer(c2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
