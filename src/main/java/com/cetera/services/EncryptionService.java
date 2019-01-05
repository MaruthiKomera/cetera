package com.cetera.services;

/**
 * This service is used to encrypt and decrypt
 * Created by danni on 4/5/16.
 */
public interface EncryptionService {
    String decrypt(String data);
    String encrypt(String data);
}
