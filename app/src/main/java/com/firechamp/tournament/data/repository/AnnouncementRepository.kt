package com.firechamp.tournament.data.repository

import com.firechamp.tournament.data.model.Announcement
import com.firechamp.tournament.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Announcement Repository - admin panel se post hone wali announcements
 * user app me live stream karta hai.
 */
@Singleton
class AnnouncementRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    fun observeAnnouncements(): Flow<List<Announcement>> =
        firebaseDataSource.observeAnnouncements()
}
