import { create } from 'zustand';
import { STORAGE_KEYS } from '../utils/constants';
import { getUsernameFromToken } from '../utils/jwtDecode';

interface AuthState {
  isAuthenticated: boolean;
  token: string | null;
  username: string | null;
  setAuth: (token: string) => void;
  clearAuth: () => void;
}

/**
 * 인증 상태 관리 Zustand 스토어
 * - 로그인/로그아웃 상태 관리
 * - 토큰 저장 및 삭제
 * - 사용자 이름 관리
 */
export const useAuthStore = create<AuthState>((set) => {
  const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
  const username = token ? getUsernameFromToken(token) : null;

  return {
    // 초기 상태: localStorage에서 토큰 확인
    isAuthenticated: !!token,
    token,
    username,

    // 로그인 시 토큰 저장
    setAuth: (token) => {
      const username = getUsernameFromToken(token);
      set({ isAuthenticated: true, token, username });
    },

    // 로그아웃 시 토큰 제거
    clearAuth: () => {
      localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
      set({ isAuthenticated: false, token: null, username: null });
    },
  };
});
