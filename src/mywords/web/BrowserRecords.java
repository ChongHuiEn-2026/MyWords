package mywords.web;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.storage.FileManager;

/** Supplemental browser fields. FileManager remains the only writer of application files. */
final class BrowserRecords {
    static final int MAX_RECORDS = 620;
    static final int WIDTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();
    private final FileManager files;
    private String[][] records = new String[MAX_RECORDS][];
    private int count;

    BrowserRecords(FileManager files) throws IOException, InvalidInputException, StorageFullException {
        this.files = files;
        String[][] stored = files.loadBrowserRecords();
        count = stored.length;
        System.arraycopy(stored, 0, records, 0, count);
    }

    String[] get(String type, String profileId, String id) {
        int index = index(type, profileId, id);
        return index < 0 ? null : records[index].clone();
    }

    String[] byEmail(String email) {
        for (int i = 0; i < count; i++) {
            String[] record = records[i];
            if ("ACCOUNT".equals(record[0]) && record[3].equals(email)) return record.clone();
        }
        return null;
    }

    String[][] needs(String profileId) {
        String[][] matches = new String[20][];
        int matchesCount = 0;
        for (int i = 0; i < count; i++) {
            String[] record = records[i];
            if ("NEED".equals(record[0]) && record[1].equals(profileId) && matchesCount < matches.length) {
                matches[matchesCount++] = record.clone();
            }
        }
        return java.util.Arrays.copyOf(matches, matchesCount);
    }

    void put(String... values) throws IOException, StorageFullException {
        int index = index(values[0], values[1], values[2]);
        if (index < 0 && count == MAX_RECORDS) {
            throw new StorageFullException("Browser settings storage is full.");
        }
        int nextCount = count + (index < 0 ? 1 : 0);
        String[][] next = records.clone();
        String[] row = new String[WIDTH];
        java.util.Arrays.fill(row, "");
        System.arraycopy(values, 0, row, 0, values.length);
        next[index < 0 ? count : index] = row;
        files.saveBrowserRecords(java.util.Arrays.copyOf(next, nextCount));
        records = next;
        count = nextCount;
    }

    void remove(String type, String profileId, String id) throws IOException {
        int index = index(type, profileId, id);
        if (index < 0) return;
        String[][] next = records.clone();
        System.arraycopy(records, index + 1, next, index, count - index - 1);
        next[count - 1] = null;
        files.saveBrowserRecords(java.util.Arrays.copyOf(next, count - 1));
        records = next;
        count--;
    }

    private int index(String type, String profileId, String id) {
        for (int i = 0; i < count; i++) {
            String[] record = records[i];
            if (type.equals(record[0]) && profileId.equals(record[1]) && id.equals(record[2])) return i;
        }
        return -1;
    }

    static String token() {
        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String passwordHash(String password) throws GeneralSecurityException {
        String salt = token();
        return salt + ":" + Base64.getUrlEncoder().encodeToString(derive(password, salt));
    }

    static boolean passwordMatches(String password, String stored) throws GeneralSecurityException {
        int separator = stored.indexOf(':');
        if (separator < 0 || password.length() > 120) return false;
        byte[] actual = derive(password, stored.substring(0, separator));
        byte[] expected = Base64.getUrlDecoder().decode(stored.substring(separator + 1));
        return MessageDigest.isEqual(actual, expected);
    }

    private static byte[] derive(String password, String salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                Base64.getUrlDecoder().decode(salt), 600_000, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }
}
