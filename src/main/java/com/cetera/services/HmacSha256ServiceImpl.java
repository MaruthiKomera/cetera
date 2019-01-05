package com.cetera.services;

import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by danni on 4/11/16.
 */
@Service
public class HmacSha256ServiceImpl implements HmacSha256Service {
    private static Logger logger = LoggerFactory.getLogger(HmacSha256ServiceImpl.class);

    @Value("${hmac.hash256.key}")
    private String key;

    /**
     * helper function to change string to array
     * @param s
     * @return
     */
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @Override
    public String hash(String message) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(hexStringToByteArray(key), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] result = sha256_HMAC.doFinal(message.getBytes());
            return hashEncode(result);
        } catch (Exception e){
            logger.debug("Cannot hash message. {}", e.getMessage());
            throw new PfmException("Cannot hash message.", PfmExceptionCode.HMAC_SHA256_HASH_ERROR);
        }
    }

    /**
     * helper function to get hex string from byte
     * @param bytes
     * @return
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for(byte b: bytes)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    private String hashEncode(byte[] result) {
        return bytesToHex(result).replace("-", "").toLowerCase();
    }
}
