package com.nioxteam.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nioxteam.music.ui.theme.MusicTheme
import android.Manifest
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Divider
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicTheme {
                // Shared state for the currently selected song
                var currentSong by remember { mutableStateOf<MusicFile?>(null) }
                // Playback state (playing or paused)
                var isPlaying by remember { mutableStateOf(false) }

                // Handle song playback
                LaunchedEffect(currentSong) {
                    if (currentSong != null) {
                        mediaPlayer?.release() // Release previous media player
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(currentSong!!.path) // Set the music file path
                            prepareAsync() // Prepare the media player asynchronously
                            setOnPreparedListener {
                                if (isPlaying) {
                                    start() // Start playback if isPlaying is true
                                }
                            }
                        }
                    }
                }

                // Handle play/pause state changes
                LaunchedEffect(isPlaying) {
                    if (isPlaying) {
                        mediaPlayer?.start() // Start playback
                    } else {
                        mediaPlayer?.pause() // Pause playback
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Head()
                        MusicTabs(
                            modifier = Modifier.weight(1f),
                            onSongSelected = { song ->
                                currentSong = song
                                isPlaying = true // Auto-play when a song is selected
                            }
                        )
                    }
                    // Show the footer only if a song is selected
                    if (currentSong != null) {
                        Footer(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            currentSong = currentSong!!,
                            isPlaying = isPlaying,
                            onPlayPauseClick = {
                                isPlaying = !isPlaying // Toggle play/pause state
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Release the media player when the activity is destroyed
        mediaPlayer = null
    }
}

@Composable
fun MusicTabs(
    modifier: Modifier = Modifier,
    onSongSelected: (MusicFile) -> Unit // Callback for song selection
) {
    val tabs = listOf("Tracks", "Playlists", "Albums", "Artists")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(modifier = modifier) {
        // Custom TabRow
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    height = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                )
            }
        }

        // Content for each tab
        when (selectedTabIndex) {
            0 -> TracksScreen(onSongSelected = onSongSelected) // Pass callback to TracksScreen
            1 -> PlaylistsScreen()
            2 -> AlbumsScreen()
            3 -> ArtistsScreen()
        }
    }
}


@Composable
fun PlaylistsScreen() {
    // Your Playlists screen content
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Playlists Screen")
    }
}

@Composable
fun AlbumsScreen() {
    // Your Albums screen content
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Albums Screen")
    }
}

@Composable
fun ArtistsScreen() {
    // Your Artists screen content
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Artists Screen")
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TracksScreen(onSongSelected: (MusicFile) -> Unit) {
    val context = LocalContext.current
    val musicFiles = remember { mutableStateListOf<MusicFile>() }

    // Request permission (READ_EXTERNAL_STORAGE or READ_MEDIA_AUDIO)
    val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val permissionState = rememberPermissionState(permission)

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            // Permission granted, fetch music files
            val files = getMusicFiles(context)
            musicFiles.addAll(files.sortedByDescending { it.dateAdded }) // Sort by dateAdded (newest first)
        } else {
            // Request permission if not granted
            permissionState.launchPermissionRequest()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (permissionState.status.isGranted) {
            if (musicFiles.isNotEmpty()) {
                LazyColumn {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${musicFiles.size} Songs",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "Date Added ",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                            )
                        }
                    }
                    items(musicFiles) { musicFile ->
                        MusicItem(
                            musicFile = musicFile,
                            onClick = { onSongSelected(musicFile) } // Notify parent when clicked
                        )
                    }
                }
            } else {
                Text("No music files found.")
            }
        } else {
            Text("Permission to read storage is required.")
        }
    }
}

