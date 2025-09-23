'use client'

import React, { useState, useEffect, createContext, useContext, ReactNode } from 'react'
import { authService } from '@/services/authService'

interface User {
    username: string
    accountType: string[]
    registrationDate: string
}

interface AuthContextType {
    user: User | null
    login: (identifier: string, password: string) => Promise<void>
    logout: () => void
    isLoading: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null)
    const [isLoading, setIsLoading] = useState(true)

    useEffect(() => {
        // Check if user is already authenticated on mount
        const checkAuth = async () => {
            try {
                const userInfo = await authService.info()
                setUser({
                    username: userInfo.username,
                    accountType: userInfo.accountType,
                    registrationDate: userInfo.registrationDate,
                })
            } catch (error) {
                // User is not authenticated
                setUser(null)
            } finally {
                setIsLoading(false)
            }
        }
        
        checkAuth()
    }, [])

    useEffect(() => {
        if (!user) return

        const interval = setInterval(async () => {
            await authService.refreshToken()
        }, 29 * 60 * 1000)

        return () => clearInterval(interval)
    }, [user])

    const login = async (identifier: string, password: string) => {
        try {
            await authService.login(identifier, password)
            const userInfo = await authService.info()

            setUser({
                username: userInfo.username,
                accountType: userInfo.accountType,
                registrationDate: userInfo.registrationDate,
            })

        } catch (error) {
            throw error
        }
    }

    const logout = async () => {
        try {
            await authService.logout()
            setUser(null)
        } catch (error) {
            console.error('Logout error:', error)
            setUser(null)
        }
    }

    const contextValue = { user, login, logout, isLoading }

    return React.createElement(AuthContext.Provider, { value: contextValue }, children)
}

export function useAuth() {
    const context = useContext(AuthContext)
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider')
    }
    return context
} 