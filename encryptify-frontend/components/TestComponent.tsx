'use client'

import { useState } from 'react'

export default function TestComponent() {
  const [count, setCount] = useState(0)

  return (
    <div className="p-6 bg-white rounded-lg shadow-sm border border-gray-200">
      <h2 className="text-xl font-bold text-gray-900 mb-4">Test Component</h2>
      <p className="text-gray-600 mb-4">
        This component tests that React, TypeScript, and Tailwind CSS are working correctly.
      </p>
      <div className="flex items-center space-x-4">
        <button
          onClick={() => setCount(count - 1)}
          className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
        >
          -
        </button>
        <span className="text-2xl font-bold text-black">{count}</span>
        <button
          onClick={() => setCount(count + 1)}
          className="px-4 py-2 bg-black text-white rounded-lg hover:bg-gray-800 transition-colors"
        >
          +
        </button>
      </div>
      <p className="text-sm text-gray-500 mt-4">
        Counter: {count} | TypeScript and React hooks are working!
      </p>
    </div>
  )
} 