package com.waiphyo.search

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.Espresso.pressBack
import com.waiphyo.common.navigation.NavigationRoute
import com.waiphyo.search.domain.use_cases.DeleteRecipeUseCase
import com.waiphyo.search.domain.use_cases.GetAllRecipeUseCase
import com.waiphyo.search.domain.use_cases.GetAllRecipesFromLocalDbUseCase
import com.waiphyo.search.domain.use_cases.GetRecipeDetailsUseCase
import com.waiphyo.search.domain.use_cases.InsertRecipeUseCase
import com.waiphyo.search.repository.FakeFailureRepoIMpl
import com.waiphyo.search.repository.FakeFailureRepoIMpl.Companion.errorMessage
import com.waiphyo.search.repository.FakeSuccessRepoImpl
import com.waiphyo.search.screens.details.RecipeDetails
import com.waiphyo.search.screens.details.RecipeDetailsScreen
import com.waiphyo.search.screens.details.RecipeDetailsScreenTestTag
import com.waiphyo.search.screens.details.RecipeDetailsViewModel
import com.waiphyo.search.screens.favorite.FavoriteScreen
import com.waiphyo.search.screens.favorite.FavoriteScreenTestTag
import com.waiphyo.search.screens.favorite.FavoriteViewModel
import com.waiphyo.search.screens.recipe_list.RecipeList
import com.waiphyo.search.screens.recipe_list.RecipeListScreen
import com.waiphyo.search.screens.recipe_list.RecipeListScreenTestTag
import com.waiphyo.search.screens.recipe_list.RecipeListViewModel
import com.waiphyo.search.utils.getRecipeResponse
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// First Approach of writing UI Testing

class FeatureSearchUiTesting {

    @get:Rule
    val composeRule = createComposeRule()


    private lateinit var getAllRecipeListUseCase: GetAllRecipeUseCase
    private lateinit var getRecipeDetailsUseCase: GetRecipeDetailsUseCase
    private lateinit var insertRecipeUseCase: InsertRecipeUseCase
    private lateinit var deleteRecipeUseCase: DeleteRecipeUseCase
    private lateinit var getAlRecipesFromLocalDbUseCase: GetAllRecipesFromLocalDbUseCase


    private lateinit var fakeSuccessRepo: FakeSuccessRepoImpl
    private lateinit var fakeFailureRepo: FakeFailureRepoIMpl


    @Before
    fun setUp() {
        fakeSuccessRepo = FakeSuccessRepoImpl()
        fakeFailureRepo = FakeFailureRepoIMpl()
    }

    @After
    fun tearDown() {
        fakeSuccessRepo.reset()
        fakeFailureRepo.reset()
    }

    private fun initSuccessUseCase() {
        getAllRecipeListUseCase = GetAllRecipeUseCase(fakeSuccessRepo)
        getRecipeDetailsUseCase = GetRecipeDetailsUseCase(fakeSuccessRepo)
        insertRecipeUseCase = InsertRecipeUseCase(fakeSuccessRepo)
        deleteRecipeUseCase = DeleteRecipeUseCase(fakeSuccessRepo)
        getAlRecipesFromLocalDbUseCase = GetAllRecipesFromLocalDbUseCase(fakeSuccessRepo)
    }

    private fun initFailureUseCase() {
        getAllRecipeListUseCase = GetAllRecipeUseCase(fakeFailureRepo)
        getRecipeDetailsUseCase = GetRecipeDetailsUseCase(fakeFailureRepo)
        insertRecipeUseCase = InsertRecipeUseCase(fakeFailureRepo)
        deleteRecipeUseCase = DeleteRecipeUseCase(fakeFailureRepo)
        getAlRecipesFromLocalDbUseCase = GetAllRecipesFromLocalDbUseCase(fakeFailureRepo)
    }


