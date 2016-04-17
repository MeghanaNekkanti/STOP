package meghana.rohan.saferworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by Rohan on 4/16/2016.
 */
public class receiver extends BroadcastReceiver {

    public static String msg;

    public receiver() {


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;

        String str = "";

//        context.startService(new Intent(context,MyService.class));

        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // For every SMS message received
            for (int i = 0; i < msgs.length; i++) {
                // Convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // Sender's phone number
                str += "SMS from " + msgs[i].getOriginatingAddress() + " : ";
                // Fetch the text message
                str += msgs[i].getMessageBody().toString();
                msg=msgs[i].getMessageBody().toString();
                Log.d("TAG", msg);
                str += "\n";


                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                if (msg.equals(preferences.getString("key",""))){
                    if (msgs[i].getOriginatingAddress().contains(preferences.getString("ec1",""))){
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(preferences.getString("ec1",""), null, "Help!\nMy location is "+MyService.loc.getLatitude()+" "+MyService.loc.getLongitude(), null, null);
                    }
                }
            }
            // Display the entire SMS Message
            Log.d("TAG", str);

        }
    }
}