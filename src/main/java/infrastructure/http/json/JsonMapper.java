package infrastructure.http.json;

public interface JsonMapper {
    String toJson(Object object);
    <T> T fromJson(String json, Class<T> type);
}