package com.lbo.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.lbo.app.domain.repository.StorageRepository
import com.lbo.app.utils.Constants
import com.lbo.app.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context
) : StorageRepository {

    override suspend fun uploadProfileImage(userId: String, imageUri: Uri): Resource<String> {
        return try {
            val ref = storage.reference
                .child("${Constants.STORAGE_PROFILE_IMAGES}/$userId/${UUID.randomUUID()}.jpg")
            
            // Compress image to save bandwidth and storage costs
            val compressedBytes = compressImage(imageUri)
            if (compressedBytes != null) {
                ref.putBytes(compressedBytes).await()
            } else {
                ref.putFile(imageUri).await() // Fallback
            }
            
            val downloadUrl = ref.downloadUrl.await().toString()
            Resource.Success(downloadUrl)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to upload profile image")
        }
    }

    override suspend fun uploadDocument(userId: String, documentUri: Uri): Resource<String> {
        return try {
            val ref = storage.reference
                .child("${Constants.STORAGE_DOCUMENTS}/$userId/${UUID.randomUUID()}")
            ref.putFile(documentUri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Resource.Success(downloadUrl)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to upload document")
        }
    }

    override suspend fun deleteFile(fileUrl: String): Resource<Boolean> {
        return try {
            storage.getReferenceFromUrl(fileUrl).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete file")
        }
    }

    private fun compressImage(uri: Uri): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (bitmap == null) return null
            
            val outputStream = ByteArrayOutputStream()
            // Compress to JPEG with 70% quality (greatly reduces size, maintains visual quality)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val bytes = outputStream.toByteArray()
            outputStream.close()
            bytes
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
