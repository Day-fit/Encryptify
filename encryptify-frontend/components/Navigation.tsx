'use client'

interface NavigationProps {
  activeView: string
  onViewChange: (view: string) => void
}

export default function Navigation({ activeView, onViewChange }: NavigationProps) {
  return (
    <nav className="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex space-x-8">
          <button
            onClick={() => onViewChange('dashboard')}
            className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
              activeView === 'dashboard'
                ? 'border-black dark:border-white text-black dark:text-white'
                : 'border-transparent text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300 hover:border-gray-300 dark:hover:border-gray-600'
            }`}
          >
            Dashboard
          </button>
          
          <button
            onClick={() => onViewChange('files')}
            className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
              activeView === 'files'
                ? 'border-black dark:border-white text-black dark:text-white'
                : 'border-transparent text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300 hover:border-gray-300 dark:hover:border-gray-600'
            }`}
          >
            Files
          </button>
          
          <button
            onClick={() => onViewChange('settings')}
            className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
              activeView === 'settings'
                ? 'border-black dark:border-white text-black dark:text-white'
                : 'border-transparent text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300 hover:border-gray-300 dark:hover:border-gray-600'
            }`}
          >
            Settings
          </button>
          
          <button
            onClick={() => onViewChange('profile')}
            className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
              activeView === 'profile'
                ? 'border-black dark:border-white text-black dark:text-white'
                : 'border-transparent text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300 hover:border-gray-300 dark:hover:border-gray-600'
            }`}
          >
            Profile
          </button>
        </div>
      </div>
    </nav>
  )
} 