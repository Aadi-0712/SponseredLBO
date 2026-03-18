package com.lbo.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lbo.app.data.model.Booking
import com.lbo.app.data.model.User
import com.lbo.app.presentation.admin.*
import com.lbo.app.presentation.auth.*
import com.lbo.app.presentation.community.CommunityScreen
import com.lbo.app.presentation.customer.*
import com.lbo.app.presentation.profile.ProfileScreen
import com.lbo.app.presentation.provider.*

@Composable
fun LBONavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val startDestination = if (authState.isLoggedIn) {
        when (authState.user?.role) {
            User.ROLE_ADMIN -> Screen.AdminDashboard.route
            User.ROLE_PROVIDER -> Screen.ProviderDashboard.route
            else -> Screen.Home.route
        }
    } else {
        Screen.Login.route
    }

    // Determine bottom nav items based on role
    val bottomNavItems = when (authState.user?.role) {
        User.ROLE_ADMIN -> adminNavItems
        User.ROLE_PROVIDER -> providerNavItems
        else -> customerNavItems
    }

    // Routes that show bottom nav
    val showBottomNav = currentRoute in listOf(
        Screen.Home.route, Screen.MyBookings.route, Screen.Community.route,
        Screen.Profile.route, Screen.ProviderDashboard.route,
        Screen.AdminDashboard.route, Screen.AllBookings.route
    )

    Scaffold(
        bottomBar = {
            if (authState.isLoggedIn && showBottomNav) {
                LBOBottomNavBar(
                    items = bottomNavItems,
                    currentRoute = currentRoute,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ============ AUTH ============
            composable(Screen.Login.route) {
                LoginScreen(
                    authState = authState,
                    onLogin = { email, pwd -> authViewModel.login(email, pwd) },
                    onGoogleSignIn = { /* Google sign-in triggered from activity */ },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Screen.ForgotPassword.route)
                    },
                    onClearError = { authViewModel.clearError() }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    authState = authState,
                    onRegister = { name, email, pwd, role ->
                        authViewModel.register(name, email, pwd, role)
                    },
                    onNavigateToLogin = { navController.popBackStack() },
                    onClearError = { authViewModel.clearError() }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    authState = authState,
                    onResetPassword = { authViewModel.forgotPassword(it) },
                    onBack = { navController.popBackStack() },
                    onClearError = { authViewModel.clearError() }
                )
            }

            // ============ CUSTOMER ============
            composable(Screen.Home.route) {
                val customerViewModel: CustomerViewModel = hiltViewModel()
                val homeState by customerViewModel.homeState.collectAsState()
                HomeScreen(
                    homeState = homeState,
                    onSearch = { navController.navigate(Screen.Search.route) },
                    onCategoryClick = { category ->
                        navController.navigate(Screen.Search.route)
                        // Category filter will be applied in search
                    },
                    onProviderClick = { id ->
                        navController.navigate(Screen.ProviderDetail.createRoute(id))
                    },
                    onViewAllTopRated = { navController.navigate(Screen.Search.route) },
                    onViewAllProviders = { navController.navigate(Screen.Search.route) },
                    onRefresh = { customerViewModel.loadHomeData() }
                )
            }

            composable(Screen.Search.route) {
                val customerViewModel: CustomerViewModel = hiltViewModel()
                val searchState by customerViewModel.searchState.collectAsState()
                SearchScreen(
                    searchState = searchState,
                    onQueryChange = { customerViewModel.searchProviders(it) },
                    onProviderClick = { id ->
                        navController.navigate(Screen.ProviderDetail.createRoute(id))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ProviderDetail.route,
                arguments = listOf(navArgument("providerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val providerId = backStackEntry.arguments?.getString("providerId") ?: ""
                val customerViewModel: CustomerViewModel = hiltViewModel()
                val provider by customerViewModel.selectedProvider.collectAsState()
                val reviews by customerViewModel.providerReviews.collectAsState()

                LaunchedEffect(providerId) {
                    customerViewModel.loadProviderDetails(providerId)
                }

                ProviderDetailScreen(
                    provider = provider,
                    reviews = reviews,
                    onBookNow = {
                        navController.navigate(Screen.Booking.createRoute(providerId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Booking.route,
                arguments = listOf(navArgument("providerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val providerId = backStackEntry.arguments?.getString("providerId") ?: ""
                val customerViewModel: CustomerViewModel = hiltViewModel()
                val provider by customerViewModel.selectedProvider.collectAsState()
                val bookingFormState by customerViewModel.bookingFormState.collectAsState()

                LaunchedEffect(providerId) {
                    customerViewModel.loadProviderDetails(providerId)
                }

                BookingScreen(
                    provider = provider,
                    bookingFormState = bookingFormState,
                    onCreateBooking = { pId, pName, cat, date, time ->
                        customerViewModel.createBooking(pId, pName, cat, date, time)
                    },
                    onBack = { navController.popBackStack() },
                    onBookingSuccess = {
                        customerViewModel.resetBookingForm()
                        navController.popBackStack(Screen.Home.route, false)
                    }
                )
            }

            composable(Screen.MyBookings.route) {
                val customerViewModel: CustomerViewModel = hiltViewModel()
                val bookingsState by customerViewModel.myBookingsState.collectAsState()

                LaunchedEffect(Unit) {
                    customerViewModel.loadMyBookings()
                }

                MyBookingsScreen(
                    bookingsState = bookingsState,
                    onRefresh = { customerViewModel.loadMyBookings() },
                    onReviewClick = { booking ->
                        navController.navigate(
                            Screen.Review.createRoute(
                                booking.bookingId,
                                booking.providerId,
                                booking.providerName
                            )
                        )
                    }
                )
            }

            composable(
                route = Screen.Review.route,
                arguments = listOf(
                    navArgument("bookingId") { type = NavType.StringType },
                    navArgument("providerId") { type = NavType.StringType },
                    navArgument("providerName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
                val providerId = backStackEntry.arguments?.getString("providerId") ?: ""
                val providerName = backStackEntry.arguments?.getString("providerName") ?: ""
                val customerViewModel: CustomerViewModel = hiltViewModel()

                ReviewScreen(
                    bookingId = bookingId,
                    providerId = providerId,
                    providerName = providerName,
                    onSubmitReview = { bId, pId, rating, comment ->
                        customerViewModel.submitReview(bId, pId, rating, comment)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // ============ PROVIDER ============
            composable(Screen.ProviderDashboard.route) {
                val providerViewModel: ProviderViewModel = hiltViewModel()
                val profileState by providerViewModel.profileState.collectAsState()
                val bookingsState by providerViewModel.bookingsState.collectAsState()

                ProviderDashboardScreen(
                    profileState = profileState,
                    bookingsState = bookingsState,
                    onEditProfile = {
                        navController.navigate(Screen.ProviderProfileSetup.route)
                    },
                    onRefresh = { providerViewModel.refreshBookings() },
                    onAcceptBooking = {
                        providerViewModel.updateBookingStatus(it, Booking.STATUS_ACCEPTED)
                    },
                    onRejectBooking = {
                        providerViewModel.updateBookingStatus(it, Booking.STATUS_REJECTED)
                    },
                    onCompleteBooking = {
                        providerViewModel.updateBookingStatus(it, Booking.STATUS_COMPLETED)
                    }
                )
            }

            composable(Screen.ProviderProfileSetup.route) {
                val providerViewModel: ProviderViewModel = hiltViewModel()
                val profileState by providerViewModel.profileState.collectAsState()

                ProviderProfileSetupScreen(
                    profileState = profileState,
                    onSaveProfile = { name, cat, loc, desc, exp, imgUri, docUris ->
                        providerViewModel.saveProfile(name, cat, loc, desc, exp, imgUri, docUris)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // ============ ADMIN ============
            composable(Screen.AdminDashboard.route) {
                val adminViewModel: AdminViewModel = hiltViewModel()
                val state by adminViewModel.dashboardState.collectAsState()

                AdminDashboardScreen(
                    state = state,
                    onRefresh = { adminViewModel.loadDashboard() },
                    onApproveProvider = { adminViewModel.approveProvider(it) },
                    onRejectProvider = { adminViewModel.rejectProvider(it) },
                    onManageCategories = {
                        navController.navigate(Screen.ManageCategories.route)
                    },
                    onCreatePost = {
                        navController.navigate(Screen.CreatePost.route)
                    },
                    onViewAllBookings = {
                        navController.navigate(Screen.AllBookings.route)
                    }
                )
            }

            composable(Screen.ManageCategories.route) {
                val adminViewModel: AdminViewModel = hiltViewModel()
                val state by adminViewModel.dashboardState.collectAsState()

                ManageCategoriesScreen(
                    categories = state.categories,
                    onAddCategory = { name, icon -> adminViewModel.addCategory(name, icon) },
                    onDeleteCategory = { adminViewModel.deleteCategory(it) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.CreatePost.route) {
                val adminViewModel: AdminViewModel = hiltViewModel()

                CreatePostScreen(
                    onCreatePost = { title, desc ->
                        adminViewModel.createCommunityPost(title, desc)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AllBookings.route) {
                val adminViewModel: AdminViewModel = hiltViewModel()
                val state by adminViewModel.dashboardState.collectAsState()

                AllBookingsScreen(
                    bookings = state.allBookings,
                    onBack = { navController.popBackStack() }
                )
            }

            // ============ COMMUNITY ============
            composable(Screen.Community.route) {
                val customerViewModel: CustomerViewModel = hiltViewModel()
                val homeState by customerViewModel.homeState.collectAsState()

                CommunityScreen(
                    posts = homeState.communityPosts,
                    isLoading = homeState.isLoading,
                    onRefresh = { customerViewModel.loadHomeData() }
                )
            }

            // ============ PROFILE ============
            composable(Screen.Profile.route) {
                ProfileScreen(
                    user = authState.user,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }

    // Navigate when auth state changes
    LaunchedEffect(authState.isLoggedIn, authState.user?.role) {
        if (authState.isLoggedIn && authState.user != null) {
            val destination = when (authState.user?.role) {
                User.ROLE_ADMIN -> Screen.AdminDashboard.route
                User.ROLE_PROVIDER -> Screen.ProviderDashboard.route
                else -> Screen.Home.route
            }
            navController.navigate(destination) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}
