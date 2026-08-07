package com.taskmanager.domain.security;

import com.taskmanager.domain.exceptions.InvalidFieldException;

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