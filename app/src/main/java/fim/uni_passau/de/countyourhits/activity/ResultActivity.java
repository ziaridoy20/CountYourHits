package fim.uni_passau.de.countyourhits.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
        View.OnClickListener  {
    private List<ResultResponse> data;

    private TextView currentItemName;
    private TextView currentItemPrice;
    private ImageView rateItemButton;
    private DiscreteScrollView itemPicker;
    private InfiniteScrollAdapter infiniteAdapter;

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
}
