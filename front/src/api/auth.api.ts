import client from './client';
import { LoginRequest, LoginResponse, SignupRequest } from '../types/auth.types';
import { STORAGE_KEYS } from '../utils/constants';

/**
 * 인증 관련 API
 */
export const authAPI = {
  /**
   * 로그인
   * @param data - 로그인 요청 데이터 (userId, password)
   * @returns 토큰 정보 (accessToken, tokenType)
   */
  login: (data: LoginRequest) =>
    client.post<LoginResponse>('/api/auth/login', data),

  /**
   * 회원가입
   * @param data - 회원가입 요청 데이터 (userId, username, email, password)
   * @returns void (201 Created)
   */
  signup: (data: SignupRequest) =>
    client.post('/api/auth/signup', data),

  /**
   * 로그아웃
   * 로컬 스토리지에서 토큰을 제거하고 로그인 페이지로 리다이렉트
   */
  logout: () => {
    localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
    window.location.href = '/login';
  },
};
