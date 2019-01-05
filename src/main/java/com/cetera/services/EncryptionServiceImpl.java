package com.cetera.services;

import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Created by danni on 4/5/16.
 */
@Service
public class EncryptionServiceImpl implements EncryptionService {

    private static Logger logger = LoggerFactory.getLogger(EncryptionServiceImpl.class);

    @Value("${payload.encryption.key}")
    private String keyString;

    private static final String KEY_ALGORITHM = "AES";

    private static Key KEY;

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    @PostConstruct
    public void init() throws Exception {
        byte[] bKey = hexStringToByteArray(keyString);
        KEY = new SecretKeySpec(bKey, KEY_ALGORITHM);
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for(byte b: bytes)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    @Override
    public String decrypt(String data) {
        try {
            Cipher c = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, KEY);
            byte[] decodedValue = new Base64().decode(data.getBytes("UTF-8"));
            byte[] decryptedVal = c.doFinal(decodedValue);
            return new String(decryptedVal, "UTF-8");

        } catch (NoSuchAlgorithmException
            | NoSuchPaddingException
            | IllegalBlockSizeException
            | BadPaddingException
            | InvalidKeyException
            | UnsupportedEncodingException e) {

            logger.debug("Decryption error. {}", e.getMessage());
            throw new PfmException("Decryption error.", PfmExceptionCode.CIPHER_ERROR);
        }
    }

    @Override
    public String encrypt(String data) {
        try {
            Cipher c = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, KEY);
            byte[] encValue = c.doFinal(data.getBytes("UTF-8"));
            byte[] encryptedByteValue = new Base64().encode(encValue);
            return new String(encryptedByteValue, "UTF-8");

        } catch (NoSuchAlgorithmException
            | NoSuchPaddingException
            | IllegalBlockSizeException
            | BadPaddingException
            | InvalidKeyException
            | UnsupportedEncodingException e) {

            logger.debug("Encryption error. {}", e.getMessage());
            throw new PfmException("Encryption error.", PfmExceptionCode.CIPHER_ERROR);
        }
    }
}
