package com.firechamp.tournament.data.repository

import com.firechamp.tournament.data.model.GameMode
import com.firechamp.tournament.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Game Mode Repository - Earn screen grid ke liye game modes
 * Firestore se live stream karta hai.
 */
@Singleton
class GameModeRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    fun observeGameModes(): Flow<List<GameMode>> =
        firebaseDataSource.observeGameModes()
}
