package com.firechamp.tournament.di

import com.firechamp.tournament.data.repository.AuthRepository
import com.firechamp.tournament.data.repository.EarnRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module - app-wide dependencies provide karta hai.
 *
 * Abhi yahan sirf explicit @Provides hain, lekin future me Retrofit, Room,
 * Firebase, OkHttp clients etc. ke providers bhi yahan aayenge.
 *
 * Note: AuthRepository aur EarnRepository dono @Inject constructor + @Singleton
 * hain, to Hilt unhe automatically provide kar deta hai. Yahan unka @Provides
 * nahi chahiye - ye sirf clarity ke liye examples ki tarah rakhe hain.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Example: agar repository me @Inject constructor nahi hota to yahan
    // explicit @Provides banana padta. Abhi dono repositories @Inject use
    // kar rahi hain, to ye providers future expansion ke liye ready hain.

    // Task 12 me Retrofit/OkHttp providers yahan add honge:
    // @Provides @Singleton fun provideOkHttpClient(): OkHttpClient = ...
    // @Provides @Singleton fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = ...
    // @Provides @Singleton fun provideApiService(retrofit: Retrofit): ApiService = ...

    // Task 14 me Firebase instances yahan provide honge (mostly already auto-provided)
}

// Type-safe re-exports to verify these classes are accessible at compile time
@Suppress("unused")
private val ensureAuthRepository: AuthRepository? = null
@Suppress("unused")
private val ensureEarnRepository: EarnRepository? = null
