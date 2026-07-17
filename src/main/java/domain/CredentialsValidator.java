package domain;

import domain.exceptions.InvalidFieldException;

public class CredentialsValidator {
    public static void validate(String username, String password) {
        if (username == null || username.isBlank()){
            throw new InvalidFieldException("Nome de usuário é obrigatório!");
        }
        if (password == null || password.isBlank()){
            throw new InvalidFieldException("Senha é obrigatória!");
        }
    }
}