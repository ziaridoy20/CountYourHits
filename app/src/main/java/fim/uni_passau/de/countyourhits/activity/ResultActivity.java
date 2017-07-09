package fim.uni_passau.de.countyourhits.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.Arrays;
import java.util.List;

import fim.uni_passau.de.countyourhits.R;
import fim.uni_passau.de.countyourhits.adapter.ResultAdapter;
import fim.uni_passau.de.countyourhits.model.ResultResponse;

public class ResultActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener,
        View.OnClickListener ,SalutDataCallback {
    private List<ResultResponse> data;

    private TextView currentItemName;
    private TextView currentItemPrice;
    private ImageView rateItemButton;
    private DiscreteScrollView itemPicker;
    private InfiniteScrollAdapter infiniteAdapter;
    //
    //salut p2p connection
    //
    public static final String TAG = "Salut";
    public SalutDataReceiver dataReceiver;
    public SalutServiceData serviceData;
    public Salut network;
    SalutDataCallback callback;
    ProgressBar progressBar;
    CardView image;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);



        currentItemName = (TextView) findViewById(R.id.item_name);
        currentItemPrice = (TextView) findViewById(R.id.item_price);

        //rateItemButton = (ImageView) findViewById(R.id.item_btn_rate);

        //shop = Shop.get();
        data = getData();
        itemPicker = (DiscreteScrollView) findViewById(R.id.item_picker);
        itemPicker.setOrientation(Orientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);
        infiniteAdapter = InfiniteScrollAdapter.wrap(new ResultAdapter(data));
        itemPicker.setAdapter(infiniteAdapter);
        itemPicker.setItemTransitionTimeMillis(1000);
        itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        onItemChanged(data.get(0));

        //findViewById(R.id.item_btn_rate).setOnClickListener(this);
        //findViewById(R.id.item_btn_buy).setOnClickListener(this);
        //findViewById(R.id.item_btn_comment).setOnClickListener(this);

        //findViewById(R.id.home).setOnClickListener(this);
        //findViewById(R.id.btn_smooth_scroll).setOnClickListener(this);
        //findViewById(R.id.btn_transition_time).setOnClickListener(this);
        initSalut();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(network != null) {
            if (network.isRunningAsHost) {
                network.stopNetworkService(false);

            } else if(network.isDiscovering) {
                network.stopServiceDiscovery(false);
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.item_btn_rate:
                ResultResponse current = data.get(itemPicker.getCurrentItem());
                //shop.setRated(current.getId(), !shop.isRated(current.getId()));
                changeRateButtonState(current);
                break;*/
            case R.id.home:
                finish();
                break;
           /* case R.id.btn_transition_time:
                //DiscreteScrollViewOptions.configureTransitionTime(itemPicker);
                break;
            case R.id.btn_smooth_scroll:
                //DiscreteScrollViewOptions.smoothScrollToUserSelectedPosition(itemPicker, v);
                break;*/
            default:
                showUnsupportedSnackBar();
                break;
        }
    }

    private void onItemChanged(ResultResponse item) {
        currentItemName.setText(item.getDescription());
        currentItemPrice.setText(item.getPlayerId());
        //changeRateButtonState(item);
    }

    private void changeRateButtonState(ResultResponse item) {

    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int position) {
        int positionInDataSet = infiniteAdapter.getRealPosition(position);
        onItemChanged(data.get(positionInDataSet));
    }

    private void showUnsupportedSnackBar() {
        Snackbar.make(itemPicker, "Unsuccessful", Snackbar.LENGTH_SHORT).show();
    }


    public List<ResultResponse> getData() {
        return Arrays.asList(
                new ResultResponse("1", "Everyday Candle", "$12.00 USD"),
                new ResultResponse("2", "Small Porcelain Bowl", "$50.00 USD"),
                new ResultResponse("3", "Favourite Board", "$265.00 USD"));
    }

    public void initSalut(){
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        image =(CardView) findViewById(R.id.card_view);

        dataReceiver = new SalutDataReceiver(this,this);
        serviceData = new SalutServiceData("wifiservice", 13334,"P2P");
        network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                Log.e(TAG, "Sorry, but this device does not support WiFi Direct.");
            }
        });
        discoverServices();

    }
    private void discoverServices() {
        progressBar.setVisibility(View.VISIBLE);
        image.setVisibility(View.GONE);

        if(!network.isRunningAsHost && !network.isDiscovering) {
            network.discoverNetworkServices(new SalutDeviceCallback() {
                @Override
                public void call(final SalutDevice device) {
                    network.registerWithHost(device, new SalutCallback() {
                        @Override
                        public void call() {
                            progressBar.setVisibility(View.GONE);
                            image.setVisibility(View.VISIBLE);
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultActivity.this);
                            alertDialog.setTitle("Client Device Connected")
                                    .setMessage("Device: " + device.deviceName + " connected as host")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = alertDialog.create();
                            alertDialog.setTitle("STOP Discovery");
                            alert.show();
                        }
                    }, new SalutCallback() {
                        @Override
                        public void call() {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultActivity.this);
                            alertDialog.setTitle("Unsuccessful")
                                    .setMessage("Sorry Unable to Connect, Try again")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressBar.setVisibility(View.GONE);
                                            image.setVisibility(View.VISIBLE);
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = alertDialog.create();
                            alertDialog.setTitle("STAR DISCOVERY");
                            alert.show();
                        }
                    });
                }
            }, true);

        }
        else {
            stopDiscovery();
//            old code
//            network.stopServiceDiscovery(true);
//            discoverBtn.setText("Discover Services");
//            hostingBtn.setAlpha(1f);
//            hostingBtn.setClickable(false);
        }
    }

    protected void stopDiscovery() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultActivity.this);
        alertDialog.setMessage("Do you want to STOP DISCOVERY?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        network.stopServiceDiscovery(true);
                        progressBar.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialog.create();
        alertDialog.setTitle("Stop Discovery");
        alert.show();
    }

    @Override
    public void onDataReceived(Object o) {

    }
}
