import axios from 'axios'

const CORE_BASE_URL = process.env.NEXT_PUBLIC_CORE_URL || 'http://localhost:8080/api/v1'

// Match exact Java DTOs
interface FileRequestDto {
  name: string
  base64Content: string
  folderId: number | null
  publicKey: string
}

interface FileResponseDto {
  id: number
  name: string
  fileSize: string
  uploadDate: string
  type: 'FILE'
}

interface FolderCreateDto {
  folderName: string
  parentId: number | null
}

interface FolderResponseDto {
  id: number
  name: string
  creationDate: string
  type: 'FOLDER'
}

interface FileDeleteDto {
  id: number
}

interface FolderDeleteDto {
  id: number
}

interface FolderRenameDto {
  id: number
  newName: string
}

type FileSystemDto = FileResponseDto | FolderResponseDto

interface UploadResponse {
  id: number
}

interface FolderCreateResponse {
  id: number
}

class FileService {
  private api = axios.create({
    baseURL: CORE_BASE_URL,
    headers: {
      'Content-Type': 'application/json',
    },
    withCredentials: true, // Important for cookies
  })

  async getFiles(folderId?: number): Promise<FileSystemDto[]> {
    try {
      const params = folderId ? { folderId } : {}
      const response = await this.api.get<FileSystemDto[]>('/folder/get-content', { params })
      return response.data
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 401) {
          throw new Error('Authentication required. Please log in again')
        } else if (error.response?.status === 403) {
          throw new Error('Access denied to this folder')
        } else if (error.response?.status === 404) {
          throw new Error('Folder not found')
        } else if (error.response?.status === 500) {
          throw new Error('Server error. Please try again later')
        }
      }
      throw new Error('Failed to fetch files')
    }
  }

  async uploadFile(file: File, folderId?: number, publicKey?: string): Promise<UploadResponse> {
    try {
      // Convert file to base64
      const base64Content = await this.fileToBase64(file)
      
      const fileDto: FileRequestDto = {
        name: file.name,
        base64Content,
        folderId: folderId || null,
        publicKey: publicKey || ''
      }

      const response = await this.api.post<UploadResponse>('/file/upload', fileDto)
      return response.data
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 400) {
          const errorMessage = error.response.data?.message || 'Invalid file data'
          throw new Error(errorMessage)
        } else if (error.response?.status === 401) {
          throw new Error('Authentication required. Please log in again')
        } else if (error.response?.status === 403) {
          throw new Error('Access denied to upload to this folder')
        } else if (error.response?.status === 413) {
          throw new Error('File too large')
        } else if (error.response?.status === 500) {
          throw new Error('Server error during upload. Please try again')
        }
      }
      throw new Error('File upload failed')
    }
  }

  async downloadFile(fileId: number): Promise<Blob> {
    try {
      const response = await this.api.get(`/file/download?fileId=${fileId}`, {
        responseType: 'blob',
      })
      return response.data
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 401) {
          throw new Error('Authentication required. Please log in again')
        } else if (error.response?.status === 403) {
          throw new Error('Access denied to this file')
        } else if (error.response?.status === 404) {
          throw new Error('File not found')
        } else if (error.response?.status === 500) {
          throw new Error('Server error during download. Please try again')
        }
      }
      throw new Error('File download failed')
    }
  }

  async deleteFile(fileId: number): Promise<void> {
    try {
      const deleteDto: FileDeleteDto = { id: fileId }
      await this.api.delete('/file/delete', { data: deleteDto })
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 401) {
          throw new Error('Authentication required. Please log in again')
        } else if (error.response?.status === 403) {
          throw new Error('Access denied to delete this file')
        } else if (error.response?.status === 404) {
          throw new Error('File not found')
        } else if (error.response?.status === 500) {
          throw new Error('Server error during deletion. Please try again')
        }
      }
      throw new Error('File deletion failed')
    }
  }

  async createFolder(name: string, parentId?: number): Promise<FolderCreateResponse> {
    try {
      const folderDto: FolderCreateDto = {
        folderName: name,
        parentId: parentId || null
      }
      const response = await this.api.post<FolderCreateResponse>('/folder/create', folderDto)
      return response.data
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 400) {
          const errorMessage = error.response.data?.message || 'Invalid folder data'
          throw new Error(errorMessage)
        } else if (error.response?.status === 401) {
          throw new Error('Authentication required. Please log in again')
        } else if (error.response?.status === 403) {
          throw new Error('Access denied to create folder here')
        } else if (error.response?.status === 409) {
          throw new Error('Folder with this name already exists')
        } else if (error.response?.status === 500) {
          throw new Error('Server error during folder creation. Please try again')
        }
      }
      throw new Error('Folder creation failed')
    }
  }

  async deleteFolder(folderId: number): Promise<void> {
    try {
      const deleteDto: FolderDeleteDto = { id: folderId }
      await this.api.delete('/folder/delete', { data: deleteDto })
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 401) {
          throw new Error('Authentication required. Please log in again')
        } else if (error.response?.status === 403) {
          throw new Error('Access denied to delete this folder')
        } else if (error.response?.status === 404) {
          throw new Error('Folder not found')
        } else if (error.response?.status === 500) {
          throw new Error('Server error during folder deletion. Please try again')
        }
      }
      throw new Error('Folder deletion failed')
    }
  }

  async renameFolder(folderId: number, newName: string): Promise<void> {
    try {
      const renameDto: FolderRenameDto = { id: folderId, newName }
      await this.api.patch('/folder/rename', renameDto)
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 400) {
          const errorMessage = error.response.data?.message || 'Invalid folder name'
          throw new Error(errorMessage)
        } else if (error.response?.status === 401) {
          throw new Error('Authentication required. Please log in again')
        } else if (error.response?.status === 403) {
          throw new Error('Access denied to rename this folder')
        } else if (error.response?.status === 404) {
          throw new Error('Folder not found')
        } else if (error.response?.status === 409) {
          throw new Error('Folder with this name already exists')
        } else if (error.response?.status === 500) {
          throw new Error('Server error during folder rename. Please try again')
        }
      }
      throw new Error('Folder rename failed')
    }
  }

  private async fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.readAsDataURL(file)
      reader.onload = () => {
        const result = reader.result as string
        // Remove data:application/pdf;base64, prefix
        const base64 = result.split(',')[1]
        resolve(base64)
      }
      reader.onerror = error => reject(error)
    })
  }
}

export const fileService = new FileService() 
