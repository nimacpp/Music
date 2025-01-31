package com.nioxteam.music

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.nioxteam.music.ui.theme.MusicTheme
import kotlinx.coroutines.delay

class PlayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val musicFile = intent.getParcelableExtra<MusicFile>("Music")
        setContent {
            MusicTheme {
                val musicFile: MusicFile? = intent.getParcelableExtra("Music")
                // Handle the nullable MusicFile
                if (musicFile != null) {
                    // Use the musicFile object as needed
                    PlayS(musicFile)
                } else {
                    // Handle the case where the MusicFile is null
                    // Show an error message or default screen
                    Text("error")
                }
            }
        }
    }
}


fun uriExists(context: Context, uriString: String): Boolean {
    val uri = Uri.parse(uriString)
    return try {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            true
        } ?: false
    } catch (e: Exception) {
        Log.e("URI Check", "Error checking URI: ${e.message}")
        false
    }
}

@Composable
fun PlayS(musicFile: MusicFile, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val albumArtUri = getAlbumArtUri(musicFile.albumId)
    var isPlaying by remember { mutableStateOf(false) }
    var repeat by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer().apply { setDataSource(musicFile.path); prepare() } }
    val totalDuration = mediaPlayer.duration.toFloat()
    var progress by remember { mutableStateOf(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (mediaPlayer.isPlaying) {
                progress = mediaPlayer.currentPosition.toFloat()
                delay(1000L)
            }
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconRow()
        Spacer(Modifier.height(30.dp))
        Log.e("test",uriExists(context ,musicFile.albumId.toString()).toString())
        Log.e("test", musicFile.albumId.toString())
        Image(
            painter = rememberImagePainter(
                data = albumArtUri,
                builder = {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                }
            ),
            contentDescription = "Album Art",
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.height(40.dp))

        CustomRow(musicFile.title, musicFile.artist)
        Slider(
            value = progress,
            onValueChange = { newProgress ->
                progress = newProgress
                mediaPlayer.seekTo(newProgress.toInt())
            },
            valueRange = 0f..totalDuration,
            modifier = Modifier.fillMaxWidth()
        )
        if (repeat){
            if (totalDuration == progress) {
                mediaPlayer.seekTo(0)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(id = R.drawable.shuffle), contentDescription = "Icon 1", modifier = Modifier.size(24.dp))
            Icon(painter = painterResource(id = R.drawable.back), contentDescription = "Icon 2", modifier = Modifier.size(24.dp))
                    if (isPlaying) {
                        Icon(
                            painter = painterResource(id = R.drawable.pause),
                            contentDescription = "Pause Icon",
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    mediaPlayer.pause()
                                    isPlaying = false
                                }
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.play),
                            contentDescription = "Play Icon",
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    mediaPlayer.start()
                                    isPlaying = true
                                }
                        )
                    }
                DisposableEffect(Unit) {
                    onDispose {
                        mediaPlayer.release()
                    }
                }
            Icon(painter = painterResource(id = R.drawable.forward), contentDescription = "Icon 4", modifier = Modifier.size(24.dp))

            Icon(
                painter = painterResource(id = R.drawable.repeat_one),
                contentDescription = "Icon 5",
                tint = if (repeat) Color.Green else Color.Black ,
                modifier = Modifier.size(24.dp).clickable {
                    repeat = !repeat
                }
            )
        }
}
}
@Composable
fun CustomRow(name :String,singer :String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium, fontSize = 20.sp )
            Text(text = singer, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        Icon(
            painter = painterResource(id = R.drawable.add_circle),
            contentDescription = "Right Icon",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun IconRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(id = R.drawable.arrow_back), contentDescription = "Left Icon", modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.weight(1f))
        Icon(painter = painterResource(id = R.drawable.share), contentDescription = "Right Icon 1", modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Icon(painter = painterResource(id = R.drawable.bar_chart), contentDescription = "Right Icon 2", modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Icon(painter = painterResource(id = R.drawable.more), contentDescription = "Right Icon 3", modifier = Modifier.size(24.dp))
    }
}





