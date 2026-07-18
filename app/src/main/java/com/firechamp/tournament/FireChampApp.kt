package com.firechamp.tournament

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Fire Champ Application class
 * Hilt ka entry point - sabhi @AndroidEntryPoint annotated classes
 * (Activities, Fragments, ViewModels etc.) iske through dependencies receive karte hain.
 */
@HiltAndroidApp
class FireChampApp : Application()
