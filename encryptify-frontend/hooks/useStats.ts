'use client'

import { useState, useEffect, useCallback } from 'react'
import { statsService } from '@/services/statsService'

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

export function useStats() {
  const [statistics, setStatistics] = useState<StatisticsDto | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchStatistics = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    
    try {
      const stats = await statsService.getStatistics()
      setStatistics(stats)
    } catch (err: any) {
      setError(err.message || 'Failed to fetch statistics')
      console.error('Error fetching statistics:', err)
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchStatistics()
  }, [fetchStatistics])

  const refreshStatistics = useCallback(() => {
    fetchStatistics()
  }, [fetchStatistics])

  const formatBytes = useCallback((bytes: number): string => {
    if (bytes === 0) return '0 Bytes'
    
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }, [])

  const formatActivityType = useCallback((type: string): string => {
    switch (type) {
      case 'CREATION':
        return 'Created'
      case 'UPLOAD':
        return 'Uploaded'
      case 'DOWNLOAD':
        return 'Downloaded'
      case 'DELETION':
        return 'Deleted'
      case 'RENAME':
        return 'Renamed'
      default:
        return type
    }
  }, [])

  const formatTimestamp = useCallback((timestamp: string): string => {
    const date = new Date(timestamp)
    const now = new Date()
    const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000)
    
    if (diffInSeconds < 60) {
      return 'Just now'
    } else if (diffInSeconds < 3600) {
      const minutes = Math.floor(diffInSeconds / 60)
      return `${minutes} minute${minutes > 1 ? 's' : ''} ago`
    } else if (diffInSeconds < 86400) {
      const hours = Math.floor(diffInSeconds / 3600)
      return `${hours} hour${hours > 1 ? 's' : ''} ago`
    } else if (diffInSeconds < 604800) {
      const days = Math.floor(diffInSeconds / 86400)
      return `${days} day${days > 1 ? 's' : ''} ago`
    } else {
      return date.toLocaleDateString()
    }
  }, [])

  return {
    statistics,
    isLoading,
    error,
    refreshStatistics,
    formatBytes,
    formatActivityType,
    formatTimestamp
  }
}

