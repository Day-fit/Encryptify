'use client'

import { useState } from 'react'
import { useAuth } from '@/hooks/useAuth'
import Navigation from '@/components/Navigation'
import LoginForm from '@/components/LoginForm'
import RegisterForm from '@/components/RegisterForm'
import Dashboard from '@/components/Dashboard'
import FilesView from '@/components/FilesView'
import Settings from '@/components/Settings'
import Profile from '@/components/Profile'
import LoadingSpinner from '@/components/LoadingSpinner'

export default function HomePage() {
  const { user, isLoading } = useAuth()
  const [activeView, setActiveView] = useState('dashboard')
  const [showLogin, setShowLogin] = useState(true)

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <LoadingSpinner />
      </div>
    )
  }

  if (!user) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
        <div className="sm:mx-auto sm:w-full sm:max-w-md">
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Welcome to Encryptify
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Secure, encrypted file management system
          </p>
        </div>

        <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
          <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
            <div className="flex space-x-4 mb-6">
              <button
                onClick={() => setShowLogin(true)}
                className={`flex-1 py-2 px-4 rounded-md font-medium transition-colors ${
                  showLogin
                    ? 'bg-black text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                }`}
              >
                Sign In
              </button>
              <button
                onClick={() => setShowLogin(false)}
                className={`flex-1 py-2 px-4 rounded-md font-medium transition-colors ${
                  !showLogin
                    ? 'bg-black text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                }`}
              >
                Sign Up
              </button>
            </div>

            {showLogin ? <LoginForm /> : <RegisterForm />}
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navigation activeView={activeView} onViewChange={setActiveView} />
      
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        {activeView === 'dashboard' && <Dashboard />}
        {activeView === 'files' && <FilesView />}
        {activeView === 'settings' && <Settings />}
        {activeView === 'profile' && <Profile />}
      </main>
    </div>
  )
}
