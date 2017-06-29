package fim.uni_passau.de.countyourhits.util;

/**
 * Created by NAHID002345 on 11/26/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class CheckNetwork {

    private static final String TAG = CheckNetwork.class.getSimpleName();

    static int no_int = 0;
    public static boolean isConnectingToInternet(Context _context,Activity _activity){

        boolean IsConnected=false;
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null && info.isConnected()){
                IsConnected=true;
            }
            else {
                IsConnected=false;
            }
        }

        return IsConnected;
    }

    public static String getWifiConnectionStatus(Context context) {
        String status = null;
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (info == null) {
            status = "No WIFI Connection";

        }
        else

        {
            if (info.isConnected())
                status = "Connected to WIFI";
            else if(info.isConnectedOrConnecting())
                status = "Connecting to WIFI";
        }
        return status;
    }

}
