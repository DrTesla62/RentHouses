// src/app/models/auth.model.ts

export interface LoginRequest {
    username: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    password: string;
    role: string;
}

export interface JwtResponse {
    accessToken: string;
    tokenType: string;
}

export interface JwtAuthenticationResponse {
    accessToken: string;
    tokenType: string;
}

export interface MessageResponse {
    message: string;
}
