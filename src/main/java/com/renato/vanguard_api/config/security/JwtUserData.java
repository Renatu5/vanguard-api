package com.renato.vanguard_api.config.security;

public record JwtUserData(Long userId, String email) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long userId;
        private String email;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public JwtUserData build() {
            return new JwtUserData(userId, email);
        }
    }
}
