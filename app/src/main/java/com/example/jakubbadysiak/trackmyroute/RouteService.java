package com.example.jakubbadysiak.trackmyroute;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RouteService extends Service {
    public RouteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
