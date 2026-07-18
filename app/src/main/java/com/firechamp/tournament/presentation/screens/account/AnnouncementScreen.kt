package com.firechamp.tournament.presentation.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firechamp.tournament.data.model.Announcement
import com.firechamp.tournament.data.repository.AnnouncementRepository
import com.firechamp.tournament.presentation.components.SubScreenTopBar
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.PurplePrimary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Announcement ViewModel - Firestore se live announcements stream karta hai.
 */
@HiltViewModel
class AnnouncementViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {

    val announcements: StateFlow<List<Announcement>> = announcementRepository
        .observeAnnouncements()
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}

/**
 * Announcement Screen - admin panel se posted content show karta hai.
 * Real-time Firestore listener se live update hota hai.
 */
@Composable
fun AnnouncementScreen(
    onBack: () -> Unit,
    viewModel: AnnouncementViewModel = hiltViewModel()
) {
    val announcements by viewModel.announcements.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(BlackBackground)) {
        SubScreenTopBar(title = "Announcement", onBack = onBack)
        if (announcements.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PurplePrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No announcements yet", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(announcements, key = { it.id }) { ann ->
                    AnnouncementCard(ann)
                }
            }
        }
    }
}

@Composable
private fun AnnouncementCard(ann: Announcement) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Text(
            text = ann.title,
            color = Color.Black,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = ann.message,
            color = Color(0xFF424242),
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = formatDate(ann.createdAt),
            color = Color(0xFF9E9E9E),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
