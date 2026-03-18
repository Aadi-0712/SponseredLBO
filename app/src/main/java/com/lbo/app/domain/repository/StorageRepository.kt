package com.lbo.app.domain.repository

import android.net.Uri
import com.lbo.app.utils.Resource

interface StorageRepository {
    suspend fun uploadProfileImage(userId: String, imageUri: Uri): Resource<String>
    suspend fun uploadDocument(userId: String, documentUri: Uri): Resource<String>
    suspend fun deleteFile(fileUrl: String): Resource<Boolean>
}
