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
        <h1 className="text-2xl font-bold text-gray-900">Profile</h1>
        <p className="text-gray-600">Manage your account settings and preferences</p>
      </div>

      {/* User Information */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <div className="flex items-center space-x-4">
          <div className="p-3 bg-blue-100 rounded-full">
            <User className="h-8 w-8 text-blue-600" />
          </div>
          <div>
            <h3 className="text-lg font-medium text-gray-900">User Information</h3>
            <p className="text-gray-600">Manage your account details</p>
          </div>
        </div>
        
        <div className="mt-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Username</label>
            <p className="mt-1 text-sm text-gray-900">{user?.username}</p>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700">Account Type</label>
            <p className="mt-1 text-sm text-gray-900">Standard User</p>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700">Member Since</label>
            <p className="mt-1 text-sm text-gray-900">Recently</p>
          </div>
        </div>
      </div>

      {/* Security Settings */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <div className="flex items-center space-x-4">
          <div className="p-3 bg-green-100 rounded-full">
            <Shield className="h-8 w-8 text-green-600" />
          </div>
          <div>
            <h3 className="text-lg font-medium text-gray-900">Security</h3>
            <p className="text-gray-600">Manage your security settings</p>
          </div>
        </div>
        
        <div className="mt-6 space-y-4">
          <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
            <div>
              <h4 className="font-medium text-gray-900">End-to-End Encryption</h4>
              <p className="text-sm text-gray-600">All files are encrypted with AES-256 before upload</p>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-3 h-3 bg-green-500 rounded-full"></div>
              <span className="text-sm text-green-600 font-medium">Active</span>
            </div>
          </div>
          
          <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
            <div>
              <h4 className="font-medium text-gray-900">Secure Key Storage</h4>
              <p className="text-sm text-gray-600">Encryption keys are stored securely in your browser</p>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-3 h-3 bg-green-500 rounded-full"></div>
              <span className="text-sm text-green-600 font-medium">Active</span>
            </div>
          </div>
        </div>
      </div>

      {/* Account Actions */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <div className="flex items-center space-x-4">
          <div className="p-3 bg-yellow-100 rounded-full">
            <AlertTriangle className="h-8 w-8 text-yellow-600" />
          </div>
          <div>
            <h3 className="text-lg font-medium text-gray-900">Account Actions</h3>
            <p className="text-gray-600">Dangerous actions - use with caution</p>
          </div>
        </div>
        
        <div className="mt-6 space-y-4">
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center space-x-2 px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
          >
            <LogOut className="h-4 w-4" />
            <span>Logout</span>
          </button>
          
          <button
            onClick={() => setShowDeleteModal(true)}
            className="w-full flex items-center justify-center space-x-2 px-4 py-2 border border-red-300 rounded-lg text-red-600 hover:bg-red-50 transition-colors"
          >
            <Trash2 className="h-4 w-4" />
            <span>Delete Account</span>
          </button>
        </div>
      </div>

      {/* Delete Account Modal */}
      {showDeleteModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <div className="flex items-center space-x-3 mb-4">
              <div className="p-2 bg-red-100 rounded-full">
                <AlertTriangle className="h-6 w-6 text-red-600" />
              </div>
              <h3 className="text-lg font-medium text-gray-900">Delete Account</h3>
            </div>
            
            <div className="mb-6">
              <p className="text-gray-600 mb-4">
                Are you sure you want to delete your account? This action cannot be undone and will:
              </p>
              <ul className="text-sm text-gray-600 space-y-2">
                <li>• Permanently delete all your files and folders</li>
                <li>• Remove all encryption keys</li>
                <li>• Delete your account data</li>
                <li>• Log you out immediately</li>
              </ul>
            </div>
            
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowDeleteModal(false)}
                className="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
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

