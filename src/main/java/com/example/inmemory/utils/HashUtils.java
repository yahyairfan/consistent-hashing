package com.example.inmemory.utils;

import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class HashUtils {

    private final Random random = new Random();
    private final TreeSet<Long> createdKeys = new TreeSet<>();
    private static final long MIN_KEY = 1;
    private static final long MAX_KEY = 10000;

    public long generateHashKey(String value) {
        if (value != null) {
            return generateHashFromString(value);
        } else {
            return generateUniqueRandomKey();
        }
    }

    public long getNextKey(long key) {
        Long nextKey = createdKeys.higher(key);
        return nextKey != null ? nextKey : createdKeys.getFirst();
    }

    private long generateHashFromString(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            long key = Math.abs(bytesToLong(hash)) % (MAX_KEY - MIN_KEY + 1) + MIN_KEY;
            return createdKeys.contains(key) ? key : getNextKey(key);
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchMessageException(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
    }

    private Long generateUniqueRandomKey() {
        Long key;
        do {
            key = random.nextInt((int) (MAX_KEY - MIN_KEY + 1)) + MIN_KEY;
        } while (createdKeys.contains(key));
        createdKeys.add(key);
        return key;
    }

    public void removeKey(Long key) {
        createdKeys.remove(key);
    }

    private long bytesToLong(byte[] bytes) {
        long value = 0L;
        for (int i = 0; i < Math.min(8, bytes.length); i++) {
            value = (value << 8) | (bytes[i] & 0xFF);
        }
        return value;
    }
}
