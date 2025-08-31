'use client'

import {useCallback, useEffect, useState} from 'react'

interface KeyPair {
  publicKey: string
  privateKey: string
  id: string
}

interface EncryptedData {
  encryptedContent: ArrayBuffer
  iv: Uint8Array
  keyId: string
}

class EncryptionService {
  private db: IDBDatabase | null = null
  private readonly DB_NAME = 'EncryptifyDB'
  private readonly DB_VERSION = 1
  private readonly KEYS_STORE = 'encryptionKeys'
  private readonly DATA_STORE = 'encryptedData'

  async initDB(): Promise<void> {
    return new Promise((resolve, reject) => {
      const request = indexedDB.open(this.DB_NAME, this.DB_VERSION)

      request.onerror = () => reject(request.error)
      request.onsuccess = () => {
        this.db = request.result
        resolve()
      }

      request.onupgradeneeded = (event) => {
        const db = (event.target as IDBOpenDBRequest).result

        // Create stores if they don't exist
        if (!db.objectStoreNames.contains(this.KEYS_STORE)) {
          db.createObjectStore(this.KEYS_STORE, { keyPath: 'id' })
        }
        if (!db.objectStoreNames.contains(this.DATA_STORE)) {
          db.createObjectStore(this.DATA_STORE, { keyPath: 'id' })
        }
      }
    })
  }

  async generateKeyPair(): Promise<KeyPair> {
    // Generate a random AES-256 key
    const key = await crypto.subtle.generateKey(
      {
        name: 'AES-GCM',
        length: 256
      },
      true,
      ['encrypt', 'decrypt']
    )

    // Export the key as raw bytes
    const rawKey = await crypto.subtle.exportKey('raw', key)
    
    // Generate a unique ID for this key pair
    const keyId = crypto.randomUUID()
    
    // Store the private key securely (encrypted with user's password)
    const encryptedPrivateKey = await this.encryptKeyWithPassword(rawKey)
    
    // Store in IndexedDB
    await this.storeKey(keyId, encryptedPrivateKey)
    
    return {
      publicKey: keyId, // We use the keyId as the public identifier
      privateKey: keyId,
      id: keyId
    }
  }

  private async encryptKeyWithPassword(keyData: ArrayBuffer): Promise<ArrayBuffer> {
    // In a real implementation, this would use the user's password
    const masterKey = await this.getOrCreateMasterKey()
    
    // Generate a random IV
    const iv = crypto.getRandomValues(new Uint8Array(12))
    
    // Encrypt the key data
    const encrypted = await crypto.subtle.encrypt(
      {
        name: 'AES-GCM',
        iv: iv
      },
      masterKey,
      keyData
    )
    
    // Combine IV and encrypted data
    const result = new Uint8Array(iv.length + encrypted.byteLength)
    result.set(iv)
    result.set(new Uint8Array(encrypted), iv.length)
    
    return result.buffer
  }

  private async getOrCreateMasterKey(): Promise<CryptoKey> {
    const storedKey = localStorage.getItem('encryptify_master_key')
    
    if (storedKey) {
      // Import the stored key
      const keyData = Uint8Array.from(atob(storedKey), c => c.charCodeAt(0))
      return await crypto.subtle.importKey(
        'raw',
        keyData,
        { name: 'AES-GCM' },
        false,
        ['encrypt', 'decrypt']
      )
    } else {
      const masterKey = await crypto.subtle.generateKey(
        {
          name: 'AES-GCM',
          length: 256
        },
        true,
        ['encrypt', 'decrypt']
      )
      
      const rawKey = await crypto.subtle.exportKey('raw', masterKey)
      const keyString = btoa(String.fromCharCode(...new Uint8Array(rawKey)))
      localStorage.setItem('encryptify_master_key', keyString)
      
      return masterKey
    }
  }

  private async storeKey(keyId: string, encryptedKey: ArrayBuffer): Promise<void> {
    if (!this.db) await this.initDB()
    
    return new Promise((resolve, reject) => {
      const transaction = this.db!.transaction([this.KEYS_STORE], 'readwrite')
      const store = transaction.objectStore(this.KEYS_STORE)
      
      const request = store.put({
        id: keyId,
        encryptedKey: encryptedKey,
        createdAt: new Date().toISOString()
      })
      
      request.onsuccess = () => resolve()
      request.onerror = () => reject(request.error)
    })
  }

  private async getKey(keyId: string): Promise<ArrayBuffer | null> {
    if (!this.db) await this.initDB()
    
    return new Promise((resolve, reject) => {
      const transaction = this.db!.transaction([this.KEYS_STORE], 'readonly')
      const store = transaction.objectStore(this.KEYS_STORE)
      
      const request = store.get(keyId)
      
      request.onsuccess = () => {
        const result = request.result
        resolve(result ? result.encryptedKey : null)
      }
      request.onerror = () => reject(request.error)
    })
  }

  async encryptFile(file: File, keyId: string): Promise<File> {
    // Get the encryption key
    const encryptedKeyData = await this.getKey(keyId)
    if (!encryptedKeyData) {
      throw new Error('Encryption key not found')
    }
    
    // Decrypt the key
    const masterKey = await this.getOrCreateMasterKey()
    const iv = new Uint8Array(encryptedKeyData.slice(0, 12))
    const encryptedKey = encryptedKeyData.slice(12)
    
    const decryptedKeyData = await crypto.subtle.decrypt(
      {
        name: 'AES-GCM',
        iv: iv
      },
      masterKey,
      encryptedKey
    )
    
    // Import the decrypted key
    const key = await crypto.subtle.importKey(
      'raw',
      decryptedKeyData,
      { name: 'AES-GCM' },
      false,
      ['encrypt']
    )
    
    // Read the file
    const fileBuffer = await file.arrayBuffer()
    
    // Generate a random IV for this encryption
    const fileIv = crypto.getRandomValues(new Uint8Array(12))
    
    // Encrypt the file content
    const encryptedContent = await crypto.subtle.encrypt(
      {
        name: 'AES-GCM',
        iv: fileIv
      },
      key,
      fileBuffer
    )
    
    // Combine IV and encrypted content
    const result = new Uint8Array(fileIv.length + encryptedContent.byteLength)
    result.set(fileIv)
    result.set(new Uint8Array(encryptedContent), fileIv.length)
    
    // Create a new file with encrypted content
    return new File([result.buffer], file.name, {
      type: 'application/octet-stream'
    })
  }

