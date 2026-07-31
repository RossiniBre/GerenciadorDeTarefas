package domain.assistant;

public final class EnumParser {

    private EnumParser() {}

    public static <E extends Enum<E>> E parse(Class<E> enumClass, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}