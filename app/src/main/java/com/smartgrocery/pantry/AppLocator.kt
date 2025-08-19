package com.smartgrocery.pantry

import com.smartgrocery.pantry.ui.AppState

object AppLocator {
    // Quick and dirty callback so non-Composable helpers can reach the current app state
    var appStateProvider: (() -> AppState)? = null
}

