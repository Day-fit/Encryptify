'use client'

import { useState, useEffect, useCallback } from 'react'

interface FileKeyMapping {
  fileId: number
  keyId: string
}

class EncryptionService {
  private db: IDBDatabase | null = null
  private readonly DB_NAME = 'EncryptifyDB'
  private readonly DB_VERSION = 2
  private readonly FILE_KEYS_STORE = 'fileKeys'
  private initPromise: Promise<void> | null = null

  async initDB(): Promise<void> {
    if (this.initPromise) {
      return this.initPromise
    }

    this.initPromise = new Promise((resolve, reject) => {
      const request = indexedDB.open(this.DB_NAME, this.DB_VERSION)
      
      request.onerror = () => reject(request.error)
      request.onsuccess = () => {
        this.db = request.result
        resolve()
      }
      
      request.onupgradeneeded = (event) => {
        const db = (event.target as IDBOpenDBRequest).result
        
        // Create fileKeys store for mapping files to their encryption keys
        if (!db.objectStoreNames.contains(this.FILE_KEYS_STORE)) {
          db.createObjectStore(this.FILE_KEYS_STORE, { keyPath: 'fileId' })
        }
      }
    })

    return this.initPromise
  }

  private async ensureDB(): Promise<void> {
    if (!this.db) {
      await this.initDB()
    }
  }

  // Generate a new AES-256-GCM key for file encryption
  async generateFileKey(): Promise<string> {
    const key = await crypto.subtle.generateKey(
      {
        name: 'AES-GCM',
        length: 256
      },
      true,
      ['encrypt', 'decrypt']
    )
    
    const keyId = crypto.randomUUID()
    
    // Store the key in localStorage as a simple solution
    const keyData = await crypto.subtle.exportKey('raw', key)
    const keyString = btoa(String.fromCharCode(...new Uint8Array(keyData)))
    localStorage.setItem(`encryptify_key_${keyId}`, keyString)
    
    return keyId
  }

  // Get a decrypted AES key
  private async getDecryptedKey(keyId: string): Promise<CryptoKey | null> {
    const keyString = localStorage.getItem(`encryptify_key_${keyId}`)
    if (!keyString) return null
    
    try {
      const keyData = Uint8Array.from(atob(keyString), c => c.charCodeAt(0))
      return await crypto.subtle.importKey(
        'raw',
        keyData,
        { name: 'AES-GCM' },
        false,
        ['encrypt', 'decrypt']
      )
    } catch (error) {
      console.error('Failed to import key:', error)
      return null
    }
  }

  // Encrypt a file with AES-256-GCM
  async encryptFile(file: File, keyId: string): Promise<File> {
    const key = await this.getDecryptedKey(keyId)
    if (!key) {
      throw new Error('Encryption key not found')
    }
    
    // Read the file
    const fileBuffer = await file.arrayBuffer()
    
    // Generate a random IV for this encryption
    const iv = crypto.getRandomValues(new Uint8Array(12))
    
    // Encrypt the file content
    const encryptedContent = await crypto.subtle.encrypt(
      {
        name: 'AES-GCM',
        iv: iv
      },
      key,
      fileBuffer
    )
    
    // Combine IV and encrypted content
    const result = new Uint8Array(iv.length + encryptedContent.byteLength)
    result.set(iv)
    result.set(new Uint8Array(encryptedContent), iv.length)
    
    // Create a new file with encrypted content
    return new File([result.buffer], file.name, {
      type: 'application/octet-stream'
    })
  }

  // Decrypt a file with AES-256-GCM
  async decryptFile(encryptedBlob: Blob, keyId: string): Promise<Blob> {
    const key = await this.getDecryptedKey(keyId)
    if (!key) {
      throw new Error('Encryption key not found')
    }
    
    // Read the encrypted file
    const encryptedData = await encryptedBlob.arrayBuffer()
    
    // Extract IV and encrypted content
    const iv = new Uint8Array(encryptedData.slice(0, 12))
    const encryptedContent = encryptedData.slice(12)
    
    // Decrypt the file content
    const decryptedContent = await crypto.subtle.decrypt(
      {
        name: 'AES-GCM',
        iv: iv
      },
      key,
      encryptedContent
    )
    
    // Return decrypted blob
    return new Blob([decryptedContent], { type: 'application/octet-stream' })
  }

