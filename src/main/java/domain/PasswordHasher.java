package domain;

public interface PasswordHasher {
    String hash(String rawPassword);
    boolean verify(String rawPassword, String hashedPassword);
}
