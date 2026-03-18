package com.lbo.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.lbo.app.data.model.Category
import com.lbo.app.domain.repository.CategoryRepository
import com.lbo.app.utils.Constants
import com.lbo.app.utils.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CategoryRepository {

    private val categoriesCollection = firestore.collection(Constants.COLLECTION_CATEGORIES)

    override suspend fun addCategory(category: Category): Resource<Boolean> {
        return try {
            val docRef = categoriesCollection.document()
            val categoryWithId = category.copy(categoryId = docRef.id)
            docRef.set(categoryWithId.toMap()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add category")
        }
    }

    override suspend fun getCategories(): Resource<List<Category>> {
        return try {
            val snapshot = categoriesCollection.get().await()
            val categories = snapshot.toObjects(Category::class.java)
            Resource.Success(categories)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get categories")
        }
    }

    override suspend fun deleteCategory(categoryId: String): Resource<Boolean> {
        return try {
            categoriesCollection.document(categoryId).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete category")
        }
    }

    override suspend fun updateCategory(category: Category): Resource<Boolean> {
        return try {
            categoriesCollection.document(category.categoryId)
                .set(category.toMap()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update category")
        }
    }
}
