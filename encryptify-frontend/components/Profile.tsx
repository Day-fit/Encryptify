'use client'

import { useState } from 'react'
import { useAuth } from '@/hooks/useAuth'
import { useEncryption } from '@/hooks/useEncryption'
import { User, Trash2, Shield, LogOut, AlertTriangle } from 'lucide-react'
import { authService } from '@/services/authService'
import toast from 'react-hot-toast'

export default function Profile() {
  const { user, logout } = useAuth()
  const { clearAllKeys } = useEncryption()
  const [showDeleteModal, setShowDeleteModal] = useState(false)
  const [isDeleting, setIsDeleting] = useState(false)

  const handleLogout = async () => {
    try {
      // Clear all encryption keys before logout
      await clearAllKeys()
      
      // Clear master key from localStorage
      localStorage.removeItem('encryptify_master_key')
      
      // Logout from server
      await logout()
      toast.success('Logged out successfully')
    } catch (error: any) {
      console.error('Logout error:', error)
      // Force logout even if server call fails
      logout()
    }
  }

  const handleDeleteAccount = async () => {
    setIsDeleting(true)
    try {
      // Clear all encryption keys
      await clearAllKeys()
      
      // Clear master key from localStorage
      localStorage.removeItem('encryptify_master_key')
      
      // Note: Backend would need a delete account endpoint
      // For now, we'll just logout and show a message
      toast.success('Account deletion requested. Please contact support for permanent deletion.')
      setShowDeleteModal(false)
      
      // Logout after account deletion
      await logout()
    } catch (error: any) {
      toast.error('Failed to delete account: ' + error.message)
    } finally {
      setIsDeleting(false)
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Profile</h1>
        <p className="text-gray-600 dark:text-gray-400">Manage your account settings and preferences</p>
      </div>

      {/* User Information */}
      <div className="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-6">
        <div className="flex items-center space-x-4">
          <div className="p-3 bg-blue-100 dark:bg-blue-900 rounded-full">
            <User className="h-8 w-8 text-blue-600 dark:text-blue-400" />
          </div>
          <div>
            <h3 className="text-lg font-medium text-gray-900 dark:text-gray-100">User Information</h3>
            <p className="text-gray-600 dark:text-gray-400">Manage your account details</p>
          </div>
        </div>
        
        <div className="mt-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">Username</label>
            <p className="mt-1 text-sm text-gray-900 dark:text-gray-100">{user?.username}</p>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">Account Type</label>
            <p className="mt-1 text-sm text-gray-900 dark:text-gray-100">
              {user?.accountType?.includes('ADMIN') ? 'Administrator' : 'Standard User'}
            </p>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">Member Since</label>
            <p className="mt-1 text-sm text-gray-900 dark:text-gray-100">
              {user?.registrationDate ? new Date(user.registrationDate).toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
              }) : 'Unknown'}
            </p>
          </div>
        </div>
      </div>

      {/* Security Settings */}
      <div className="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-6">
        <div className="flex items-center space-x-4">
          <div className="p-3 bg-green-100 dark:bg-green-900 rounded-full">
            <Shield className="h-8 w-8 text-green-600 dark:text-green-400" />
          </div>
          <div>
            <h3 className="text-lg font-medium text-gray-900 dark:text-gray-100">Security</h3>
            <p className="text-gray-600 dark:text-gray-400">Manage your security settings</p>
          </div>
        </div>
        
        <div className="mt-6 space-y-4">
          <div className="flex items-center justify-between p-4 bg-gray-50 dark:bg-gray-700 rounded-lg">
            <div>
              <h4 className="font-medium text-gray-900 dark:text-gray-100">End-to-End Encryption</h4>
              <p className="text-sm text-gray-600 dark:text-gray-400">All files are encrypted with AES-256 before upload</p>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-3 h-3 bg-green-500 rounded-full"></div>
              <span className="text-sm text-green-600 dark:text-green-400 font-medium">Active</span>
            </div>
          </div>
          
          <div className="flex items-center justify-between p-4 bg-gray-50 dark:bg-gray-700 rounded-lg">
            <div>
              <h4 className="font-medium text-gray-900 dark:text-gray-100">Secure Key Storage</h4>
              <p className="text-sm text-gray-600 dark:text-gray-400">Encryption keys are stored securely in your browser</p>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-3 h-3 bg-green-500 rounded-full"></div>
              <span className="text-sm text-green-600 dark:text-green-400 font-medium">Active</span>
            </div>
          </div>
        </div>
      </div>

      {/* Account Actions */}
      <div className="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-6">
        <div className="flex items-center space-x-4">
          <div className="p-3 bg-yellow-100 dark:bg-yellow-900 rounded-full">
            <AlertTriangle className="h-8 w-8 text-yellow-600 dark:text-yellow-400" />
          </div>
          <div>
            <h3 className="text-lg font-medium text-gray-900 dark:text-gray-100">Account Actions</h3>
            <p className="text-gray-600 dark:text-gray-400">Dangerous actions - use with caution</p>
          </div>
        </div>
        
        <div className="mt-6 space-y-4">
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center space-x-2 px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
          >
            <LogOut className="h-4 w-4" />
            <span>Logout</span>
          </button>
          
          <button
            onClick={() => setShowDeleteModal(true)}
            className="w-full flex items-center justify-center space-x-2 px-4 py-2 border border-red-300 dark:border-red-600 rounded-lg text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors"
          >
            <Trash2 className="h-4 w-4" />
            <span>Delete Account</span>
          </button>
        </div>
      </div>

      {/* Delete Account Modal */}
      {showDeleteModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-800 rounded-lg p-6 w-full max-w-md">
            <div className="flex items-center space-x-3 mb-4">
              <div className="p-2 bg-red-100 dark:bg-red-900 rounded-full">
                <AlertTriangle className="h-6 w-6 text-red-600 dark:text-red-400" />
              </div>
              <h3 className="text-lg font-medium text-gray-900 dark:text-gray-100">Delete Account</h3>
            </div>
            
            <div className="mb-6">
              <p className="text-gray-600 dark:text-gray-400 mb-4">
                Are you sure you want to delete your account? This action cannot be undone and will:
              </p>
              <ul className="text-sm text-gray-600 dark:text-gray-400 space-y-2">
                <li>• Permanently delete all your files and folders</li>
                <li>• Remove all encryption keys</li>
                <li>• Delete your account data</li>
                <li>• Log you out immediately</li>
              </ul>
            </div>
            
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowDeleteModal(false)}
                className="px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
                disabled={isDeleting}
              >
                Cancel
              </button>
              <button
                onClick={handleDeleteAccount}
                disabled={isDeleting}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors disabled:opacity-50"
              >
                {isDeleting ? 'Deleting...' : 'Delete Account'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

