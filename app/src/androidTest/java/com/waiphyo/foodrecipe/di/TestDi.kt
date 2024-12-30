package com.waiphyo.foodrecipe.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import com.waiphyo.search.data.di.SearchDataModule
import com.waiphyo.search.data.local.RecipeDao
import com.waiphyo.search.domain.repository.SearchRepository
import com.waiphyo.foodrecipe.local.AppDatabase
import com.waiphyo.foodrecipe.repository.FakeSearchRepository
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SearchDataModule::class,DataBaseModule::class]
)
@Module
object TestDi {


    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @Provides
    fun provideRecipeDao(appDatabase: AppDatabase): RecipeDao {
        return appDatabase.getRecipeDao()
    }

    @Provides
    fun provideRepoImpl(recipeDao: RecipeDao): SearchRepository {
        return FakeSearchRepository(recipeDao)
    }


}