    private fun testingEnv() {
        val recipeListViewModel = RecipeListViewModel(getAllRecipeListUseCase)
        val recipeDetailsViewModel = RecipeDetailsViewModel(
            getRecipeDetailsUseCase,
            deleteRecipeUseCase,
            insertRecipeUseCase
        )
        val favoriteViewModel =
            FavoriteViewModel(getAlRecipesFromLocalDbUseCase, deleteRecipeUseCase)

        composeRule.setContent {
            val navHostController = rememberNavController()
            NavHost(
                navController = navHostController,
                startDestination = NavigationRoute.RecipeList.route
            ) {
                composable(route = NavigationRoute.RecipeList.route) {
                    RecipeListScreen(
                        viewModel = recipeListViewModel,
                        navHostController = navHostController
                    ) { mealId ->
                        recipeListViewModel.onEvent(RecipeList.Event.GoToRecipeDetails(mealId))
                    }

                }

                composable(route = NavigationRoute.RecipeDetails.route) {
                    val mealId = it.arguments?.getString("id")
                    LaunchedEffect(key1 = mealId) {
                        mealId?.let {
                            recipeDetailsViewModel.onEvent(RecipeDetails.Event.FetchRecipeDetails(it))
                        }
                    }
                    RecipeDetailsScreen(
                        viewModel = recipeDetailsViewModel,
                        onNavigationClick = {
                            recipeDetailsViewModel.onEvent(RecipeDetails.Event.GoToRecipeListScreen)
                        },
                        onFavoriteClick = {
                            recipeDetailsViewModel.onEvent(RecipeDetails.Event.InsertRecipe(it))
                        },
                        onDelete = {
                            recipeDetailsViewModel.onEvent(RecipeDetails.Event.DeleteRecipe(it))
                        }, navHostController = navHostController
                    )
                }

                composable(NavigationRoute.FavoriteScreen.route) {
                    FavoriteScreen(
                        navHostController = navHostController,
                        viewModel = favoriteViewModel,
                        onClick = { mealId ->
                            favoriteViewModel.onEvent(FavoriteScreen.Event.GoToDetails(mealId))
                        })
                }

            }
        }

    }

    @Test
    fun test_recipeListSuccess() {
        initSuccessUseCase()
        testingEnv()
        with(composeRule) {
            onNodeWithTag(RecipeListScreenTestTag.SEARCH).performClick()
            onNodeWithTag(RecipeListScreenTestTag.SEARCH).performTextInput("chicken")

            onNodeWithTag(RecipeListScreenTestTag.LAZY_COL).assertIsDisplayed()
            onNodeWithTag(RecipeListScreenTestTag.LAZY_COL).onChildAt(0)
                .assert(hasTestTag(getRecipeResponse().first().strMeal.plus(0)))
        }
    }

 @Test
    fun test_recipeLIstFailure() {
        initFailureUseCase()
        testingEnv()
        with(composeRule) {
            onNodeWithTag(RecipeListScreenTestTag.SEARCH).performClick()
            onNodeWithTag(RecipeListScreenTestTag.SEARCH).performTextInput("chicken")

            onNodeWithText(errorMessage).assertIsDisplayed()
        }
    }

       @Test
       fun test_recipeListSuccess_recipeDetailsSuccess() {
           initSuccessUseCase()
           testingEnv()
           with(composeRule) {
               onNodeWithTag(RecipeListScreenTestTag.SEARCH).performClick()
               onNodeWithTag(RecipeListScreenTestTag.SEARCH).performTextInput("chicken")
               onNodeWithTag(RecipeListScreenTestTag.LAZY_COL).assertIsDisplayed()
               onNodeWithTag(RecipeListScreenTestTag.LAZY_COL).onChildAt(0)
                   .assert(hasTestTag(getRecipeResponse().first().strMeal.plus(0)))
               onNodeWithTag(getRecipeResponse().first().strMeal.plus(0)).performClick()
               waitForIdle()
               onNodeWithText(getRecipeResponse().first().strMeal).assertIsDisplayed()

           }

       }

@Test
    fun test_insertion() {
        initSuccessUseCase()
        testingEnv()
        with(composeRule) {

            onNodeWithTag(RecipeListScreenTestTag.SEARCH).performClick()
            onNodeWithTag(RecipeListScreenTestTag.SEARCH).performTextInput("chicken")
            onNodeWithTag(RecipeListScreenTestTag.LAZY_COL).assertIsDisplayed()
            onNodeWithTag(RecipeListScreenTestTag.LAZY_COL).onChildAt(0)
                .assert(hasTestTag(getRecipeResponse().first().strMeal.plus(0)))
            onNodeWithTag(getRecipeResponse().first().strMeal.plus(0)).performClick()

            onNodeWithTag(RecipeDetailsScreenTestTag.INSERT).performClick()

            onNodeWithTag(RecipeDetailsScreenTestTag.ARROW_BACK).performClick()

            onNodeWithTag(RecipeListScreenTestTag.FLOATING_ACTION_BTN).performClick()
            waitForIdle()
            onNodeWithText(getRecipeResponse().first().strMeal).assertIsDisplayed()

        }
    }

