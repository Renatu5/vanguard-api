package com.renato.vanguard_api.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record RegisterUserRequest(@NotEmpty(message = "nome é obrigatório") String name,
                @NotEmpty(message = "email é obrigatório") @NotEmpty(message = "email é obrigatório") String email,
                @NotEmpty(message = "senha é obrigatória") String password) {

}
