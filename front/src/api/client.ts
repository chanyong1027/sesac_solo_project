import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios';
// import { toast } from 'react-hot-toast';
import { STORAGE_KEYS, ERROR_MESSAGES } from '../utils/constants';

// Axios 인스턴스 생성
const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: 토큰 자동 첨부
client.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: 에러 처리
client.interceptors.response.use(
  (response) => response,
  (error: AxiosError<{ message: string; status: number }>) => {
    if (error.response) {
      const { status } = error.response;

      switch (status) {
        case 401:
          // 인증 실패 - 토큰 제거 및 로그인 페이지로 리다이렉트
          localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
          window.location.href = '/login';
          // toast.error(ERROR_MESSAGES.AUTH.REQUIRED_LOGIN);
          break;

        case 403:
          // 권한 없음
          // toast.error(ERROR_MESSAGES.NETWORK.FORBIDDEN);
          break;

        case 404:
          // 리소스를 찾을 수 없음
          // toast.error(ERROR_MESSAGES.NETWORK.NOT_FOUND);
          break;

        case 500:
          // 서버 오류
          // toast.error(ERROR_MESSAGES.NETWORK.SERVER_ERROR);
          break;

        default:
          // 기타 에러 - 서버에서 제공한 메시지 또는 기본 메시지
          // toast.error(data?.message || ERROR_MESSAGES.NETWORK.SERVER_ERROR);
          break;
      }
    } else if (error.request) {
      // 요청은 보냈지만 응답을 받지 못함 (네트워크 에러)
      // toast.error(ERROR_MESSAGES.NETWORK.NETWORK_ERROR);
    } else {
      // 요청 설정 중 에러 발생
      // toast.error(ERROR_MESSAGES.NETWORK.SERVER_ERROR);
    }

    return Promise.reject(error);
  }
);

export default client;
