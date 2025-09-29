import axios from 'axios'

const STATS_BASE_URL = process.env.NEXT_PUBLIC_STATS_URL || 'http://localhost:8084/api/v1/statistics'

// Match the StatisticsDto from the backend
interface ActivityResponseDto {
  targetName: string
  type: 'CREATION' | 'UPLOAD' | 'DOWNLOAD' | 'DELETION' | 'RENAME'
  timestamp: string
}

interface StatisticsDto {
  folderCount: number
  fileCount: number
  totalSpaceUsed: number
  lastActivity: ActivityResponseDto | null
}

class StatsService {
  private api = axios.create({
    baseURL: STATS_BASE_URL,
    headers: {
      'Content-Type': 'application/json',
    },
    withCredentials: true, // Important for cookies
  })

  async getStatistics(): Promise<StatisticsDto> {
    try {
      const response = await this.api.get<StatisticsDto>('/get')
      return response.data
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 401) {
          throw new Error('Authentication required. Please log in again')
        } else if (error.response?.status === 403) {
          throw new Error('Access denied to statistics')
        } else if (error.response?.status === 500) {
          throw new Error('Server error. Please try again later')
        }
      }
      throw new Error('Failed to fetch statistics')
    }
  }
}

export const statsService = new StatsService()

