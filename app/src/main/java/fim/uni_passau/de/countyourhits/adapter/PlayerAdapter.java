package fim.uni_passau.de.countyourhits.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fim.uni_passau.de.countyourhits.R;
import fim.uni_passau.de.countyourhits.model.Players;

/**
 * Created by subash on 08/07/2017.
 */

public class PlayerAdapter extends ArrayAdapter<Players> {
    List<Players> players;

    public PlayerAdapter(Context context, List<Players> players) {
        super(context, R.layout.item_row_players, players);
        this.players=players;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.item_row_players, parent, false);

        Players player = players.get(position);
        TextView tvPlayerName = (TextView) customView.findViewById(R.id.player_name);
        TextView tvPlayerId = (TextView) customView.findViewById(R.id.player_id);
        ImageView ivPlayerImage = (ImageView) customView.findViewById(R.id.player_image);

        tvPlayerName.setText(player.getPlayerName());
        tvPlayerId.setText(String.valueOf(player.getPlayerId()));

        int imageResource = getContext().getResources().getIdentifier(
                player.getPlayerImage(), "drawable", getContext().getPackageName());
        if (imageResource != 0) {
            //ivPlayerImage.setImageResource(R.drawable.ic_player_score_img);
            ivPlayerImage.setImageResource(imageResource);
        }
        return customView;
    }


//    Context context;
//    List<Players> players;
//
//    public PlayerAdapter(Context context, List<Players> players) {
//        super(context, android.R.id.content, players);
//        this.context = context;
//        this.players = players;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = vi.inflate(R.layout.item_row_players, null);
//
//        Players player = players.get(position);
//
//        TextView tvPlayerName = (TextView) view.findViewById(R.id.player_name);
//        tvPlayerName.setText(player.getPlayerName());
//
//        TextView tvPlayerId = (TextView) view.findViewById(R.id.player_id);
//        tvPlayerId.setText((int) player.getPlayerId());
//
//        ImageView iv = (ImageView) view.findViewById(R.id.player_image);
//        int imageResource = context.getResources().getIdentifier(
//                player.getPlayerImage(), "drawable", context.getPackageName());
//        if (imageResource != 0) {
//            iv.setImageResource(imageResource);
//        }
//
//        return view;
//    }
}

