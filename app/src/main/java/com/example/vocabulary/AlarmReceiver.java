package com.example.vocabulary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.media.Ringtone;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Ringtone ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            if (ringtone != null) {
                ringtone.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
