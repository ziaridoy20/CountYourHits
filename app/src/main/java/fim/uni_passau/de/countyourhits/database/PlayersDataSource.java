package fim.uni_passau.de.countyourhits.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fim.uni_passau.de.countyourhits.model.Players;

/**
 * Created by subash on 02/07/2017.
 */

public class PlayersDataSource {
//    private static final String LOGTAG = "Data_PlayersDataSource";
//    SQLiteOpenHelper dbHelper;
//    SQLiteDatabase database;
//
//    private static final String[] allColumns = {
//            DartOpenDBHelper.COLUMN_PLAYER_ID,
//            DartOpenDBHelper.COLUMN_PLAYER_NAME,
//            DartOpenDBHelper.COLUMN_PLAYER_IMAGE,
//            DartOpenDBHelper.COLUMN_PLAYERS_NOTE};
//
//    public PlayersDataSource(Context context) {
//        dbHelper = new DartOpenDBHelper(context);
//    }
//
//    public void open() {
//        Log.i(LOGTAG, "Database opened");
//        database = dbHelper.getWritableDatabase();
//    }
//
//    public void close() {
//        Log.i(LOGTAG, "Database closed");
//        dbHelper.close();
//    }
//
//    public Players createPlayersData(Players player) {
//        ContentValues values = new ContentValues();
//        values.put(DartOpenDBHelper.COLUMN_PLAYER_NAME, player.getPlayerName());
//        values.put(DartOpenDBHelper.COLUMN_PLAYER_IMAGE, player.getPlayerImage());
//        values.put(DartOpenDBHelper.COLUMN_PLAYERS_NOTE, player.getPlayerNote());
//        long insertId = database.insert(DartOpenDBHelper.TABLE_PLAYERS, null, values);
//        player.setPlayerId(insertId);
//        return player;
//    }
//
//    public List<Players> findAll() {
//        List<Players> players = new ArrayList<Players>();
//        Cursor cursor = database.query(DartOpenDBHelper.TABLE_PLAYERS, allColumns,
//                null, null, null,null, null);
//        Log.i(LOGTAG, "Returned " +cursor.getCount() + "rows");
//
//        if (cursor.getCount() > 0) {
//            while (cursor.moveToNext()) {
//                Players player = new Players();
//                player.setPlayerId(cursor.getLong(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_PLAYER_ID)));
//                player.setPlayerName(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_PLAYER_NAME)));
//                player.setPlayerImage(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_PLAYER_IMAGE)));
//                player.setPlayerNote(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_PLAYERS_NOTE)));
//                players.add(player);
//            }
//        }
//        return  players;
//    }

    private static final String LOGTAG = "Dart_PlayerDataSource";

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    private static final String[] allColumns = {
            DartOpenDBHelper.COLUMN_PLAYER_ID,
            DartOpenDBHelper.COLUMN_PLAYER_NAME,
            DartOpenDBHelper.COLUMN_PLAYER_IMAGE,
            DartOpenDBHelper.COLUMN_PLAYERS_NOTE};

    public PlayersDataSource(Context context) {
        dbHelper = new DartOpenDBHelper(context);
    }

    public void open() {
        Log.i(LOGTAG, "Database opened");
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        Log.i(LOGTAG, "Database closed");
        dbHelper.close();
    }

    public Players create(Players player) {
        ContentValues values = new ContentValues();
        values.put(DartOpenDBHelper.COLUMN_PLAYER_NAME, player.getPlayerName());
        values.put(DartOpenDBHelper.COLUMN_PLAYER_IMAGE, player.getPlayerImage());
        values.put(DartOpenDBHelper.COLUMN_PLAYERS_NOTE, player.getPlayerNote());
        values.put(DartOpenDBHelper.COLUMN_PLAYER_ID, player.getPlayerId());
        long insertId = database.insert(DartOpenDBHelper.TABLE_PLAYERS, null, values);
        player.setPlayerId(insertId);
        return player;
    }

    public List<Players> findAll() {
        List<Players> players = new ArrayList<Players>();
        Cursor cursor = database.query(DartOpenDBHelper.TABLE_PLAYERS, allColumns,
                null, null, null,null, null);
        Log.i(LOGTAG, "Returned " +cursor.getCount() + "rows");

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Players player = new Players();
                player.setPlayerId(cursor.getLong(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_PLAYER_ID)));
                player.setPlayerName(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_PLAYER_NAME)));
                player.setPlayerImage(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_PLAYER_IMAGE)));
                player.setPlayerNote(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_PLAYERS_NOTE)));
                players.add(player);
            }
        }
        return  players;
    }

    public List<Players> findByPlayerId(String playerId) {
        List<Players> players = new ArrayList<Players>();
        Cursor cursor = database.query(DartOpenDBHelper.TABLE_PLAYERS, allColumns,
                String.valueOf(playerId), null, null, null, null, null);
        Log.i(LOGTAG, "Returned " +cursor.getCount() + "rows");

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Players player = new Players();
                player.setPlayerId(cursor.getLong(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_PLAYER_ID)));
                player.setPlayerName(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_PLAYER_NAME)));
                player.setPlayerImage(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_PLAYER_IMAGE)));
                player.setPlayerNote(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_PLAYERS_NOTE)));
                players.add(player);
            }
        }
        return  players;
    }
}
