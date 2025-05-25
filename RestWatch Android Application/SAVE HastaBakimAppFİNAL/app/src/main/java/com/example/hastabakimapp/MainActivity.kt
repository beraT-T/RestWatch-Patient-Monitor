package com.example.hastabakimapp

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Notification permission granted.")
                // İzin verildi, belki FCM token'ını almak için bir işlem tetiklenebilir
                // veya kullanıcıya bilgi verilebilir.
            } else {
                Log.d("MainActivity", "Notification permission denied.")
                // İzin verilmedi, kullanıcıya neden bu iznin önemli olduğu açıklanabilir.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

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
    var prediction by remember { mutableStateOf("waiting for ip input") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Raspberry Pi IP Adress:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = ipAddress,
            onValueChange = { ipAddress = it },
            label = { Text("for exp. 192.168.1.107") },
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
                        prediction = obj.getString("guess") + " (${(obj.getDouble("percentage") * 100).toInt()}%)"
                    } catch (e: Exception) {
                        prediction = "No guess"
                    }
                    delay(5000)
                }
            }
        }) {
            Text("Connect & Start")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Position: $prediction", style = MaterialTheme.typography.headlineSmall)

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