@Composable
fun MusicItem(
    musicFile: MusicFile,
    onClick: () -> Unit // Callback for item click
) {
    val albumArtUri = getAlbumArtUri(musicFile.albumId)
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() }, // Trigger onClick callback
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Column for the image and two text elements
        Row(
            modifier = Modifier.weight(1f)
        ) {
            Image(
                painter = rememberImagePainter(
                    data = albumArtUri,
                    builder = {
                        crossfade(true)
                        placeholder(R.drawable.ic_launcher_background) // Add a placeholder image
                    }
                ),
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Column(
                modifier = Modifier.padding(top = 5.dp)
            ) {
                Text(
                    text = musicFile.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = musicFile.artist,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        // Icon on the right
        Icon(
            painter = painterResource(id = R.drawable.more),
            contentDescription = "Right Icon",
            modifier = Modifier
                .size(24.dp)
                .clickable { /* Handle click */ },
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun Head() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Text on the left
        Text(
            text = "Music",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground
        )

        // First Icon on the right
        Icon(
            painter = painterResource(id = R.drawable.search),
            contentDescription = "Icon 1",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(24.dp)
                .clickable { /* Handle click for Icon 1 */ }
        )
        Spacer(modifier = Modifier.width(8.dp)) // Add space between the icons

        // Second Icon on the right
        Icon(
            painter = painterResource(id = R.drawable.more),
            contentDescription = "Icon 2",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .size(24.dp)
                .clickable { /* Handle click for Icon 2 */ }
        )
    }
}
@Composable
fun Footer(
    modifier: Modifier = Modifier,
    currentSong: MusicFile, // Current song to display
    isPlaying: Boolean, // Playback state
    onPlayPauseClick: () -> Unit // Callback for play/pause button click
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .border(color = Color.Gray, width = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album Art (Image)
        Image(
            painter = rememberImagePainter(
                data = getAlbumArtUri(currentSong.albumId),
                builder = {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background) // Add a placeholder image
                }
            ),
            contentDescription = "Album Art",
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Title and Artist Name
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = currentSong.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = currentSong.artist,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Play/Pause Icon
        Icon(
            painter = painterResource(
                id = if (isPlaying) R.drawable.pause else R.drawable.play // Toggle between play and pause icons
            ),
            contentDescription = if (isPlaying) "Pause" else "Play",
            modifier = Modifier
                .size(32.dp)
                .clickable { onPlayPauseClick() }, // Trigger play/pause callback
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
fun getMusicFiles(context: Context): List<MusicFile> {
    val musicFiles = mutableListOf<MusicFile>()
    val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media.TITLE, // Music name
        MediaStore.Audio.Media.ARTIST, // Artist name
        MediaStore.Audio.Media.ALBUM_ID, // Album ID (for album art)
        MediaStore.Audio.Media.DATA ,// File path
        MediaStore.Audio.Media.DATE_ADDED
    )
    val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
    val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

    val cursor: Cursor? = context.contentResolver.query(
        uri,
        projection,
        selection,
        null,
        sortOrder
    )

    cursor?.use {
        val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        val pathColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val dateAdded = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
        while (it.moveToNext()) {
            val title = it.getString(titleColumn)
            val artist = it.getString(artistColumn)
            val albumId = it.getLong(albumIdColumn)
            val path = it.getString(pathColumn)
            val dateAdded = it.getLong(dateAdded)
            musicFiles.add(MusicFile(title, artist, albumId, path,dateAdded))
        }
    }

    cursor?.close()
    return musicFiles
}

fun getAlbumArtUri(albumId: Long): Uri {
    return Uri.parse("content://media/external/audio/albumart/$albumId")
}


data class MusicFile(
    val title: String,
    val artist: String,
    val albumId: Long,
    val path: String,
    val dateAdded: Long // New property for date added
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readLong()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeLong(albumId)
        parcel.writeString(path)
        parcel.writeLong(dateAdded)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MusicFile> {
        override fun createFromParcel(parcel: Parcel): MusicFile {
            return MusicFile(parcel)
        }

        override fun newArray(size: Int): Array<MusicFile?> {
            return arrayOfNulls(size)
        }
    }
}
