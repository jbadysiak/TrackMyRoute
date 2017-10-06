package com.example.jakubbadysiak.trackmyroute.GSM;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

/**
 * Created by jakub.badysiak on 03.10.2017.
 */

public class MyPhoneListener extends PhoneStateListener {
    public int signalStrengthValue;

    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        if (signalStrength.isGsm()) {
            if (signalStrength.getGsmSignalStrength() != 99)
                signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
            else
                signalStrengthValue = signalStrength.getGsmSignalStrength();
        } else {
            signalStrengthValue = signalStrength.getCdmaDbm();
        }
    }
}
