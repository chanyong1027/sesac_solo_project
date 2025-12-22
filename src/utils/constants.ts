// API 엔드포인트
export const API_ENDPOINTS = {
  // 인증
  AUTH: {
    LOGIN: '/api/auth/login',
    SIGNUP: '/api/auth/signup',
  },
  // 공연
  PERFORMANCES: {
    LIST: '/api/performances',
    DETAIL: (id: number) => `/api/performances/${id}`,
    CALENDAR: '/api/performances/calendar',
  },
  // 리뷰
  REVIEWS: {
    LIST: '/api/reviews',
    DETAIL: (id: number) => `/api/reviews/${id}`,
    CREATE: '/api/reviews',
    DELETE: (id: number) => `/api/reviews/${id}`,
  },
  // 관심 공연
  INTERESTED_PERFORMANCES: {
    LIST: '/api/interested-performances',
    CREATE: '/api/interested-performances',
    DELETE: (performanceId: number) => `/api/interested-performances/${performanceId}`,
  },
} as const;

// 페이지네이션 기본값
export const PAGINATION = {
  DEFAULT_PAGE: 0,
  DEFAULT_SIZE: 6,
  DEFAULT_SORT: 'startDate',
} as const;

// 로컬 스토리지 키
export const STORAGE_KEYS = {
  ACCESS_TOKEN: 'accessToken',
} as const;

// 라우트 경로
export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  SIGNUP: '/signup',
  PERFORMANCES: '/performances',
  PERFORMANCE_DETAIL: (id: number) => `/performances/${id}`,
  CALENDAR: '/calendar',
  REVIEWS: '/reviews',
  REVIEW_DETAIL: (id: number) => `/reviews/${id}`,
  REVIEW_CREATE: '/reviews/create',
  REVIEW_EDIT: (id: number) => `/reviews/${id}/edit`,
  INTERESTED_PERFORMANCES: '/interested-performances',
  MY_PERFORMANCES: '/my-performances',
  FAVORITE_ARTISTS: '/favorite-artists',
  PROFILE: '/profile',
  ADMIN: '/admin',
} as const;

// 공연 상태
export const PERFORMANCE_STATUS = {
  ONGOING: '공연중',
  UPCOMING: '공연예정',
  ENDED: '공연완료',
} as const;

// 날짜 형식
export const DATE_FORMAT = {
  DISPLAY: 'yyyy.MM.dd',
  API: 'yyyy-MM-dd',
  CALENDAR: 'yyyy-MM',
  FULL: 'yyyy.MM.dd HH:mm',
} as const;

// 유효성 검사 정규식
export const VALIDATION_REGEX = {
  // 아이디: 6-12자
  USER_ID: /^.{6,12}$/,
  // 유저명: 2-8자, 한글/영문/숫자/언더스코어
  USERNAME: /^[가-힣a-zA-Z0-9_]{2,8}$/,
  // 이메일
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  // 비밀번호: 8-20자, 영문/숫자/특수문자 포함
  PASSWORD: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/,
} as const;

// 에러 메시지
export const ERROR_MESSAGES = {
  // 인증
  AUTH: {
    INVALID_CREDENTIALS: '아이디 또는 비밀번호가 올바르지 않습니다.',
    REQUIRED_LOGIN: '로그인이 필요합니다.',
    SIGNUP_FAILED: '회원가입에 실패했습니다.',
  },
  // 유효성 검사
  VALIDATION: {
    REQUIRED_FIELD: '필수 입력 항목입니다.',
    INVALID_EMAIL: '올바른 이메일 형식이 아닙니다.',
    INVALID_PASSWORD: '비밀번호는 8-20자의 영문, 숫자, 특수문자를 포함해야 합니다.',
    INVALID_USER_ID: '아이디는 6-12자로 입력해주세요.',
    INVALID_USERNAME: '유저명은 2-8자의 한글, 영문, 숫자, 언더스코어만 사용 가능합니다.',
  },
  // 네트워크
  NETWORK: {
    SERVER_ERROR: '서버 오류가 발생했습니다.',
    NETWORK_ERROR: '네트워크 연결을 확인해주세요.',
    NOT_FOUND: '요청한 리소스를 찾을 수 없습니다.',
    FORBIDDEN: '접근 권한이 없습니다.',
  },
} as const;

// 성공 메시지
export const SUCCESS_MESSAGES = {
  AUTH: {
    LOGIN_SUCCESS: '로그인 성공!',
    SIGNUP_SUCCESS: '회원가입이 완료되었습니다!',
    LOGOUT_SUCCESS: '로그아웃 되었습니다.',
  },
  REVIEW: {
    CREATE_SUCCESS: '리뷰가 작성되었습니다.',
    UPDATE_SUCCESS: '리뷰가 수정되었습니다.',
    DELETE_SUCCESS: '리뷰가 삭제되었습니다.',
  },
  INTERESTED_PERFORMANCE: {
    ADD_SUCCESS: '관심 공연에 추가되었습니다.',
    DELETE_SUCCESS: '관심 공연에서 삭제되었습니다.',
  },
} as const;
