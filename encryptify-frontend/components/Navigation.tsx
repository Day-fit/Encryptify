'use client'

interface NavigationProps {
  activeView: string
  onViewChange: (view: string) => void
}

export default function Navigation({ activeView, onViewChange }: NavigationProps) {
  return (
    <nav className="bg-white border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex space-x-8">
          <button
            onClick={() => onViewChange('dashboard')}
            className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
              activeView === 'dashboard'
                ? 'border-black text-black'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            Dashboard
          </button>
          
          <button
            onClick={() => onViewChange('files')}
            className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
              activeView === 'files'
                ? 'border-black text-black'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            Files
          </button>
          
          <button
            onClick={() => onViewChange('settings')}
            className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
              activeView === 'settings'
                ? 'border-black text-black'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            Settings
          </button>
          
          <button
            onClick={() => onViewChange('profile')}
            className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
              activeView === 'profile'
                ? 'border-black text-black'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            Profile
          </button>
        </div>
      </div>
    </nav>
  )
} 