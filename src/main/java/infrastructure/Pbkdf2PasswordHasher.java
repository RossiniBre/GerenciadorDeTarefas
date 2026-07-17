package infrastructure;

import domain.PasswordHasher;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Arrays;

public class Pbkdf2PasswordHasher implements PasswordHasher {

    private static final int ITERATIONS = 65536;

    @Override
    public String hash(String rawPassword) {
        //1
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        SecretKey key;

        try {
            //2
            PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, ITERATIONS, 256);

            //3
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            key = factory.generateSecret(spec);

        } catch (Exception e) {
            throw new RuntimeException("Falha ao gerar hash da senha", e);
        }

        byte[] hashBytes = key.getEncoded();

        //4
        String saltText = Base64.getEncoder().encodeToString(salt);
        String bytesText = Base64.getEncoder().encodeToString(hashBytes);

        //5
        return String.format("%d,%s,%s", ITERATIONS, saltText, bytesText);
    }

    @Override
    public boolean verify(String rawPassword, String hashedPassword) {

        String[] parties = hashedPassword.split(",");
        int savedIterations = Integer.parseInt(parties[0]);
        byte[] savedSalt = Base64.getDecoder().decode(parties[1]);
        byte[] savedHash = Base64.getDecoder().decode(parties[2]);
        SecretKey key;

        try {

            PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), savedSalt, savedIterations, 256);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            key = factory.generateSecret(spec);

        } catch (Exception e) {
            throw new RuntimeException("Falha ao verificar senha", e);
        }

        byte[] hashBytes = key.getEncoded();

        return Arrays.equals(savedHash, hashBytes);
    }
}