package com.pwc.sentinel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SentinelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();

        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){

            if(users != null){

                Intent mServiceIntent = new Intent(context, SentinelService.class);
                mServiceIntent.setAction(SentinelService.ACTION_START_FOREGROUND_SERVICE);
                context.startForegroundService(mServiceIntent);
            }
        }
    }
}

