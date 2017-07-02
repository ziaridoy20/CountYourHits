package fim.uni_passau.de.countyourhits.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by subash on 02/07/2017.
 */

public class DartDBHelper extends SQLiteOpenHelper{

    private static final String LOGTAG = "DARTDB";
    private static final String DATABASE_NAME = "dart.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_DART = "players_record";
    public static final String COLUMN_ID = "players_record_id";
    public static final String COLUMN_NAME = "player_name";
    public static final String COLUMN_SCORE = "player_score";
    public static final String COLUMN_IMAGE = "player_score_image";
    public static final String COLUMN_DESC = "player_description";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_DART + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_SCORE + " TEXT, " +
                    COLUMN_IMAGE + " TEXT, " +
                    COLUMN_DESC + " NUMERIC " +
                    ")";


    public DartDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        Log.i(LOGTAG, "Table has been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DART);
        onCreate(db);
    }

}
