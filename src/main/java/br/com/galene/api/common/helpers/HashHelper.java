package br.com.galene.api.common.helpers;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;

public final class HashHelper {

    private static final String SHA_256 = "SHA-256";
    private static final String HMAC_SHA_256 = "HmacSHA256";
    private static final HexFormat HEX = HexFormat.of();

    private HashHelper() { }

    public static String sha256Hex(String value) {
        try {
            var digest = MessageDigest.getInstance(SHA_256);
            return HEX.formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException(SHA_256, ex);
        }
    }

    public static String hmacSha256Hex(String key, String value) {
        try {
            var mac = Mac.getInstance(HMAC_SHA_256);
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
            return HEX.formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException(HMAC_SHA_256, ex);
        }
    }

    public static boolean matches(String expectedHex, String actualHex) {
        if (expectedHex == null || actualHex == null) return false;

        return MessageDigest.isEqual(expectedHex.getBytes(StandardCharsets.UTF_8), actualHex.getBytes(StandardCharsets.UTF_8));
    }
}
