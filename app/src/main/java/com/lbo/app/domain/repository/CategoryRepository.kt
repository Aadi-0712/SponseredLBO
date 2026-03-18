package com.lbo.app.domain.repository

import com.lbo.app.data.model.Category
import com.lbo.app.utils.Resource

interface CategoryRepository {
    suspend fun addCategory(category: Category): Resource<Boolean>
    suspend fun getCategories(): Resource<List<Category>>
    suspend fun deleteCategory(categoryId: String): Resource<Boolean>
    suspend fun updateCategory(category: Category): Resource<Boolean>
}
