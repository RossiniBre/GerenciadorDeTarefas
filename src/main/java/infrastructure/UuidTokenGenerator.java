package infrastructure;

import domain.TokenGenerator;
import java.util.UUID;

public class UuidTokenGenerator implements TokenGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}