import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
// import { toast } from 'react-hot-toast';
import { authAPI } from '../api/auth.api';
import { LoginRequest, SignupRequest } from '../types/auth.types';
import { useAuthStore } from '../store/authStore';
import { STORAGE_KEYS, SUCCESS_MESSAGES, ROUTES } from '../utils/constants';

/**
 * 로그인 Hook
 * - 로그인 API 호출
 * - 성공 시 토큰 저장 및 홈으로 이동
 */
export const useLogin = () => {
  const navigate = useNavigate();
  const setAuth = useAuthStore((state) => state.setAuth);

  return useMutation({
    mutationFn: (data: LoginRequest) => authAPI.login(data),
    onSuccess: (response) => {
      const { accessToken } = response.data;
      // 로컬 스토리지에 토큰 저장
      localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken);
      // Zustand 스토어 업데이트
      setAuth(accessToken);
      // toast.success(SUCCESS_MESSAGES.AUTH.LOGIN_SUCCESS);
      navigate(ROUTES.HOME);
    },
    onError: () => {
      // toast.error('ID/비밀번호 정보가 일치하지 않습니다.');
    },
  });
};

/**
 * 회원가입 Hook
 * - 회원가입 API 호출
 * - 성공 시 로그인 페이지로 이동
 */
export const useSignup = () => {
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (data: SignupRequest) => authAPI.signup(data),
    onSuccess: () => {
      // toast.success(SUCCESS_MESSAGES.AUTH.SIGNUP_SUCCESS);
      navigate(ROUTES.LOGIN);
    },
    onError: () => {
      // toast.error('회원가입에 실패했습니다.');
    },
  });
};

/**
 * 로그아웃 Hook
 * - 토큰 제거 및 로그인 페이지로 이동
 */
export const useLogout = () => {
  const clearAuth = useAuthStore((state) => state.clearAuth);

  return () => {
    clearAuth();
    authAPI.logout();
    // toast.success(SUCCESS_MESSAGES.AUTH.LOGOUT_SUCCESS);
  };
};
