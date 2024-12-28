package com.waiphyo.foodrecipe.navigation

import com.waiphyo.media_player.navigation.MediaPlayerFeatureAPi
import com.waiphyo.search.navigation.SearchFeatureApi

data class NavigationSubGraphs(
    val searchFeatureApi: SearchFeatureApi,
    val mediaPlayerApi:MediaPlayerFeatureAPi
)
