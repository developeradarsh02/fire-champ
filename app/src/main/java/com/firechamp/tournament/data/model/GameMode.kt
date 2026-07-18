package com.firechamp.tournament.data.model

/**
 * Game mode card - Earn screen grid me dikhta hai.
 * Click karne par Task 3 ka tournament list khulega.
 */
data class GameMode(
    val id: String,
    val name: String,            // "FULL MAP", "CS 1V1", "LW 1V1"
    val label: String,           // Red label bar - "HEAD 2V2", "LW-10 RS", "FREE !!"
    val imageUrl: String? = null // Real image Coil se load hoga
)
