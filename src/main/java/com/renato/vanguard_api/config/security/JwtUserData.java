package com.renato.vanguard_api.config.security;

import lombok.Builder;

@Builder
public record JwtUserData(Long userId, String email) {

}
