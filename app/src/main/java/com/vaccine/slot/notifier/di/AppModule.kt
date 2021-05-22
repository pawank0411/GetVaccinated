package com.vaccine.slot.notifier.di

import android.content.Context
import androidx.room.Room
import com.vaccine.slot.notifier.db.AppDatabase
import com.vaccine.slot.notifier.retrofit.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideServerApi(): ApiService {
        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor { chain ->
            val request: Request = chain.request().newBuilder()
                .addHeader("Authority", "cdn-api.co-vin.in")
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Language", "hi_IN")
                .addHeader("User-Agent", "PostmanRuntime/7.28.0")
                .build()
            chain.proceed(request)
        }

        return Retrofit.Builder()
                .baseUrl("https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
                .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(
            @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, AppDatabase::class.java, "Notification Database").build()

    @Provides
    @Singleton
    fun provideNotificationDao(dataBase: AppDatabase) = dataBase.notificationDao()
}