      @Test
      fun test_delete() {
          initSuccessUseCase()
          testingEnv()
          with(composeRule) {

              onNodeWithTag(RecipeListScreenTestTag.SEARCH).performClick()
              onNodeWithTag(RecipeListScreenTestTag.SEARCH).performTextInput("chicken")
              onNodeWithTag(RecipeListScreenTestTag.LAZY_COL).assertIsDisplayed()
              onNodeWithTag(RecipeListScreenTestTag.LAZY_COL).onChildAt(0)
                  .assert(hasTestTag(getRecipeResponse().first().strMeal.plus(0)))
              onNodeWithTag(getRecipeResponse().first().strMeal.plus(0)).performClick()

              onNodeWithTag(RecipeDetailsScreenTestTag.INSERT).performClick()

              onNodeWithTag(RecipeDetailsScreenTestTag.ARROW_BACK).performClick()

              onNodeWithTag(RecipeListScreenTestTag.FLOATING_ACTION_BTN).performClick()
              waitForIdle()
              onNodeWithText(getRecipeResponse().first().strMeal).assertIsDisplayed()

              onNodeWithTag(FavoriteScreenTestTag.DELETE).performClick()

              onNodeWithText("Nothing Found").assertIsDisplayed()
          }
      }


        @Test
        fun test_alphabetical() {
            initSuccessUseCase()
            testingEnv()
            with(composeRule) {
                onNodeWithTag(RecipeListScreenTestTag.SEARCH).performClick()
                onNodeWithTag(RecipeListScreenTestTag.SEARCH).performTextInput("chicken")

                onNodeWithTag(RecipeListScreenTestTag.LAZY_COL)
                    .onChildAt(0).performClick()

                onNodeWithTag(RecipeDetailsScreenTestTag.INSERT).performClick()

                onNodeWithTag(RecipeDetailsScreenTestTag.ARROW_BACK).performClick()

                onNodeWithTag(RecipeListScreenTestTag.LAZY_COL)
                    .performScrollToNode(
                        hasTestTag(getRecipeResponse().last().strMeal.plus(1))
                    )

                onNodeWithTag(RecipeListScreenTestTag.LAZY_COL).onChildAt(1).performClick()

                onNodeWithTag(RecipeDetailsScreenTestTag.INSERT).performClick()
                onNodeWithTag(RecipeDetailsScreenTestTag.ARROW_BACK).performClick()

                onNodeWithTag(RecipeListScreenTestTag.FLOATING_ACTION_BTN).performClick()

                onNodeWithTag(FavoriteScreenTestTag.DROP_DOWN).performClick()
                onNodeWithTag(FavoriteScreenTestTag.ALPHABETICAL).performClick()

                onNodeWithTag(FavoriteScreenTestTag.LAZY_COL)
                    .onChildAt(0)
                    .assert(hasTestTag(getRecipeResponse().first().strMeal.plus(0)))

                onNodeWithTag(FavoriteScreenTestTag.LAZY_COL)
                    .onChildAt(1)
                    .assert(hasTestTag(getRecipeResponse().last().strMeal.plus(1)))


            }

        }


        @Test
        fun test_less_ingredients() {
            initSuccessUseCase()
            testingEnv()
            with(composeRule) {
                onNodeWithTag(RecipeListScreenTestTag.SEARCH).performClick()
                onNodeWithTag(RecipeListScreenTestTag.SEARCH).performTextInput("chicken")

                onNodeWithTag(RecipeListScreenTestTag.LAZY_COL)
                    .onChildAt(0).performClick()

                onNodeWithTag(RecipeDetailsScreenTestTag.INSERT).performClick()

                onNodeWithTag(RecipeDetailsScreenTestTag.ARROW_BACK).performClick()

                onNodeWithTag(RecipeListScreenTestTag.LAZY_COL)
                    .performScrollToNode(
                        hasTestTag(getRecipeResponse().last().strMeal.plus(1))
                    )

                onNodeWithTag(RecipeListScreenTestTag.LAZY_COL).onChildAt(1).performClick()

                onNodeWithTag(RecipeDetailsScreenTestTag.INSERT).performClick()
                onNodeWithTag(RecipeDetailsScreenTestTag.ARROW_BACK).performClick()

                onNodeWithTag(RecipeListScreenTestTag.FLOATING_ACTION_BTN).performClick()

                onNodeWithTag(FavoriteScreenTestTag.DROP_DOWN).performClick()
                onNodeWithTag(FavoriteScreenTestTag.LESS_INGREDIENT).performClick()

                onNodeWithTag(FavoriteScreenTestTag.LAZY_COL)
                    .onChildAt(0)
                    .assert(hasTestTag(getRecipeResponse().last().strMeal.plus(0)))

                onNodeWithTag(FavoriteScreenTestTag.LAZY_COL)
                    .onChildAt(1)
                    .assert(hasTestTag(getRecipeResponse().first().strMeal.plus(1)))


            }
        }


