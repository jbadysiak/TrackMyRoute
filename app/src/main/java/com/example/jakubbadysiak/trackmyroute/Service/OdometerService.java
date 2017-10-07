package com.example.jakubbadysiak.trackmyroute.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by jakub.badysiak on 07.10.2017.
 */

public class OdometerService extends Service {

    private final IBinder binder = new OdometerBinder();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class OdometerBinder extends Binder {
        OdometerService getOdometer(){
            return OdometerService.this;
        }
    }
}
