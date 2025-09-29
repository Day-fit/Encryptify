'use client'

import { useState, useEffect, useCallback } from 'react'

interface Settings {
  theme: 'light' | 'dark' | 'auto'
  notifications: boolean
  emailNotifications: boolean
  autoLock: boolean
  sessionTimeout: number // in minutes
  analytics: boolean
  dataSharing: boolean
  cacheSize: number // in MB
  autoSync: boolean
}

const DEFAULT_SETTINGS: Settings = {
  theme: 'auto',
  notifications: true,
  emailNotifications: true,
  autoLock: true,
  sessionTimeout: 30,
  analytics: false,
  dataSharing: false,
  cacheSize: 500,
  autoSync: true
}

const SETTINGS_STORAGE_KEY = 'encryptify_settings'

export function useSettings() {
  const [settings, setSettings] = useState<Settings>(DEFAULT_SETTINGS)
  const [isLoading, setIsLoading] = useState(true)

  // Load settings from localStorage on mount
  useEffect(() => {
    const loadSettings = () => {
      try {
        const storedSettings = localStorage.getItem(SETTINGS_STORAGE_KEY)
        if (storedSettings) {
          const parsedSettings = JSON.parse(storedSettings)
          // Merge with defaults to handle any new settings that might be added
          setSettings({ ...DEFAULT_SETTINGS, ...parsedSettings })
        }
      } catch (error) {
        console.error('Failed to load settings from localStorage:', error)
        // Use default settings if loading fails
        setSettings(DEFAULT_SETTINGS)
      } finally {
        setIsLoading(false)
      }
    }

    loadSettings()
  }, [])

  // Save settings to localStorage whenever they change
  useEffect(() => {
    if (!isLoading) {
      try {
        localStorage.setItem(SETTINGS_STORAGE_KEY, JSON.stringify(settings))
      } catch (error) {
        console.error('Failed to save settings to localStorage:', error)
      }
    }
  }, [settings, isLoading])

  const updateSetting = useCallback(<K extends keyof Settings>(
    key: K,
    value: Settings[K]
  ) => {
    setSettings(prevSettings => ({
      ...prevSettings,
      [key]: value
    }))
  }, [])

  const updateMultipleSettings = useCallback((updates: Partial<Settings>) => {
    setSettings(prevSettings => ({
      ...prevSettings,
      ...updates
    }))
  }, [])

  const resetSettings = useCallback(() => {
    setSettings(DEFAULT_SETTINGS)
  }, [])

  const getSetting = useCallback(<K extends keyof Settings>(key: K): Settings[K] => {
    return settings[key]
  }, [settings])

  // Apply theme to document
  useEffect(() => {
    if (isLoading) return

    const applyTheme = () => {
      const root = document.documentElement
      
      if (settings.theme === 'dark') {
        root.classList.add('dark')
      } else if (settings.theme === 'light') {
        root.classList.remove('dark')
      } else { // auto
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
        if (prefersDark) {
          root.classList.add('dark')
        } else {
          root.classList.remove('dark')
        }
      }
    }

    applyTheme()

    // Listen for system theme changes when using auto theme
    if (settings.theme === 'auto') {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
      const handleChange = () => applyTheme()
      
      mediaQuery.addEventListener('change', handleChange)
      return () => mediaQuery.removeEventListener('change', handleChange)
    }
  }, [settings.theme, isLoading])

  // Handle session timeout
  useEffect(() => {
    if (isLoading || !settings.autoLock) return

    let timeoutId: NodeJS.Timeout

    const resetTimeout = () => {
      clearTimeout(timeoutId)
      timeoutId = setTimeout(() => {
        // Trigger logout or lock action
        // This would typically be handled by the auth context
        console.log('Session timeout reached')
        // You might want to emit an event or call a logout function here
      }, settings.sessionTimeout * 60 * 1000)
    }

    const handleActivity = () => {
      resetTimeout()
    }

    // Set up activity listeners
    const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click']
    events.forEach(event => {
      document.addEventListener(event, handleActivity, true)
    })

    // Initial timeout
    resetTimeout()

    return () => {
      clearTimeout(timeoutId)
      events.forEach(event => {
        document.removeEventListener(event, handleActivity, true)
      })
    }
  }, [settings.autoLock, settings.sessionTimeout, isLoading])

  return {
    settings,
    isLoading,
    updateSetting,
    updateMultipleSettings,
    resetSettings,
    getSetting
  }
}

