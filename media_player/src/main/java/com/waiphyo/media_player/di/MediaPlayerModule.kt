package com.waiphyo.media_player.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.waiphyo.media_player.navigation.MediaPlayerFeatureAPi
import com.waiphyo.media_player.navigation.MediaPlayerImpl

@InstallIn(SingletonComponent::class)
@Module
object MediaPlayerModule {


    @Provides
    fun provideMediaPlayerFeatureApi(): MediaPlayerFeatureAPi {
        return MediaPlayerImpl()
    }

}