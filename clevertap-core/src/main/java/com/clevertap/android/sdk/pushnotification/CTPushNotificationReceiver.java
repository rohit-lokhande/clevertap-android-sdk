package com.clevertap.android.sdk.pushnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.Constants;
import com.clevertap.android.sdk.Logger;
import com.clevertap.android.sdk.Utils;


public class CTPushNotificationReceiver extends BroadcastReceiver {

    String getRandomString(){
        // create a string of all characters
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        // create random string builder
        StringBuilder sb = new StringBuilder();

        // create an object of Random class
        Random random = new Random();

        // specify length of random string
        int length = 7;

        for(int i = 0; i < length; i++) {

            // generate random index number
            int index = random.nextInt(alphabet.length());

            // get character specified by index
            // from the string
            char randomChar = alphabet.charAt(index);

            // append the character to string builder
            sb.append(randomChar);
        }

        String randomString = sb.toString();
        return randomString;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String sessionId = getRandomString();
        Logger.d("(" + sessionId + ") On Received Method Called");
        try {
            Intent launchIntent;

            Bundle extras = intent.getExtras();
            if (extras == null) {
                Logger.d("(" + sessionId + ") Intent get extras was null");
                return;
            }

            if (extras.containsKey(Constants.DEEP_LINK_KEY)) {
                Logger.d("(" + sessionId + ") Deeplink Found:" + intent.getStringExtra(Constants.DEEP_LINK_KEY));
                launchIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(intent.getStringExtra(Constants.DEEP_LINK_KEY)));
                Utils.setPackageNameFromResolveInfoList(context, launchIntent);
            } else {
                Logger.d("(" + sessionId + ") Deeplink Not Found");
                launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                Logger.d("(" + sessionId + ") Launch Intent was null");
                if (launchIntent == null) {
                    return;
                }
            }

            Logger.d("(" + sessionId + ") handleNotificationClicked will be clicked");
            CleverTapAPI.handleNotificationClicked(context, extras);
            Logger.d("(" + sessionId + ") handleNotificationClicked Clicked");

            launchIntent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            launchIntent.putExtras(extras);

            //to prevent calling of pushNotificationClickedEvent(extras) in ActivityLifecycleCallback
            launchIntent.putExtra(Constants.WZRK_FROM_KEY, Constants.WZRK_FROM);
            
            Logger.d("(" + sessionId + ") Will start activity");
            context.startActivity(launchIntent);
            Logger.d("(" + sessionId + ") CTPushNotificationReceiver: handled notification: " + extras.toString());

        } catch (Throwable t) {
            Logger.v("(" + sessionId + ") CTPushNotificationReceiver: error handling notification", t);
        }
    }
}
