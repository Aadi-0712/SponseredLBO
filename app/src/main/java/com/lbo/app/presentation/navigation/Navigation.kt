package com.lbo.app.presentation.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

sealed class Screen(val route: String) {
    // Auth
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")

    // Customer
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object ProviderDetail : Screen("provider_detail/{providerId}") {
        fun createRoute(providerId: String) = "provider_detail/$providerId"
    }
    data object Booking : Screen("booking/{providerId}") {
        fun createRoute(providerId: String) = "booking/$providerId"
    }
    data object MyBookings : Screen("my_bookings")
    data object Review : Screen("review/{bookingId}/{providerId}/{providerName}") {
        fun createRoute(bookingId: String, providerId: String, providerName: String) =
            "review/$bookingId/$providerId/$providerName"
    }

    // Provider
    data object ProviderDashboard : Screen("provider_dashboard")
    data object ProviderProfileSetup : Screen("provider_profile_setup")

    // Admin
    data object AdminDashboard : Screen("admin_dashboard")
    data object ManageCategories : Screen("manage_categories")
    data object CreatePost : Screen("create_post")
    data object AllBookings : Screen("all_bookings")

    // Community
    data object Community : Screen("community")

    // Profile
    data object Profile : Screen("profile")
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    // Customer bottom nav
    data object Home : BottomNavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object MyBookings : BottomNavItem("my_bookings", "Bookings", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
    data object Community : BottomNavItem("community", "Community", Icons.Filled.Forum, Icons.Outlined.Forum)
    data object Profile : BottomNavItem("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)

    // Provider bottom nav
    data object ProviderDashboard : BottomNavItem("provider_dashboard", "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard)
    data object ProviderBookings : BottomNavItem("provider_dashboard", "Bookings", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)

    // Admin bottom nav
    data object AdminDashboard : BottomNavItem("admin_dashboard", "Dashboard", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings)
    data object AdminBookings : BottomNavItem("all_bookings", "Bookings", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
}

val customerNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.MyBookings,
    BottomNavItem.Community,
    BottomNavItem.Profile
)

val providerNavItems = listOf(
    BottomNavItem.ProviderDashboard,
    BottomNavItem.Community,
    BottomNavItem.Profile
)

val adminNavItems = listOf(
    BottomNavItem.AdminDashboard,
    BottomNavItem.AdminBookings,
    BottomNavItem.Community,
    BottomNavItem.Profile
)

@Composable
fun LBOBottomNavBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = isSelected,
                onClick = { onItemClick(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
