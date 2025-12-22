// 로그인 요청
export interface LoginRequest {
  userId: string;
  password: string;
}

// 로그인 응답 (TokenResponse)
export interface LoginResponse {
  accessToken: string;
  tokenType: string;
}

// 회원가입 요청
export interface SignupRequest {
  userId: string;
  username: string;
  email: string;
  password: string;
}

// 사용자 역할
export type Role = 'USER' | 'ADMIN';

// 사용자 정보
export interface User {
  id: number;
  userId: string;
  username: string;
  email: string;
  role: Role;
}
