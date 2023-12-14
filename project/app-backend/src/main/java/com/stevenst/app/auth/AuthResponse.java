package com.stevenst.app.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
	private String token;
	private String error;

	public static class AuthenticationResponseBuilder {
        private String token;
        private String error;

        public AuthenticationResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthenticationResponseBuilder error(String error) {
            this.error = error;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(this.token, this.error);
        }
    }
}
