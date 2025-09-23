'use client'

import { useState, useEffect, useRef } from 'react'
import { 
  File, 
  Folder, 
  Download, 
  Trash2, 
  Edit, 
  Plus, 
  Upload, 
  Grid, 
  List, 
  ArrowLeft,
  MoreVertical
} from 'lucide-react'
import { fileService } from '@/services/fileService'
import { useEncryption } from '@/hooks/useEncryption'
import toast from 'react-hot-toast'

interface FileSystemItem {
  id: number
  name: string
  type: 'FILE' | 'FOLDER'
  fileSize?: string
  uploadDate?: string
  creationDate?: string
}

export default function FilesView() {
  const [items, setItems] = useState<FileSystemItem[]>([])
  const [currentFolderId, setCurrentFolderId] = useState<number | null>(null)
  const [viewMode, setViewMode] = useState<'list' | 'grid'>('grid')
  const [isLoading, setIsLoading] = useState(false)
  const [selectedItems, setSelectedItems] = useState<Set<number>>(new Set())
  const [showContextMenu, setShowContextMenu] = useState<{ x: number; y: number; itemId: number } | null>(null)
  
  // State to hold the item that the context menu is currently acting upon
  const [itemInContext, setItemInContext] = useState<FileSystemItem | null>(null)
  // State to track the ID of the file currently being downloaded
  const [downloadingFileId, setDownloadingFileId] = useState<number | null>(null)
  
  // Modal states
  const [showUploadModal, setShowUploadModal] = useState(false)
  const [showCreateFolderModal, setShowCreateFolderModal] = useState(false)
  const [showRenameModal, setShowRenameModal] = useState(false)
  const [showDeleteModal, setShowDeleteModal] = useState(false)
  
  // Form states
  const [newFolderName, setNewFolderName] = useState('')
  const [renameName, setRenameName] = useState('')
  const [uploadFile, setUploadFile] = useState<File | null>(null)
  const [uploadFolderId, setUploadFolderId] = useState<number | null>(null)
  const [uploadProgress, setUploadProgress] = useState(0) // New state for upload progress
  
  const { isInitialized, encryptFile, decryptFile, generateFileKey, storeFileKeyMapping, getFileKeyId } = useEncryption()
  const fileInputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    loadItems()
  }, [currentFolderId])

  const loadItems = async () => {
    setIsLoading(true)
    try {
      // Clear items first to prevent duplication
      setItems([])
      const data = await fileService.getFiles(currentFolderId || undefined)
      setItems(data)
    } catch (error: any) {
      toast.error(error.message)
    } finally {
      setIsLoading(false)
    }
  }

  const handleFileUpload = async () => {
    if (!uploadFile) return
    if (!isInitialized) {
      toast.error('Encryption service not initialized. Please wait or refresh.')
      return
    }

    try {
      // Generate encryption key for this file
      const keyId = await generateFileKey()
      
      // Encrypt file before upload
      const encryptedFile = await encryptFile(uploadFile, keyId)
      
      // Upload encrypted file
      const uploadResponse = await fileService.uploadFile(encryptedFile, (uploadFolderId || currentFolderId) || undefined, keyId, setUploadProgress)
      
      // Store the key mapping for this file
      await storeFileKeyMapping(uploadResponse.id, keyId)
      
      toast.success('File uploaded successfully!')
      setShowUploadModal(false)
      setUploadFile(null)
      loadItems()
    } catch (error: any) {
      toast.error(error.message)
    } finally {
      setUploadProgress(0) // Reset progress after upload attempt
    }
  }

  const handleCreateFolder = async () => {
    if (!newFolderName.trim()) return

    try {
      await fileService.createFolder(newFolderName.trim(), currentFolderId || undefined)
      toast.success('Folder created successfully!')
      setShowCreateFolderModal(false)
      setNewFolderName('')
      loadItems()
    } catch (error: any) {
      toast.error(error.message)
    }
  }

  const handleRename = async () => {
    if (!renameName.trim()) return

    try {
      if (!itemInContext) {
        return
      }
      
      if (itemInContext.type === 'FOLDER') {
        await fileService.renameFolder(itemInContext.id, renameName.trim())
      } else if (itemInContext.type === 'FILE') {
        await fileService.renameFile(itemInContext.id, renameName.trim())
      }
      
      toast.success('Renamed successfully!')
      setShowRenameModal(false)
      setRenameName('')
      setShowContextMenu(null)
      loadItems()
    } catch (error: any) {
      toast.error(error.message)
    }
  }

  const handleDelete = async () => {
    if (!itemInContext) return

    try {
      if (!itemInContext) {
        return
      }
      
      if (itemInContext.type === 'FILE') {
        await fileService.deleteFile(itemInContext.id)
      } else {
        await fileService.deleteFolder(itemInContext.id)
      }
      
      toast.success('Deleted successfully!')
      setShowDeleteModal(false)
      setShowContextMenu(null)
      loadItems()
    } catch (error: any) {
      toast.error(error.message)
    }
  }

  const handleDownload = async (fileId: number, fileName: string) => {
    if (!isInitialized) {
      toast.error('Encryption service not initialized. Please wait or refresh.')
      return
    }
    setDownloadingFileId(fileId)
    try {
      const blob = await fileService.downloadFile(fileId)
      
      // Get the key ID for this file
      const keyId = await getFileKeyId(fileId)
      if (!keyId) {
        toast.error('Encryption key not found for this file')
        return
      }
      
      // Decrypt the file
      const decryptedBlob = await decryptFile(blob, keyId)
      
      // Download the decrypted file
      const url = window.URL.createObjectURL(decryptedBlob)
      const a = document.createElement('a')
      a.href = url
      a.download = fileName
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(a)
      
      toast.success('File downloaded successfully!')
    } catch (error: any) {
      toast.error(error.message)
    } finally {
      setDownloadingFileId(null)
    }
  }

  const handleItemClick = (item: FileSystemItem) => {
    if (item.type === 'FOLDER') {
      setCurrentFolderId(item.id)
    }
  }

  const handleContextMenu = (e: React.MouseEvent, item: FileSystemItem) => {
    e.preventDefault()
    setItemInContext(item)
    setShowContextMenu({ x: e.clientX, y: e.clientY, itemId: item.id })
  }

  const renderListItem = (item: FileSystemItem) => (
    <div
      key={`${item.type}-${item.id}`}
      className="flex items-center justify-between p-4 hover:bg-gray-50 dark:hover:bg-gray-700 border-b border-gray-100 dark:border-gray-600 cursor-pointer"
      onClick={() => handleItemClick(item)}
      onContextMenu={(e) => handleContextMenu(e, item)}
    >
      <div className="flex items-center space-x-3">
        {item.type === 'FOLDER' ? (
          <Folder className="h-5 w-5 text-blue-500 dark:text-blue-400" />
        ) : (
          <File className="h-5 w-5 text-gray-500 dark:text-gray-400" />
        )}
        <div>
          <p className="font-medium text-gray-900 dark:text-gray-100">{item.name}</p>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            {item.type === 'FILE' 
              ? `${item.fileSize} • ${item.uploadDate}`
              : `Created ${item.creationDate}`
            }
          </p>
        </div>
      </div>
      
      <div className="flex items-center space-x-2">
        {item.type === 'FILE' && (
          <>
            <button
              onClick={(e) => {
                e.stopPropagation()
                handleDownload(item.id, item.name)
              }}
              className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg"
              disabled={downloadingFileId === item.id}
            >
              {downloadingFileId === item.id ? (
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
              ) : (
                <Download className="h-4 w-4" />
              )}
            </button>
            <button
              onClick={(e) => {
                e.stopPropagation()
                handleContextMenu(e, item)
              }}
              className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-50 rounded-lg"
            >
              <MoreVertical className="h-4 w-4" />
            </button>
          </>
        )}
        {item.type === 'FOLDER' && (
          <button
            onClick={(e) => {
              e.stopPropagation()
              handleContextMenu(e, item)
            }}
            className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-50 rounded-lg"
          >
            <MoreVertical className="h-4 w-4" />
          </button>
        )}
      </div>
    </div>
  )

  const renderGridItem = (item: FileSystemItem) => (
    <div
      key={`${item.type}-${item.id}`}
      className="p-4 border border-gray-200 dark:border-gray-600 rounded-lg hover:border-gray-300 dark:hover:border-gray-500 hover:shadow-sm cursor-pointer bg-white dark:bg-gray-700"
      onClick={() => handleItemClick(item)}
      onContextMenu={(e) => handleContextMenu(e, item)}
    >
      <div className="text-center">
        {item.type === 'FOLDER' ? (
          <Folder className="h-12 w-12 text-blue-500 dark:text-blue-400 mx-auto mb-3" />
        ) : (
          <File className="h-12 w-12 text-gray-500 dark:text-gray-400 mx-auto mb-3" />
        )}
        <p className="font-medium text-gray-900 dark:text-gray-100 text-sm truncate">{item.name}</p>
        <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
          {item.type === 'FILE' ? item.fileSize : 'Folder'}
        </p>
      </div>
      
      {item.type === 'FILE' && (
        <div className="mt-3 flex justify-center space-x-2">
          <button
            onClick={(e) => {
              e.stopPropagation()
              handleDownload(item.id, item.name)
            }}
            className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg"
            title="Download"
            disabled={downloadingFileId === item.id}
          >
            {downloadingFileId === item.id ? (
              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
            ) : (
              <Download className="h-4 w-4" />
            )}
          </button>
          <button
            onClick={(e) => {
              e.stopPropagation()
              handleContextMenu(e, item)
            }}
            className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-50 rounded-lg"
            title="More options"
          >
            <MoreVertical className="h-4 w-4" />
          </button>
        </div>
      )}
    </div>
  )

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          {currentFolderId && (
            <button
              onClick={() => setCurrentFolderId(null)}
              className="p-2 text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
            >
              <ArrowLeft className="h-5 w-5" />
            </button>
          )}
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Files</h1>
            <p className="text-gray-600 dark:text-gray-400">
              {currentFolderId ? 'Current folder' : 'Root directory'}
            </p>
          </div>
        </div>
        
        <div className="flex items-center space-x-3">
          <button
            onClick={() => setShowCreateFolderModal(true)}
            className="btn-secondary flex items-center space-x-2"
          >
            <Plus className="h-4 w-4" />
            <span>New Folder</span>
          </button>
          
          <button
            onClick={() => setShowUploadModal(true)}
            disabled={!isInitialized}
            className="btn-primary disabled:opacity-50 flex items-center space-x-2"
          >
            <Upload className="h-4 w-4" />
            <span>Upload File</span>
          </button>
          
          <div className="flex border border-gray-200 rounded-lg">
            <button
              onClick={() => setViewMode('grid')}
              className={`p-2 ${viewMode === 'grid' ? 'bg-gray-100 text-gray-900' : 'text-gray-500'}`}
            >
              <Grid className="h-4 w-4" />
            </button>
            <button
              onClick={() => setViewMode('list')}
              className={`p-2 ${viewMode === 'list' ? 'bg-gray-100 text-gray-900' : 'text-gray-500'}`}
            >
              <List className="h-4 w-4" />
            </button>
          </div>
        </div>
      </div>

      {/* File List */}
      <div className="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700">
        {isLoading ? (
          <div className="p-8 text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900 dark:border-gray-100 mx-auto"></div>
            <p className="mt-2 text-gray-500 dark:text-gray-400">Loading...</p>
          </div>
        ) : items.length === 0 ? (
          <div className="p-8 text-center">
            <Folder className="h-12 w-12 text-gray-400 dark:text-gray-500 mx-auto mb-3" />
            <p className="text-gray-500 dark:text-gray-400">No files or folders yet</p>
            <p className="text-sm text-gray-400 dark:text-gray-500 mt-1">Upload a file or create a folder to get started</p>
          </div>
        ) : (
          <div className={viewMode === 'grid' ? 'grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4 p-4' : ''}>
            {viewMode === 'list' ? items.map(renderListItem) : items.map(renderGridItem)}
          </div>
        )}
      </div>

      {/* Upload Modal */}
      {showUploadModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-medium mb-4">Upload File</h3>
            <input
              ref={fileInputRef}
              type="file"
              onChange={(e) => setUploadFile(e.target.files?.[0] || null)}
              className="w-full mb-4"
            />
            {uploadProgress > 0 && uploadProgress < 100 && (
              <div className="w-full bg-gray-200 rounded-full h-2.5 mb-4">
                <div
                  className="bg-blue-600 h-2.5 rounded-full"
                  style={{ width: `${uploadProgress}%` }}
                ></div>
              </div>
            )}
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowUploadModal(false)}
                className="btn-secondary"
              >
                Cancel
              </button>
              <button
                onClick={handleFileUpload}
                disabled={!uploadFile || !isInitialized || (uploadProgress > 0 && uploadProgress < 100)}
                className="btn-primary disabled:opacity-50"
              >
                Upload
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Create Folder Modal */}
      {showCreateFolderModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-medium mb-4">Create New Folder</h3>
            <input
              type="text"
              value={newFolderName}
              onChange={(e) => setNewFolderName(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter' && newFolderName.trim()) {
                  handleCreateFolder()
                }
              }}
              placeholder="Folder name"
              className="input-field w-full mb-4"
            />
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowCreateFolderModal(false)}
                className="btn-secondary"
              >
                Cancel
              </button>
              <button
                onClick={handleCreateFolder}
                disabled={!newFolderName.trim()}
                className="btn-primary disabled:opacity-50"
              >
                Create
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Rename Modal */}
      {showRenameModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-medium mb-4">Rename</h3>
            <input
              type="text"
              value={renameName}
              onChange={(e) => setRenameName(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter' && renameName.trim()) {
                  handleRename()
                }
              }}
              placeholder="New name"
              className="input-field w-full mb-4"
            />
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => {
                  setShowRenameModal(false)
                  setItemInContext(null)
                }}
                className="btn-secondary"
              >
                Cancel
              </button>
              <button
                onClick={handleRename}
                disabled={!renameName.trim()}
                className="btn-primary disabled:opacity-50"
              >
                Rename
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Modal */}
      {showDeleteModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-lg">
            <h3 className="text-lg font-medium mb-4">Confirm Delete</h3>
            <p className="text-gray-600 mb-4 text-wrap">Are you sure you want to delete this item? This action cannot be undone.</p>
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => {
                  setShowDeleteModal(false)
                  setItemInContext(null)
                }}
                className="btn-secondary"
              >
                Cancel
              </button>
              <button
                onClick={handleDelete}
                className="btn-danger"
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    handleDelete()
                  }
                }}
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Context Menu */}
      {showContextMenu && (
        <div
          className="fixed z-50 bg-white border border-gray-200 rounded-lg shadow-lg py-1 min-w-[160px]"
          style={{ left: showContextMenu.x, top: showContextMenu.y }}
        >
          <button
            onClick={() => {
              if (itemInContext) {
                setRenameName(itemInContext.name)
                setShowRenameModal(true)
                setShowContextMenu(null)
              }
            }}
            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center space-x-2"
          >
            <Edit className="h-4 w-4" />
            <span>Rename</span>
          </button>
          <button
            onClick={() => {
              if (itemInContext) {
                setShowDeleteModal(true)
                setShowContextMenu(null)
              }
            }}
            className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 flex items-center space-x-2"
          >
            <Trash2 className="h-4 w-4" />
            <span>Delete</span>
          </button>
        </div>
      )}

      {/* Click outside to close context menu */}
      {showContextMenu && (
        <div
          className="fixed inset-0 z-40"
          onClick={() => {
            setShowContextMenu(null)
          }}
        />
      )}
    </div>
  )
}
