package com.clevertap.android.sdk;

import static com.clevertap.android.sdk.JsonUtil.toJsonArray;
import static com.clevertap.android.sdk.JsonUtil.toList;
import static com.clevertap.android.sdk.pushnotification.PushNotificationUtil.getAll;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import java.util.ArrayList;
import org.json.JSONObject;

public class CleverTapInstanceConfig implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CleverTapInstanceConfig> CREATOR
            = new Parcelable.Creator<CleverTapInstanceConfig>() {
        @Override
        public CleverTapInstanceConfig createFromParcel(Parcel in) {
            return new CleverTapInstanceConfig(in);
        }

        @Override
        public CleverTapInstanceConfig[] newArray(int size) {
            return new CleverTapInstanceConfig[size];
        }
    };

    private String accountId;

    private String accountRegion;

    private String accountToken;

    private String proxyDomain;

    @NonNull
    private ArrayList<String> allowedPushTypes = getAll();

    CleverTapInstanceConfig(CleverTapInstanceConfig config) {
        this.accountId = config.accountId;
        this.accountToken = config.accountToken;
        this.accountRegion = config.accountRegion;
        this.proxyDomain = config.proxyDomain;
        this.isDefaultInstance = config.isDefaultInstance;
        this.analyticsOnly = config.analyticsOnly;
        this.personalization = config.personalization;
        this.debugLevel = config.debugLevel;
        this.logger = config.logger;
        this.useGoogleAdId = config.useGoogleAdId;
        this.disableAppLaunchedEvent = config.disableAppLaunchedEvent;
        this.createdPostAppLaunch = config.createdPostAppLaunch;
        this.sslPinning = config.sslPinning;
        this.backgroundSync = config.backgroundSync;
        this.enableCustomCleverTapId = config.enableCustomCleverTapId;
        this.fcmSenderId = config.fcmSenderId;
        this.enableABTesting = config.enableABTesting;
        this.enableUIEditor = config.enableUIEditor;
        this.packageName = config.packageName;
        this.beta = config.beta;
        this.allowedPushTypes = config.allowedPushTypes;
    }

    private boolean analyticsOnly;

    private boolean backgroundSync;

    private boolean beta;

    private boolean createdPostAppLaunch;

    private int debugLevel;

    private boolean disableAppLaunchedEvent;

    private boolean enableABTesting;

    private boolean enableCustomCleverTapId;

    private boolean enableUIEditor;

    private String fcmSenderId;

    private boolean isDefaultInstance;

    private Logger logger;

    private String packageName;

    private boolean personalization;

    private boolean sslPinning;

    private boolean useGoogleAdId;

    @SuppressWarnings("unused")
    public static CleverTapInstanceConfig createInstance(Context context, @NonNull String accountId,
            @NonNull String accountToken, @NonNull String proxyDomain) {
        //noinspection ConstantConditions
        if (accountId == null || accountToken == null) {
            Logger.i("CleverTap accountId and accountToken cannot be null");
            return null;
        }
        return new CleverTapInstanceConfig(context, accountId, accountToken, proxyDomain, null, false);
    }

    @SuppressWarnings({"unused"})
    public static CleverTapInstanceConfig createInstance(Context context, @NonNull String accountId,
            @NonNull String accountToken, @NonNull String proxyDomain, String accountRegion) {
        //noinspection ConstantConditions
        if (accountId == null || accountToken == null) {
            Logger.i("CleverTap accountId and accountToken cannot be null");
            return null;
        }
        return new CleverTapInstanceConfig(context, accountId, accountToken, proxyDomain, accountRegion, false);
    }

    private CleverTapInstanceConfig(Context context, String accountId, String accountToken,
                                    String proxyDomain, String accountRegion, boolean isDefault) {
        this.accountId = accountId;
        this.accountToken = accountToken;
        this.accountRegion = accountRegion;
        this.proxyDomain = proxyDomain;
        this.isDefaultInstance = isDefault;
        this.analyticsOnly = false;
        this.personalization = true;
        this.debugLevel = CleverTapAPI.LogLevel.DEBUG.intValue();
        this.logger = new Logger(this.debugLevel);
        this.createdPostAppLaunch = false;
        this.enableABTesting = this.isDefaultInstance;
        this.enableUIEditor = this.enableABTesting;

        ManifestInfo manifest = ManifestInfo.getInstance(context);
        this.useGoogleAdId = manifest.useGoogleAdId();
        this.disableAppLaunchedEvent = manifest.isAppLaunchedDisabled();
        this.sslPinning = manifest.isSSLPinningEnabled();
        this.backgroundSync = manifest.isBackgroundSync();
        this.fcmSenderId = manifest.getFCMSenderId();
        this.packageName = manifest.getPackageName();
        this.enableCustomCleverTapId = manifest.useCustomId();
        this.beta = manifest.enableBeta();
    }

    private CleverTapInstanceConfig(String jsonString) throws Throwable {
        try {
            JSONObject configJsonObject = new JSONObject(jsonString);
            if (configJsonObject.has(Constants.KEY_ACCOUNT_ID)) {
                this.accountId = configJsonObject.getString(Constants.KEY_ACCOUNT_ID);
            }
            if (configJsonObject.has(Constants.KEY_ACCOUNT_TOKEN)) {
                this.accountToken = configJsonObject.getString(Constants.KEY_ACCOUNT_TOKEN);
            }
            if (configJsonObject.has(Constants.KEY_ACCOUNT_REGION)) {
                this.accountRegion = configJsonObject.getString(Constants.KEY_ACCOUNT_REGION);
            }
            if (configJsonObject.has(Constants.KEY_PROXY_DOMAIN)) {
                this.proxyDomain = configJsonObject.getString(Constants.KEY_PROXY_DOMAIN);
            }
            if (configJsonObject.has(Constants.KEY_ANALYTICS_ONLY)) {
                this.analyticsOnly = configJsonObject.getBoolean(Constants.KEY_ANALYTICS_ONLY);
            }
            if (configJsonObject.has(Constants.KEY_DEFAULT_INSTANCE)) {
                this.isDefaultInstance = configJsonObject.getBoolean(Constants.KEY_DEFAULT_INSTANCE);
            }
            if (configJsonObject.has(Constants.KEY_USE_GOOGLE_AD_ID)) {
                this.useGoogleAdId = configJsonObject.getBoolean(Constants.KEY_USE_GOOGLE_AD_ID);
            }
            if (configJsonObject.has(Constants.KEY_DISABLE_APP_LAUNCHED)) {
                this.disableAppLaunchedEvent = configJsonObject.getBoolean(Constants.KEY_DISABLE_APP_LAUNCHED);
            }
            if (configJsonObject.has(Constants.KEY_PERSONALIZATION)) {
                this.personalization = configJsonObject.getBoolean(Constants.KEY_PERSONALIZATION);
            }
            if (configJsonObject.has(Constants.KEY_DEBUG_LEVEL)) {
                this.debugLevel = configJsonObject.getInt(Constants.KEY_DEBUG_LEVEL);
            }
            this.logger = new Logger(this.debugLevel);
            if (configJsonObject.has(Constants.KEY_ENABLE_ABTEST)) {
                this.enableABTesting = configJsonObject.getBoolean(Constants.KEY_ENABLE_ABTEST);
            }
            if (configJsonObject.has(Constants.KEY_ENABLE_UIEDITOR)) {
                this.enableUIEditor = configJsonObject.getBoolean(Constants.KEY_ENABLE_UIEDITOR);
            }
            if (configJsonObject.has(Constants.KEY_PACKAGE_NAME)) {
                this.packageName = configJsonObject.getString(Constants.KEY_PACKAGE_NAME);
            }
            if (configJsonObject.has(Constants.KEY_CREATED_POST_APP_LAUNCH)) {
                this.createdPostAppLaunch = configJsonObject.getBoolean(Constants.KEY_CREATED_POST_APP_LAUNCH);
            }
            if (configJsonObject.has(Constants.KEY_SSL_PINNING)) {
                this.sslPinning = configJsonObject.getBoolean(Constants.KEY_SSL_PINNING);
            }
            if (configJsonObject.has(Constants.KEY_BACKGROUND_SYNC)) {
                this.backgroundSync = configJsonObject.getBoolean(Constants.KEY_BACKGROUND_SYNC);
            }
            if (configJsonObject.has(Constants.KEY_ENABLE_CUSTOM_CT_ID)) {
                this.enableCustomCleverTapId = configJsonObject.getBoolean(Constants.KEY_ENABLE_CUSTOM_CT_ID);
            }
            if (configJsonObject.has(Constants.KEY_FCM_SENDER_ID)) {
                this.fcmSenderId = configJsonObject.getString(Constants.KEY_FCM_SENDER_ID);
            }
            if (configJsonObject.has(Constants.KEY_BETA)) {
                this.beta = configJsonObject.getBoolean(Constants.KEY_BETA);
            }
            if (configJsonObject.has(Constants.KEY_ALLOWED_PUSH_TYPES)) {
                this.allowedPushTypes = (ArrayList<String>) toList(
                        configJsonObject.getJSONArray(Constants.KEY_ALLOWED_PUSH_TYPES));
            }
        } catch (Throwable t) {
            Logger.v("Error constructing CleverTapInstanceConfig from JSON: " + jsonString + ": ", t.getCause());
            throw (t);
        }
    }

    private CleverTapInstanceConfig(Parcel in) {
        accountId = in.readString();
        accountToken = in.readString();
        accountRegion = in.readString();
        proxyDomain = in.readString();
        analyticsOnly = in.readByte() != 0x00;
        isDefaultInstance = in.readByte() != 0x00;
        useGoogleAdId = in.readByte() != 0x00;
        disableAppLaunchedEvent = in.readByte() != 0x00;
        personalization = in.readByte() != 0x00;
        debugLevel = in.readInt();
        createdPostAppLaunch = in.readByte() != 0x00;
        sslPinning = in.readByte() != 0x00;
        backgroundSync = in.readByte() != 0x00;
        enableCustomCleverTapId = in.readByte() != 0x00;
        fcmSenderId = in.readString();
        enableABTesting = in.readByte() != 0x00;
        enableUIEditor = in.readByte() != 0x00;
        packageName = in.readString();
        logger = new Logger(debugLevel);
        beta = in.readByte() != 0x00;
        allowedPushTypes = new ArrayList<>();
        in.readList(allowedPushTypes, String.class.getClassLoader());
    }

    @NonNull
    public ArrayList<String> getAllowedPushTypes() {
        return allowedPushTypes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void enablePersonalization(boolean enablePersonalization) {
        this.personalization = enablePersonalization;
    }

    public String getAccountId() {
        return accountId;
    }

    @SuppressWarnings({"unused"})
    public String getAccountRegion() {
        return accountRegion;
    }

    @SuppressWarnings({"unused"})
    public String getProxyDomain() {
        return proxyDomain;
    }

    @SuppressWarnings({"unused"})
    public String getAccountToken() {
        return accountToken;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public int getDebugLevel() {
        return debugLevel;
    }

    @SuppressWarnings({"unused"})
    public void setDebugLevel(CleverTapAPI.LogLevel debugLevel) {
        this.debugLevel = debugLevel.intValue();
    }

    @SuppressWarnings({"unused"})
    public void setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public String getFcmSenderId() {
        return fcmSenderId;
    }

    public Logger getLogger() {
        if (logger == null) {
            logger = new Logger(this.debugLevel);
        }
        return logger;
    }

    public String getPackageName() {
        return packageName;
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted", "WeakerAccess"})
    public boolean isABTestingEnabled() {
        return enableABTesting;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public boolean isAnalyticsOnly() {
        return analyticsOnly;
    }

    @SuppressWarnings({"unused"})
    public void setAnalyticsOnly(boolean analyticsOnly) {
        this.analyticsOnly = analyticsOnly;
    }

    public boolean isBeta() {
        return beta;
    }

    @SuppressWarnings({"unused"})
    public boolean isUIEditorEnabled() {
        return enableUIEditor;
    }

    public void log(@NonNull String tag, @NonNull String message) {
        logger.verbose(getDefaultSuffix(tag), message);
    }

    public void log(@NonNull String tag, @NonNull String message, Throwable throwable) {
        logger.verbose(getDefaultSuffix(tag), message, throwable);
    }

    @SuppressWarnings("SameParameterValue")
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void setEnableABTesting(boolean enableABTesting) {
        this.enableABTesting = enableABTesting;
    }

    @SuppressWarnings({"unused"})
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void setEnableUIEditor(boolean enableUIEditor) {
        this.enableUIEditor = enableUIEditor;
    }

    @SuppressWarnings({"unused"})
    public void useGoogleAdId(boolean value) {
        this.useGoogleAdId = value;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accountId);
        dest.writeString(accountToken);
        dest.writeString(accountRegion);
        dest.writeString(proxyDomain);
        dest.writeByte((byte) (analyticsOnly ? 0x01 : 0x00));
        dest.writeByte((byte) (isDefaultInstance ? 0x01 : 0x00));
        dest.writeByte((byte) (useGoogleAdId ? 0x01 : 0x00));
        dest.writeByte((byte) (disableAppLaunchedEvent ? 0x01 : 0x00));
        dest.writeByte((byte) (personalization ? 0x01 : 0x00));
        dest.writeInt(debugLevel);
        dest.writeByte((byte) (createdPostAppLaunch ? 0x01 : 0x00));
        dest.writeByte((byte) (sslPinning ? 0x01 : 0x00));
        dest.writeByte((byte) (backgroundSync ? 0x01 : 0x00));
        dest.writeByte((byte) (enableCustomCleverTapId ? 0x01 : 0x00));
        dest.writeString(fcmSenderId);
        dest.writeByte((byte) (enableABTesting ? 0x01 : 0x00));
        dest.writeByte((byte) (enableUIEditor ? 0x01 : 0x00));
        dest.writeString(packageName);
        dest.writeByte((byte) (beta ? 0x01 : 0x00));
        dest.writeList(allowedPushTypes);
    }

    boolean getEnableCustomCleverTapId() {
        return enableCustomCleverTapId;
    }

    @SuppressWarnings({"unused"})
    public void setEnableCustomCleverTapId(boolean enableCustomCleverTapId) {
        this.enableCustomCleverTapId = enableCustomCleverTapId;
    }

    boolean isBackgroundSync() {
        return backgroundSync;
    }

    @SuppressWarnings({"unused"})
    public void setBackgroundSync(boolean backgroundSync) {
        this.backgroundSync = backgroundSync;
    }

    boolean isCreatedPostAppLaunch() {
        return createdPostAppLaunch;
    }

    boolean isDefaultInstance() {
        return isDefaultInstance;
    }

    boolean isDisableAppLaunchedEvent() {
        return disableAppLaunchedEvent;
    }

    @SuppressWarnings({"unused"})
    public void setDisableAppLaunchedEvent(boolean disableAppLaunchedEvent) {
        this.disableAppLaunchedEvent = disableAppLaunchedEvent;
    }

    boolean isPersonalizationEnabled() {
        return personalization;
    }

    boolean isSslPinningEnabled() {
        return sslPinning;
    }

    boolean isUseGoogleAdId() {
        return useGoogleAdId;
    }

    void setCreatedPostAppLaunch() {
        this.createdPostAppLaunch = true;
    }

    String toJSONString() {
        JSONObject configJsonObject = new JSONObject();
        try {
            configJsonObject.put(Constants.KEY_ACCOUNT_ID, getAccountId());
            configJsonObject.put(Constants.KEY_ACCOUNT_TOKEN, getAccountToken());
            configJsonObject.put(Constants.KEY_ACCOUNT_REGION, getAccountRegion());
            configJsonObject.put(Constants.KEY_PROXY_DOMAIN, getProxyDomain());
            configJsonObject.put(Constants.KEY_FCM_SENDER_ID, getFcmSenderId());
            configJsonObject.put(Constants.KEY_ANALYTICS_ONLY, isAnalyticsOnly());
            configJsonObject.put(Constants.KEY_DEFAULT_INSTANCE, isDefaultInstance());
            configJsonObject.put(Constants.KEY_USE_GOOGLE_AD_ID, isUseGoogleAdId());
            configJsonObject.put(Constants.KEY_DISABLE_APP_LAUNCHED, isDisableAppLaunchedEvent());
            configJsonObject.put(Constants.KEY_PERSONALIZATION, isPersonalizationEnabled());
            configJsonObject.put(Constants.KEY_DEBUG_LEVEL, getDebugLevel());
            configJsonObject.put(Constants.KEY_CREATED_POST_APP_LAUNCH, isCreatedPostAppLaunch());
            configJsonObject.put(Constants.KEY_SSL_PINNING, isSslPinningEnabled());
            configJsonObject.put(Constants.KEY_BACKGROUND_SYNC, isBackgroundSync());
            configJsonObject.put(Constants.KEY_ENABLE_CUSTOM_CT_ID, getEnableCustomCleverTapId());
            configJsonObject.put(Constants.KEY_PACKAGE_NAME, getPackageName());
            configJsonObject.put(Constants.KEY_BETA, isBeta());
            configJsonObject.put(Constants.KEY_ENABLE_UIEDITOR, isUIEditorEnabled());
            configJsonObject.put(Constants.KEY_ENABLE_ABTEST, isABTestingEnabled());
            configJsonObject.put(Constants.KEY_ALLOWED_PUSH_TYPES, toJsonArray(allowedPushTypes));
            return configJsonObject.toString();
        } catch (Throwable e) {
            Logger.v("Unable to convert config to JSON : ", e.getCause());
            return null;
        }
    }

    private String getDefaultSuffix(@NonNull String tag) {
        return "[" + ((!TextUtils.isEmpty(tag) ? ": " + tag : "") + ":" + accountId + "]");
    }

    // convenience to construct the internal only default config
    @SuppressWarnings({"unused", "WeakerAccess"})
    protected static CleverTapInstanceConfig createDefaultInstance(Context context, @NonNull String accountId,
            @NonNull String accountToken, @NonNull String proxyDomain, String accountRegion) {
        return new CleverTapInstanceConfig(context, accountId, accountToken, proxyDomain, accountRegion, true);
    }

    // for internal use only!
    @SuppressWarnings({"unused", "WeakerAccess"})
    protected static CleverTapInstanceConfig createInstance(@NonNull String jsonString) {
        try {
            return new CleverTapInstanceConfig(jsonString);
        } catch (Throwable t) {
            return null;
        }
    }

}
