package com.example.cddd2_nhom6.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.cddd2_nhom6.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ThongBaoKhiUngDungTat extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "ThongBaoChannel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "";
        String message = "";

        // Kiểm tra nếu thông điệp chứa dữ liệu
        if (remoteMessage.getData().size() > 0) {
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("message");
            // Hiển thị thông báo khi ứng dụng chạy ngầm hoặc đang hoạt động
            if (title != null && message != null) {
                hienThiThongBao(title, message);
            }
        }

        // Kiểm tra nếu thông điệp chứa thông báo
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
            // Hiển thị thông báo khi ứng dụng đang tắt
            if (title != null && message != null) {
                hienThiThongBao(title, message);
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("ThongBaoKhiUngDungTat", "Token mới nhận được: " + token);
        // Gửi token lên máy chủ của bạn nếu cần
    }

    private void hienThiThongBao(String title, String message) {
        taoKenhThongBao();

        // Tạo và hiển thị thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void taoKenhThongBao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Thông Báo Mới",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
