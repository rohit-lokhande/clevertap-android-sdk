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

    @Override
    public void onReceive(Context context, Intent intent) {

        Logger.d("On Received Method Called");

        try {
            Intent launchIntent;

            Bundle extras = intent.getExtras();
            if (extras == null) {
                Logger.d("Intent get extras was null");
                return;
            }

            if (extras.containsKey(Constants.DEEP_LINK_KEY)) {
                Logger.d("Deeplink Found:" + intent.getStringExtra(Constants.DEEP_LINK_KEY));
                launchIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(intent.getStringExtra(Constants.DEEP_LINK_KEY)));
                Utils.setPackageNameFromResolveInfoList(context, launchIntent);
            } else {
                Logger.d("Deeplink Not Found");
                launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                Logger.d("Launch Intent was null");
                if (launchIntent == null) {
                    return;
                }
            }

            Logger.d("handleNotificationClicked will be clicked");
            CleverTapAPI.handleNotificationClicked(context, extras);

            launchIntent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            launchIntent.putExtras(extras);

            //to prevent calling of pushNotificationClickedEvent(extras) in ActivityLifecycleCallback
            launchIntent.putExtra(Constants.WZRK_FROM_KEY, Constants.WZRK_FROM);

            context.startActivity(launchIntent);

            Logger.d("CTPushNotificationReceiver: handled notification: " + extras.toString());
        } catch (Throwable t) {
            Logger.v("CTPushNotificationReceiver: error handling notification", t);
        }
    }
}
