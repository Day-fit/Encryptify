import axios from 'axios'

const AUTH_BASE_URL = process.env.NEXT_PUBLIC_AUTH_URL || 'http://localhost:8083/api/v1/'

interface LoginRequestDTO {
  identifier: string  // Can be username or email
  password: string
}

interface RegisterRequestDTO {
  email: string
  username: string
  password: string
}

interface AuthResponse {
  message: string
}

interface InfoResponse {
  username: string
}

class AuthService {
  private api = axios.create({
    baseURL: AUTH_BASE_URL,
    headers: {
      'Content-Type': 'application/json',
    },
    withCredentials: true, // Important for cookies
  })

  async info(): Promise<InfoResponse> {
    try{
      const response = await this.api.get<InfoResponse>('/account/info', {})
      return response.data
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 403) {
          throw new Error('Not authenticated')
        }
      }

      throw new Error('Info request failed')
    }
  }

  async login(identifier: string, password: string): Promise<AuthResponse> {
    try {
      const loginDto: LoginRequestDTO = { identifier, password }
      const response = await this.api.post<AuthResponse>('/auth/login', loginDto)
      return response.data
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 401) {
          throw new Error('Invalid username/email or password')
        } else if (error.response?.status === 400) {
          throw new Error('Invalid input data')
        } else if (error.response?.status === 500) {
          throw new Error('Server error. Please try again later')
        } else {
          throw new Error('Login failed. Please check your connection and try again')
        }
      }
      throw new Error('Login failed')
    }
  }

  async register(email: string, username: string, password: string): Promise<AuthResponse> {
    try {
      const registerDto: RegisterRequestDTO = { email, username, password }
      await this.api.post<AuthResponse>('/auth/register', registerDto)
      return { message: 'Registration successful! Please check your email for verification.' }
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 409) {
          throw new Error('Username or email already exists')
        } else if (error.response?.status === 400) {
          const errorMessage = error.response.data?.message || 'Invalid input data'
          throw new Error(errorMessage)
        } else if (error.response?.status === 500) {
          throw new Error('Server error. Please try again later')
        } else {
          throw new Error('Registration failed. Please check your connection and try again')
        }
      }
      throw new Error('Registration failed')
    }
  }

  async logout(): Promise<AuthResponse> {
    try {
      const response = await this.api.post<AuthResponse>('/auth/logout')
      return response.data
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 401) {
          throw new Error('Not authenticated')
        } else if (error.response?.status === 500) {
          throw new Error('Server error during logout')
        }
      }
      throw new Error('Logout failed')
    }
  }

  async refreshToken(): Promise<AuthResponse> {
    try {
      const response = await this.api.post<AuthResponse>('/auth/refresh')
      return response.data
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 401) {
          throw new Error('Invalid refresh token')
        } else if (error.response?.status === 500) {
          throw new Error('Server error during token refresh')
        }
      }
      throw new Error('Token refresh failed')
    }
  }
}

export const authService = new AuthService() 