  // Store key mapping for a file
  async storeFileKeyMapping(fileId: number, keyId: string): Promise<void> {
    await this.ensureDB()
    
    return new Promise((resolve, reject) => {
      const transaction = this.db!.transaction([this.FILE_KEYS_STORE], 'readwrite')
      const store = transaction.objectStore(this.FILE_KEYS_STORE)
      
      const request = store.put({ fileId, keyId })
      request.onsuccess = () => resolve()
      request.onerror = () => reject(request.error)
    })
  }

  // Get key ID for a file
  async getFileKeyId(fileId: number): Promise<string | null> {
    await this.ensureDB()
    
    return new Promise((resolve, reject) => {
      const transaction = this.db!.transaction([this.FILE_KEYS_STORE], 'readonly')
      const store = transaction.objectStore(this.FILE_KEYS_STORE)
      
      const request = store.get(fileId)
      request.onsuccess = () => {
        const result = request.result
        resolve(result ? result.keyId : null)
      }
      request.onerror = () => reject(request.error)
    })
  }

  // Clear all data
  async clearAllKeys(): Promise<void> {
    await this.ensureDB()
    
    // Clear IndexedDB
    const transaction = this.db!.transaction([this.FILE_KEYS_STORE], 'readwrite')
    const store = transaction.objectStore(this.FILE_KEYS_STORE)
    store.clear()
    
    // Clear localStorage keys
    const keys = Object.keys(localStorage).filter(key => key.startsWith('encryptify_key_'))
    keys.forEach(key => localStorage.removeItem(key))
  }
}

const encryptionService = new EncryptionService()

export function useEncryption() {
  const [isInitialized, setIsInitialized] = useState(false)

  useEffect(() => {
    const init = async () => {
      try {
        await encryptionService.initDB()
        setIsInitialized(true)
      } catch (error) {
        console.error('Failed to initialize encryption service:', error)
        setIsInitialized(false) // Ensure state is false if init fails
      }
    }
    
    init()
  }, [])

  const generateFileKey = useCallback(async (): Promise<string> => {
    if (!isInitialized) {
      throw new Error('Encryption service not initialized')
    }
    return await encryptionService.generateFileKey()
  }, [isInitialized])

  const encryptFile = useCallback(async (file: File, keyId: string): Promise<File> => {
    if (!isInitialized) {
      throw new Error('Encryption service not initialized')
    }
    return await encryptionService.encryptFile(file, keyId)
  }, [isInitialized])

  const decryptFile = useCallback(async (blob: Blob, keyId: string): Promise<Blob> => {
    if (!isInitialized) {
      throw new Error('Encryption service not initialized')
    }
    return await encryptionService.decryptFile(blob, keyId)
  }, [isInitialized])

  const storeFileKeyMapping = useCallback(async (fileId: number, keyId: string): Promise<void> => {
    if (!isInitialized) {
      throw new Error('Encryption service not initialized')
    }
    return await encryptionService.storeFileKeyMapping(fileId, keyId)
  }, [isInitialized])

  const getFileKeyId = useCallback(async (fileId: number): Promise<string | null> => {
    if (!isInitialized) {
      throw new Error('Encryption service not initialized')
    }
    return await encryptionService.getFileKeyId(fileId)
  }, [isInitialized])

  const clearAllKeys = useCallback(async (): Promise<void> => {
    if (!isInitialized) {
      throw new Error('Encryption service not initialized')
    }
    return await encryptionService.clearAllKeys()
  }, [isInitialized])

  return {
    isInitialized,
    generateFileKey,
    encryptFile,
    decryptFile,
    storeFileKeyMapping,
    getFileKeyId,
    clearAllKeys
  }
}
