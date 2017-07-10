package fim.uni_passau.de.countyourhits.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fim.uni_passau.de.countyourhits.model.Scores;

/**
 * Created by subash on 02/07/2017.
 */

public class ScoreDataSource {
    private static final String LOGTAG = "Dart_ScoresDataSource";

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    private static final String[] allColumns = {
            DartOpenDBHelper.COLUMN_SCORE_ID,
            DartOpenDBHelper.COLUMN_SCORE_PLAYER_ID,
            DartOpenDBHelper.COLUMN_SCORE_REQUEST_NO,
            DartOpenDBHelper.COLUMN_SCORE_POINT,
            DartOpenDBHelper.COLUMN_SCORE_CO_ORDINATE_X,
            DartOpenDBHelper.COLUMN_SCORE_CO_ORDINATE_Y,
            DartOpenDBHelper.COLUMN_SCORE_IMAGE_PATH,
            DartOpenDBHelper.COLUMN_SCORE_DATE_TIME,
            DartOpenDBHelper.COLUMN_SCORE_NOTE};

    public ScoreDataSource(Context context) {
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

    public Scores create(Scores score) {
        ContentValues values = new ContentValues();
        values.put(DartOpenDBHelper.COLUMN_SCORE_PLAYER_ID, score.getScorePlayer_Id());
        values.put(DartOpenDBHelper.COLUMN_SCORE_REQUEST_NO, score.getScoreRequestNo());
        values.put(DartOpenDBHelper.COLUMN_SCORE_POINT, score.getScorePoint());
        values.put(DartOpenDBHelper.COLUMN_SCORE_CO_ORDINATE_X, score.getScoreCo_ordinate_x());
        values.put(DartOpenDBHelper.COLUMN_SCORE_CO_ORDINATE_Y, score.getScoreCo_ordinate_y());
        values.put(DartOpenDBHelper.COLUMN_SCORE_IMAGE_PATH, score.getScoreImagePath());
        values.put(DartOpenDBHelper.COLUMN_SCORE_DATE_TIME, score.getScoreDateTime());
        values.put(DartOpenDBHelper.COLUMN_SCORE_NOTE, score.getScoreNote());
        long insertId = database.insert(DartOpenDBHelper.TABLE_SCORES, null, values);
        score.setScoreId(insertId);
        return score;
    }

    public List<Scores> findAll() {
        Cursor cursor = database.query(DartOpenDBHelper.TABLE_SCORES, allColumns,
                null, null, null, null, null);
        Log.i(LOGTAG, "Returned " +cursor.getCount() + "rows");

        List<Scores> scores = cursorToList(cursor);
        return  scores;
    }

    public List<Scores> findByPlayerId(String playerId, String orderBy) {
        Cursor cursor = database.query(DartOpenDBHelper.TABLE_SCORES, allColumns,
                String.valueOf(playerId), null, null, null, orderBy);
        Log.i(LOGTAG, "Returned " +cursor.getCount() + "rows");

        List<Scores> scores = cursorToList(cursor);
        return  scores;
    }

    @NonNull
    private List<Scores> cursorToList(Cursor cursor) {
        List<Scores> scores = new ArrayList<Scores>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Scores score = new Scores();
                score.setScoreId(cursor.getLong(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_SCORE_ID)));
                score.setScorePlayer_Id(cursor.getLong(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_SCORE_PLAYER_ID)));
                score.setScoreRequestNo(cursor.getLong(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_SCORE_REQUEST_NO)));
                score.setScorePoint(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_SCORE_POINT)));
                score.setScoreCo_ordinate_x(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_SCORE_CO_ORDINATE_X)));
                score.setScoreCo_ordinate_y(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_SCORE_CO_ORDINATE_Y)));
                score.setScoreImagePath(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_SCORE_IMAGE_PATH)));
                score.setScoreDateTime(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_SCORE_DATE_TIME)));
                score.setScoreNote(cursor.getString(cursor.getColumnIndex(DartOpenDBHelper.COLUMN_SCORE_NOTE)));
                scores.add(score);
            }
        }
        return scores;
    }




//    public List<Scores> findByPlayerId(Long playerId, String orderBy) {
//        //List<Scores> scores = new ArrayList<Scores>();
//        Cursor cursor = database.query(DartOpenDBHelper.TABLE_SCORES, allColumns,
//                String.valueOf(playerId), null, null,null, orderBy);
//        Log.i(LOGTAG, "Returned " +cursor.getCount() + "rows");
//        List<Scores> scores =
//    }
}
