package com.assignment.ytCloneLite

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.assignment.ytCloneLite.model.Video
import com.assignment.ytCloneLite.network.Api
import com.google.android.exoplayer2.Player.REPEAT_MODE_OFF
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoListScreen()
        }
    }
}
@SuppressLint("UnrememberedMutableState")
@Composable
fun VideoListScreen() {
    val scope = rememberCoroutineScope()
    var videos by remember { mutableStateOf(listOf<Video>()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredVideos by derivedStateOf {
        videos.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    LaunchedEffect(key1 = true) {
        scope.launch {
            videos = Api.supabaseService.listVideos()
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(query = searchQuery, onQueryChanged = { searchQuery = it })
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(filteredVideos) { video ->
                    VideoItem(video)
                }
            }
        }
    }
}
@Composable
fun VideoItem(video: Video) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )) {
        Column(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.heightIn(0.dp, 232.dp)) {
                VideoPlayer(videoUrl = video.video_url)
            }

            Text(text = video.title, style = MaterialTheme.typography.headlineMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)){
                Text(text = "Channel: ${video.channel_name}")
                Text(text = "Likes: ${video.likes}")
            }
            Text(text = video.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        placeholder = { Text("Search videos") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") }
    )
}
@Composable
fun VideoPlayer(videoUrl: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = false
            repeatMode = REPEAT_MODE_OFF
        }
    }
    LaunchedEffect(videoUrl) {
        val mediaItem = MediaItem.Builder().setUri(videoUrl).build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }
    AndroidView(
        factory = { ctx ->
            StyledPlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}
