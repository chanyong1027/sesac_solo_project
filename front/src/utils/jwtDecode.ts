/**
 * JWT 토큰 디코딩 유틸리티
 */

interface JwtPayload {
  sub: string; // userId
  username?: string;
  email?: string;
  role?: string;
  exp?: number;
  iat?: number;
}

/**
 * JWT 토큰을 디코딩하여 payload 반환
 */
export const decodeJwt = (token: string): JwtPayload | null => {
  try {
    // JWT는 header.payload.signature 형태
    const base64Url = token.split('.')[1];
    if (!base64Url) return null;

    // Base64Url을 Base64로 변환
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');

    // Base64 디코딩
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );

    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('JWT 디코딩 실패:', error);
    return null;
  }
};

/**
 * 토큰에서 username 추출
 */
export const getUsernameFromToken = (token: string): string | null => {
  const payload = decodeJwt(token);
  return payload?.username || payload?.sub || null;
};
