package cordova.plugin.eland.mdm.ElandMDM;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.jiransoft.mdm.library.MDMLib;
import com.jiransoft.mdm.library.Services.OnMangobananaCompleteListener;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class ElandMDM extends CordovaPlugin implements OnMangobananaCompleteListener {

    private String LOG_TAG = "MDMPlugin";
    private String CHECK_APP = "check_app";
    private String authServer = "mdm30ssl.eland.co.kr";
    private String authserverPort = "44300";
    private String companyCode = "17121500";
    private Context context;
    private Handler mdmHandler;
    private CallbackContext statusCallbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.i(LOG_TAG, "initialize");
        this.statusCallbackContext = null;
        MDMLib.setOnMangobananaCompleteListener(this);
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        context = this.cordova.getActivity().getApplicationContext();

        if (action.equals(CHECK_APP)) {
            this.statusCallbackContext = callbackContext;

            mdmHandler = new Handler();
            MDMLib.mangobanana(cordova.getActivity(), mdmHandler, companyCode, authServer, authserverPort, "");
            return true;
        }
        return false;
    }

    @Override
    public void onMangobananaComplete(String code, String message) {

        String packageNmae = getPackageName(context);
        String locale = getLocale();

        Log.d(LOG_TAG, "ID: " + message);
        final JSONObject status = new JSONObject();

        try {
            status.put("packageName", packageNmae);
            status.put("locale", locale);
            status.put("codeKey", code);
            status.put("message", message);
        } catch (JSONException e) {

        }

        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, status);
        pluginResult.setKeepCallback(true);
        this.statusCallbackContext.sendPluginResult(pluginResult);
    }

    private String getPackageName(Context context) {
        return context.getPackageName();
    }

    private String getLocale() {
        return Locale.getDefault().getLanguage();
    }
}
