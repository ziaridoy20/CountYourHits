package fim.uni_passau.de.countyourhits.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by subash on 02/07/2017.
 */

public class DartOpenDBHelper extends SQLiteOpenHelper {

    private static final String LOGTAG = "Dart_DB";
    private static final String DATABASE_NAME = "dart.db";
    private static final int DATABASE_VERSION = 3;

    //table column for players table
    public static final String TABLE_PLAYERS = "players";
    public static final String COLUMN_PLAYER_ID = "player_id";
    public static final String COLUMN_PLAYER_NAME = "player_name";
    public static final String COLUMN_PLAYER_IMAGE = "player_image";
    public static final String COLUMN_PLAYERS_NOTE = "player_note";

    //sql for creating player table
    private static final String TABLE_CREATE_PLAYERS =
            "CREATE TABLE " + TABLE_PLAYERS + " (" +
                    COLUMN_PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PLAYER_NAME + " TEXT, " +
                    COLUMN_PLAYER_IMAGE + " TEXT, " +
                    COLUMN_PLAYERS_NOTE + " TEXT" +
                    ")";

    //table column for result table
    public static final String TABLE_SCORES = "scores";
    public static final String COLUMN_SCORE_ID = "score_id";
    public static final String COLUMN_SCORE_PLAYER_ID = "fk_player_id";
    public static final String COLUMN_SCORE_REQUEST_NO = "request_no";
    public static final String COLUMN_SCORE_POINT = "score_point";
    public static final String COLUMN_SCORE_CO_ORDINATE_X = "score_co_ordinate_x";
    public static final String COLUMN_SCORE_CO_ORDINATE_Y = "score_co_ordinate_y";
    public static final String COLUMN_SCORE_IMAGE_PATH = "score_image_path";
    public static final String COLUMN_SCORE_DATE_TIME = "score_date_time";
    public static final String COLUMN_SCORE_NOTE = "score_note";
    public static final String COLUMN_SCORE_TIME_STAMP = "time_stamp";

    //sql for creating score table
    private static final String TABLE_CREATE_SCORES =
            "CREATE TABLE " + TABLE_SCORES + " (" +
                    COLUMN_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SCORE_PLAYER_ID + " INTEGER, " +
                    COLUMN_SCORE_REQUEST_NO + " INTEGER, " +
                    COLUMN_SCORE_POINT + " TEXT, " +
                    COLUMN_SCORE_CO_ORDINATE_X + " TEXT, " +
                    COLUMN_SCORE_CO_ORDINATE_Y + " TEXT, " +
                    COLUMN_SCORE_IMAGE_PATH + " TEXT, " +
                    COLUMN_SCORE_DATE_TIME + " TEXT, " +
                    COLUMN_SCORE_NOTE + " TEXT, " +
                    COLUMN_SCORE_TIME_STAMP + "INTEGER, " +
                    " FOREIGN KEY ("+COLUMN_SCORE_PLAYER_ID+") REFERENCES "+TABLE_PLAYERS+" ("+COLUMN_PLAYER_ID+"));";

    //triggers everytime and delete row if exceeded more than 5
/*    private static final String CREATE_TRIGGER =
            "CREATE TRIGGER tabletrigger AFTER INSERT ON" +  TABLE_SCORES +
                    "WHEN (SELECT COUNT (*) FROM " + TABLE_SCORES + ") > " + 5 +
                    "BEGIN DELETE FROM "+ TABLE_SCORES + "WHERE " + COLUMN_SCORE_ID +
                    "=(SELECT MIN("+COLUMN_SCORE_ID  +") FROM" +  TABLE_SCORES +"END;";*/



    public DartOpenDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_PLAYERS);
        Log.i(LOGTAG, "Table PLAYERS has been created");
        db.execSQL(TABLE_CREATE_SCORES);
        Log.i(LOGTAG, "Table SCORES has been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);

        Log.i(LOGTAG, "Database has been upgraded from " + oldVersion + " to " + newVersion);
    }
}
