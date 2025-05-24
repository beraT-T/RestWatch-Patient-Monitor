package com.example.hastabakimapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// Eğer R.mipmap.ic_launcher bulunamazsa, projenin mipmap klasöründeki
// varsayılan ikon adını kullan veya kendi ikonunu ekle.
// Genellikle Android Studio yeni proje oluşturduğunda bu isimde bir ikon olur.
// Eğer import R hatası alırsan, en tepede kendi paket adınla import et:
// import com.example.yourproject.R (KENDİ PAKET ADINLA DEĞİŞTİR)


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMsgService" // Logcat'te filtrelemek için etiket

    /**
     * Firebase'den yeni bir mesaj alındığında çağrılır (uygulama ön plandaysa veya
     * sadece data payload içeren bir bildirimse).
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage) // Bunu silme

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Gelen mesajın "notification" payload'ını kontrol et
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Title: ${it.title}")
            Log.d(TAG, "Message Notification Body: ${it.body}")

            // Lokal bir bildirim oluşturup göster
            sendLocalNotification(it.title, it.body)
        }

        // Gelen mesajın "data" payload'ını kontrol et (eğer gönderildiyse)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            // Data payload'ını işleyebilirsin. Örneğin, bir başlık ve mesaj çıkarıp
            // sendLocalNotification ile gösterebilirsin veya farklı bir işlem yapabilirsin.
            // val titleFromData = remoteMessage.data["title"]
            // val bodyFromData = remoteMessage.data["body"]
            // if (titleFromData != null && bodyFromData != null && remoteMessage.notification == null) {
            //    sendLocalNotification(titleFromData, bodyFromData)
            // }
        }
    }

    /**
     * FCM kayıt token'ı güncellendiğinde veya ilk kez oluşturulduğunda çağrılır.
     * Bu token, belirli bir cihaza bildirim göndermek için gereklidir.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token) // Bunu silme
        Log.d(TAG, "Refreshed token: $token")

        // BU TOKEN'I KOPYALAYIP RASPBERRY PI'DEKİ PYTHON SCRIPT'İNE YAPIŞTIRACAKSIN!
        // Token'ı sunucuna gönderebilirsin, ama senin senaryonda Pi'ye manuel olarak ekleyeceksin.
        // Örnek: sendRegistrationToServer(token)
    }

    /**
     * Alınan mesajı kullanarak lokal bir Android bildirimi oluşturur ve gösterir.
     */
    private fun sendLocalNotification(title: String?, messageBody: String?) {
        val channelId = "restwatch_notification_channel" // Bildirim kanalının ID'si
        val channelName = "RestWatch Notifications" // Kullanıcıya görünecek kanal adı

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Uygulamanın varsayılan ikonu (res/mipmap altında)
            .setContentTitle(title ?: getString(R.string.app_name)) // Başlık (eğer null ise uygulama adını kullan)
            .setContentText(messageBody)
            .setAutoCancel(true) // Bildirime tıklandığında otomatik olarak kapanması için
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Bildirimin önceliği (baş üstü gösterimi için önemli)
        // .setSound(defaultSoundUri) // İstersen ses ekleyebilirsin
        // .setContentIntent(pendingIntent) // Bildirime tıklandığında bir aktivite açmak için

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android Oreo (API 26) ve üzeri için Bildirim Kanalı (Notification Channel) oluşturmak zorunludur.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH) // Önem seviyesi (ses ve baş üstü gösterim için)
            notificationManager.createNotificationChannel(channel)
        }

        // Bildirimi göster (0, benzersiz bir ID'dir. Farklı ID'ler farklı bildirimler oluşturur)
        notificationManager.notify(0 /* notification id */, notificationBuilder.build())
    }
}
