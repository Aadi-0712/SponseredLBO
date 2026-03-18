package com.lbo.app.utils

object Constants {
    // Firestore Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_PROVIDERS = "providers"
    const val COLLECTION_BOOKINGS = "bookings"
    const val COLLECTION_REVIEWS = "reviews"
    const val COLLECTION_COMMUNITY_POSTS = "community_posts"
    const val COLLECTION_CATEGORIES = "categories"
    
    // CQRS Read Collections
    const val COLLECTION_PROVIDER_SEARCH_INDEX = "providerSearchIndex"
    const val COLLECTION_TOP_RATED_PROVIDERS = "topRatedProviders"

    // Storage Paths
    const val STORAGE_PROFILE_IMAGES = "profile_images"
    const val STORAGE_DOCUMENTS = "documents"

    // Notification Topics
    const val TOPIC_COMMUNITY = "community"

    // Preferences
    const val PREF_NAME = "lbo_preferences"
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_ROLE = "user_role"

    // Pagination
    const val PAGE_SIZE = 10

    // Admin Email (change to your admin email)
    const val ADMIN_EMAIL = "admin@lbo.com"
}
