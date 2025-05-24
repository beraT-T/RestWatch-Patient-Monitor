package com.example.hastabakimapp

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.os.Build
import android.util.Log


class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Notification permission granted.")
            } else {
                Log.d("MainActivity", "Notification permission denied.")
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {// Android 13 (API 33)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("MainActivity", "Notification permission already granted.")
            } else {
                Log.d("MainActivity", "Requesting notification permission.")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    IPStreamAndCNNScreen()
                }
            }
        }
    }
}

@Composable
fun IPStreamAndCNNScreen() {
    var ipAddress by remember { mutableStateOf("") }
    var streamUrl by remember { mutableStateOf<String?>(null) }
    var prediction by remember { mutableStateOf("ip girişi bekleniyor") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Raspberry Pi IP Adresi:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = ipAddress,
            onValueChange = { ipAddress = it },
            label = { Text("örn. 192.168.1.107") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            streamUrl = "http://$ipAddress:5000/video"
            // CNN tahminini periyodik olarak çek
            scope.launch {
                while (true) {
                    try {
                        val json = withContext(Dispatchers.IO) {
                            URL("http://$ipAddress:5000/pozisyon").readText()
                        }
                        val obj = JSONObject(json)
                        prediction = obj.getString("tahmin") + " (${(obj.getDouble("oran") * 100).toInt()}%)"
                    } catch (e: Exception) {
                        prediction = "Tahmin alınamadı"
                    }
                    delay(5000)
                }
            }
        }) {
            Text("Bağlan ve Başla")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Hastanın Pozisyonu: $prediction", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        streamUrl?.let { url ->
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.builtInZoomControls = false
                        loadUrl(url)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}
