'use client'

import Navigation from '@/components/Navigation'
import { useState } from 'react'

export default function DebugPage() {
  const [activeView, setActiveView] = useState('dashboard')

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Debug Page</h1>
      
      <div className="space-y-8">
        <div>
          <h2 className="text-xl font-bold text-gray-900 mb-4">Navigation Component Test</h2>
          <Navigation activeView={activeView} onViewChange={setActiveView} />
          <p className="mt-4 text-sm text-gray-600">Active view: {activeView}</p>
        </div>

        <div>
          <h2 className="text-xl font-bold text-gray-900 mb-4">Component Status</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="p-4 bg-green-100 rounded-lg">
              <h3 className="font-semibold text-green-800">✅ Working Components</h3>
              <ul className="text-sm text-green-700 mt-2">
                <li>• Navigation (if you can see this)</li>
                <li>• Page rendering</li>
                <li>• State management</li>
              </ul>
            </div>
            
            <div className="p-4 bg-yellow-100 rounded-lg">
              <h3 className="font-semibold text-yellow-800">⚠️ Potential Issues</h3>
              <ul className="text-sm text-yellow-700 mt-2">
                <li>• Icon imports from lucide-react</li>
                <li>• Component export/import</li>
                <li>• Build configuration</li>
              </ul>
            </div>
          </div>
        </div>

        <div>
          <h2 className="text-xl font-bold text-gray-900 mb-4">Next Steps</h2>
          <div className="space-y-2 text-sm text-gray-600">
            <p>1. If Navigation renders above, the component is working</p>
            <p>2. If you see an error, check the browser console</p>
            <p>3. Try refreshing the page</p>
            <p>4. Check if all dependencies are installed</p>
          </div>
        </div>
      </div>
    </div>
  )
} 