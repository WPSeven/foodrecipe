package com.waiphyo.foodrecipe.repository

import com.waiphyo.search.data.local.RecipeDao
import com.waiphyo.search.domain.model.Recipe
import com.waiphyo.search.domain.model.RecipeDetails
import com.waiphyo.search.domain.repository.SearchRepository
import com.waiphyo.foodrecipe.utils.getRecipeDetailsList
import com.waiphyo.foodrecipe.utils.getRecipeResponse
import kotlinx.coroutines.flow.Flow

class FakeSearchRepository(private val recipeDao: RecipeDao) : SearchRepository {

    override suspend fun getRecipes(s: String): Result<List<Recipe>> {
        return Result.success(getRecipeResponse())
    }

    override suspend fun getRecipeDetails(id: String): Result<RecipeDetails> {
        return getRecipeDetailsList().find { it.idMeal == id }?.let { recipeDetails ->
            Result.success(recipeDetails)
        } ?: run { Result.success(com.waiphyo.foodrecipe.utils.getRecipeDetails()) }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
       recipeDao.insertRecipe(recipe)
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe)
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
    }
}