          @Test
          fun test_resetSort() {
              initSuccessUseCase()
              testingEnv()
              with(composeRule) {
                  onNodeWithTag(RecipeListScreenTestTag.SEARCH).performClick()
                  onNodeWithTag(RecipeListScreenTestTag.SEARCH).performTextInput("chicken")

                  onNodeWithTag(RecipeListScreenTestTag.LAZY_COL)
                      .onChildAt(0).performClick()

                  onNodeWithTag(RecipeDetailsScreenTestTag.INSERT).performClick()

                  onNodeWithTag(RecipeDetailsScreenTestTag.ARROW_BACK).performClick()

                  onNodeWithTag(RecipeListScreenTestTag.LAZY_COL)
                      .performScrollToNode(
                          hasTestTag(getRecipeResponse().last().strMeal.plus(1))
                      )

                  onNodeWithTag(RecipeListScreenTestTag.LAZY_COL).onChildAt(1).performClick()

                  onNodeWithTag(RecipeDetailsScreenTestTag.INSERT).performClick()
                  onNodeWithTag(RecipeDetailsScreenTestTag.ARROW_BACK).performClick()

                  onNodeWithTag(RecipeListScreenTestTag.FLOATING_ACTION_BTN).performClick()

                  onNodeWithTag(FavoriteScreenTestTag.DROP_DOWN).performClick()
                  onNodeWithTag(FavoriteScreenTestTag.LESS_INGREDIENT).performClick()

                  onNodeWithTag(FavoriteScreenTestTag.LAZY_COL)
                      .onChildAt(0)
                      .assert(hasTestTag(getRecipeResponse().last().strMeal.plus(0)))

                  onNodeWithTag(FavoriteScreenTestTag.LAZY_COL)
                      .onChildAt(1)
                      .assert(hasTestTag(getRecipeResponse().first().strMeal.plus(1)))


                  onNodeWithTag(FavoriteScreenTestTag.DROP_DOWN).performClick()

                  onNodeWithTag(FavoriteScreenTestTag.RESET).performClick()

                  onNodeWithTag(FavoriteScreenTestTag.LAZY_COL)
                      .onChildAt(0)
                      .assert(hasTestTag(getRecipeResponse().first().strMeal.plus(0)))

                  onNodeWithTag(FavoriteScreenTestTag.LAZY_COL)
                      .onChildAt(1)
                      .assert(hasTestTag(getRecipeResponse().last().strMeal.plus(1)))


              }


          }


          @Test
          fun test_recipeDetailsFromFavorite() {
              initSuccessUseCase()
              testingEnv()
              with(composeRule) {
                  onNodeWithTag(RecipeListScreenTestTag.SEARCH).performClick()
                  onNodeWithTag(RecipeListScreenTestTag.SEARCH).performTextInput("chicken")

                  onNodeWithTag(RecipeListScreenTestTag.LAZY_COL).onChildAt(0).performClick()
                  onNodeWithTag(RecipeDetailsScreenTestTag.INSERT).performClick()

                  onNodeWithTag(RecipeDetailsScreenTestTag.ARROW_BACK).performClick()

                  onNodeWithTag(RecipeListScreenTestTag.FLOATING_ACTION_BTN).performClick()

                  onNodeWithTag(FavoriteScreenTestTag.LAZY_COL).onChildAt(0)
                      .assert(hasTestTag(getRecipeResponse().first().strMeal.plus(0)))
                      .performClick()


                  onNodeWithText(getRecipeResponse().first().strMeal).assertIsDisplayed()


              }


          }


          @Test
          fun test_deleteFromRecipeDetails() {
              initSuccessUseCase()
              testingEnv()
              with(composeRule) {

                  onNodeWithTag(RecipeListScreenTestTag.SEARCH).performClick()
                  onNodeWithTag(RecipeListScreenTestTag.SEARCH).performTextInput("chicken")
                  onNodeWithTag(RecipeListScreenTestTag.LAZY_COL).onChildAt(0).performClick()

                  onNodeWithTag(RecipeDetailsScreenTestTag.INSERT).performClick()
                  onNodeWithTag(RecipeDetailsScreenTestTag.ARROW_BACK).performClick()

                  onNodeWithTag(RecipeListScreenTestTag.FLOATING_ACTION_BTN).performClick()

                  onNodeWithTag(FavoriteScreenTestTag.LAZY_COL)
                      .onChildAt(0)
                      .assert(hasTestTag(getRecipeResponse().first().strMeal.plus(0)))

                  pressBack()

                  onNodeWithTag(RecipeListScreenTestTag.LAZY_COL)
                      .onChildAt(0).performClick()

                  onNodeWithTag(RecipeDetailsScreenTestTag.DELETE).performClick()

                  onNodeWithTag(RecipeDetailsScreenTestTag.ARROW_BACK).performClick()

                  onNodeWithTag(RecipeListScreenTestTag.FLOATING_ACTION_BTN).performClick()

                  onNodeWithText("Nothing Found").assertIsDisplayed()


              }
          }


}