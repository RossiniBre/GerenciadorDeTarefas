package domain;

import java.util.UUID;

public class User {
    private String id;
    private String username;
    private String passwordHash;

    private User(String id, String username, String passwordHash){
        if (username == null || username.isBlank()){
            throw new IllegalArgumentException("Nome de usuário é obrigatório!");
        }
        if (passwordHash == null || passwordHash.isBlank()){
            throw new IllegalArgumentException("Senha é obrigatório!");
        }
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public static User newUser(String username,String passwordHash){
        return new User(UUID.randomUUID().toString(), username, passwordHash);
    }

    public static User rebuiltedUser(String id, String username, String passwordHash){
        return new User(id, username, passwordHash);
    }

    //getters
    public String getId(){ return id; }
    public String getPasswordHash(){ return passwordHash; }
    public String getUsername(){ return username; }

}