package com.example.culinair.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.example.culinair.data.local.session.SessionManager
import com.example.culinair.domain.usecase.notifications.DeleteFcmTokenUseCase
import com.example.culinair.domain.usecase.notifications.SaveFcmTokenUseCase
import com.example.culinair.firebase_notification.FCMTokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by John Ralph Dela Rosa on 8/4/2025.
 */
@Module
@InstallIn(SingletonComponent::class)
object SessionModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.dataStoreFile("user_session.preferences_pb") }
        )
    }

    @Provides
    @Singleton
    fun provideSessionManager(dataStore: DataStore<Preferences>): SessionManager {
        return SessionManager(dataStore)
    }

    @Provides
    @Singleton
    fun provideFCMTokenManager(
        saveFcmTokenUseCase: SaveFcmTokenUseCase,
        deleteFcmTokenUseCase: DeleteFcmTokenUseCase,
        sessionManager: SessionManager,
        @ApplicationContext context: Context
    ): FCMTokenManager {
        return FCMTokenManager(
            saveFcmTokenUseCase = saveFcmTokenUseCase,
            deleteFcmTokenUseCase = deleteFcmTokenUseCase,
            sessionManager = sessionManager,
            context = context
        )
    }
}