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
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        IsConnected=true;
                    }
        }
        if(!IsConnected)
        {
            /*NetworkAlertDialog alert = new NetworkAlertDialog();
            alert.showDialog(_activity, MessageType.MT_NOT_CONNECTED);*/
        }
        return IsConnected;
    }

    public static String getNetStatus(Context context) {
        String status = null;
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();

        if (info == null) {
            status = "No Internet Connection";

        }
        else

        {
            if (info.isConnected())
                status = "Connected to Internet";
            else
                status = "Connecting to Internet";
        }
        return status;
    }

}
