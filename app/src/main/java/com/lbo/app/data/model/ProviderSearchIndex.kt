package com.lbo.app.data.model

data class ProviderSearchIndex(
    val providerId: String = "",
    val name: String = "",
    val category: String = "",
    val city: String = "",
    val rating: Double = 0.0,
    val experience: Int = 0
)
