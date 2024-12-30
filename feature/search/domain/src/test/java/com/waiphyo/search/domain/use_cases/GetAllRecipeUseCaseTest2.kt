package com.waiphyo.search.domain.use_cases

import com.waiphyo.search.domain.model.Recipe
import org.junit.Assert.*
import org.junit.Test
import com.waiphyo.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class GetAllRecipeUseCaseTest2{

    private val searchRepository: SearchRepository = mock()

    @Test
    fun test_success() = runTest {

        `when`(searchRepository.getRecipes("chicken"))
            .thenReturn(
                Result.success(getRecipeResponse())
            )

        val useCase = GetAllRecipeUseCase(searchRepository)


        val response = useCase.invoke("chicken")

        assertEquals(getRecipeResponse(), response.last().data)
    }


    @Test
    fun test_failing() = runTest {
        `when`(searchRepository.getRecipes("chicken"))
            .thenReturn(Result.failure(RuntimeException("error")))

        val useCase = GetAllRecipeUseCase(searchRepository)

        val response = useCase.invoke("chicken")

        assertEquals("error", response.last().message)
    }

    @Test
    fun test_exception() = runTest {
        `when`(searchRepository.getRecipes("chicken"))
            .thenThrow(RuntimeException("error"))

        val useCase = GetAllRecipeUseCase(searchRepository)
        val response = useCase.invoke("chicken")
        assertEquals("error",response.last().message)

    }

    

}





private fun getRecipeResponse(): List<Recipe> {
    return listOf(
        Recipe(
            idMeal = "idMeal",
            strArea = "India",
            strCategory = "category",
            strYoutube = "strYoutube",
            strTags = "tag1,tag2",
            strMeal = "Chicken",
            strMealThumb = "strMealThumb",
            strInstructions = "12",
        ),
        Recipe(
            idMeal = "idMeal",
            strArea = "India",
            strCategory = "category",
            strYoutube = "strYoutube",
            strTags = "tag1,tag2",
            strMeal = "Chicken",
            strMealThumb = "strMealThumb",
            strInstructions = "123",
        )
    )

}