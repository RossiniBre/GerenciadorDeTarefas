package infrastructure.http.dto;

public class RegisterUserResponse {
    public String id;
    public String username;

    public RegisterUserResponse(String id, String username) {
        this.id = id;
        this.username = username;
    }
}