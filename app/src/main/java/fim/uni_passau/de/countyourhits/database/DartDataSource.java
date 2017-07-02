package fim.uni_passau.de.countyourhits.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fim.uni_passau.de.countyourhits.activity.PlayerScoreActivity;
import fim.uni_passau.de.countyourhits.model.Players;

/**
 * Created by subash on 02/07/2017.
 */

public class DartDataSource {
    private static final String LOGTAG = "DARTDB";

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    private static final String[] allColumns = {
            DartDBHelper.COLUMN_ID,
            DartDBHelper.COLUMN_NAME,
            DartDBHelper.COLUMN_SCORE,
            DartDBHelper.COLUMN_IMAGE,
            DartDBHelper.COLUMN_DESC};

    public DartDataSource(Context context) {
        dbHelper = new DartDBHelper(context);
    }

    public void open() {
        Log.i(LOGTAG, "Database opened");
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        Log.i(LOGTAG, "Database closed");
        dbHelper.close();
    }

    public Players createPlayer(Players players) {
        ContentValues values = new ContentValues();
        values.put(DartDBHelper.COLUMN_NAME, players.getPlayerName());
        values.put(DartDBHelper.COLUMN_SCORE, players.getScore());
        values.put(DartDBHelper.COLUMN_IMAGE, players.getScoreImage());
        values.put(DartDBHelper.COLUMN_DESC, players.getDescription());
        long insertid = database.insert(DartDBHelper.TABLE_DART, null, values);
        players.setId(insertid);
        return players;
    }

    public List<Players> findAll() {
        List<Players> players = new ArrayList<Players>();
        Cursor cursor = database.query(DartDBHelper.TABLE_DART, allColumns,
                null, null, null,null, null);
        Log.i(LOGTAG, "Returned " +cursor.getCount() + "rows");

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Players player = new Players();
                player.setId(cursor.getInt(cursor.getColumnIndex(DartDBHelper.COLUMN_ID)));
                player.setPlayerName(cursor.getString(cursor.getColumnIndex(DartDBHelper.COLUMN_NAME)));
                player.setScore(cursor.getString(cursor.getColumnIndex(DartDBHelper.COLUMN_SCORE)));
                player.setScoreImage(cursor.getString(cursor.getColumnIndex(DartDBHelper.COLUMN_IMAGE)));
                player.setDescription(cursor.getString(cursor.getColumnIndex(DartDBHelper.COLUMN_DESC)));
                players.add(player);
            }
        }
        return  players;
    }
}
