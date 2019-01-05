package com.cetera.services;

/**
 * This service is used to get hash value for SSO links
 * Created by danni on 4/11/16.
 */
public interface HmacSha256Service {
    String hash(String input);
}