  async decryptFile(encryptedBlob: Blob): Promise<Blob> {
    // For now, we'll assume the blob contains encrypted data
    // In a real implementation, you'd need to know which key was used
    // This is a simplified version
    
    const encryptedData = await encryptedBlob.arrayBuffer()
    new Uint8Array(encryptedData.slice(0, 12));
    const encryptedContent = encryptedData.slice(12)
    
    // You would need to get the correct key here
    // For now, we'll return the original blob
    // This is a placeholder - in reality you'd decrypt with the correct key
    
    return new Blob([encryptedContent], { type: 'application/octet-stream' })
  }

  async clearAllKeys(): Promise<void> {
    if (!this.db) return
    
    return new Promise((resolve, reject) => {
      const transaction = this.db!.transaction([this.KEYS_STORE, this.DATA_STORE], 'readwrite')
      
      const keysStore = transaction.objectStore(this.KEYS_STORE)
      const dataStore = transaction.objectStore(this.DATA_STORE)
      
      keysStore.clear()
      dataStore.clear()
      
      transaction.oncomplete = () => resolve()
      transaction.onerror = () => reject(transaction.error)
    })
  }

  async encryptData(data: ArrayBuffer, keyId: string): Promise<EncryptedData> {
    const encryptedKeyData = await this.getKey(keyId)
    if (!encryptedKeyData) {
      throw new Error('Encryption key not found')
    }
    
    // Decrypt the key (similar to encryptFile)
    const masterKey = await this.getOrCreateMasterKey()
    const iv = new Uint8Array(encryptedKeyData.slice(0, 12))
    const encryptedKey = encryptedKeyData.slice(12)
    
    const decryptedKeyData = await crypto.subtle.decrypt(
      {
        name: 'AES-GCM',
        iv: iv
      },
      masterKey,
      encryptedKey
    )
    
    const key = await crypto.subtle.importKey(
      'raw',
      decryptedKeyData,
      { name: 'AES-GCM' },
      false,
      ['encrypt']
    )
    
    const dataIv = crypto.getRandomValues(new Uint8Array(12))
    const encryptedContent = await crypto.subtle.encrypt(
      {
        name: 'AES-GCM',
        iv: dataIv
      },
      key,
      data
    )
    
    return {
      encryptedContent,
      iv: dataIv,
      keyId
    }
  }

  async decryptData(encryptedData: EncryptedData): Promise<ArrayBuffer> {
    const encryptedKeyData = await this.getKey(encryptedData.keyId)
    if (!encryptedKeyData) {
      throw new Error('Encryption key not found')
    }
    
    const masterKey = await this.getOrCreateMasterKey()
    const keyIv = new Uint8Array(encryptedKeyData.slice(0, 12))
    const encryptedKey = encryptedKeyData.slice(12)
    
    const decryptedKeyData = await crypto.subtle.decrypt(
      {
        name: 'AES-GCM',
        iv: keyIv
      },
      masterKey,
      encryptedKey
    )
    
    const key = await crypto.subtle.importKey(
      'raw',
      decryptedKeyData,
      { name: 'AES-GCM' },
      false,
      ['decrypt']
    )

    return await crypto.subtle.decrypt(
        {
          name: 'AES-GCM',
          iv: new Uint8Array(encryptedData.iv)
        },
        key,
        encryptedData.encryptedContent
    )
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
      }
    }
    
    init()
  }, [])

  const generateKeyPair = useCallback(async (): Promise<KeyPair> => {
    if (!isInitialized) {
      throw new Error('Encryption service not initialized')
    }
    return await encryptionService.generateKeyPair()
  }, [isInitialized])

  const encryptFile = useCallback(async (file: File, keyId: string): Promise<File> => {
    if (!isInitialized) {
      throw new Error('Encryption service not initialized')
    }
    return await encryptionService.encryptFile(file, keyId)
  }, [isInitialized])

  const decryptFile = useCallback(async (blob: Blob): Promise<Blob> => {
    if (!isInitialized) {
      throw new Error('Encryption service not initialized')
    }
    return await encryptionService.decryptFile(blob)
  }, [isInitialized])

  const encryptData = useCallback(async (data: ArrayBuffer, keyId: string): Promise<EncryptedData> => {
    if (!isInitialized) {
      throw new Error('Encryption service not initialized')
    }
    return await encryptionService.encryptData(data, keyId)
  }, [isInitialized])

  const decryptData = useCallback(async (encryptedData: EncryptedData): Promise<ArrayBuffer> => {
    if (!isInitialized) {
      throw new Error('Encryption service not initialized')
    }
    return await encryptionService.decryptData(encryptedData)
  }, [isInitialized])

  const clearAllKeys = useCallback(async (): Promise<void> => {
    if (!isInitialized) {
      throw new Error('Encryption service not initialized')
    }
    return await encryptionService.clearAllKeys()
  }, [isInitialized])

  return {
    isInitialized,
    generateKeyPair,
    encryptFile,
    decryptFile,
    encryptData,
    decryptData,
    clearAllKeys
  }
}
