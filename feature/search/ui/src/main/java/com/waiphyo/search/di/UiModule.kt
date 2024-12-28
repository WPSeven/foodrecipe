package com.waiphyo.search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.waiphyo.search.navigation.SearchFeatureApi
import com.waiphyo.search.navigation.SearchFeatureApiImpl

@InstallIn(SingletonComponent::class)
@Module
object UiModule {


    @Provides
    fun provideSearchFeatureApi(): SearchFeatureApi {
        return SearchFeatureApiImpl()
    }

}