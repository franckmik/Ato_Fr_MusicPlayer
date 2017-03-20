package info.dicj.ato_fr_musicplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by utilisateur on 09/03/2017.
 */
public class mySQLiteHelper extends SQLiteOpenHelper
{

    public static final String TABLE_FAVORIS = "tblFavoris";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INDICEMUSIQUE = "indiceMusique";

    private static final String DATABASE_NAME = "favoris.db";
    private static final int DATABASE_VERSION = 2;


    private static final String DATABASE_CREATE = "create table "
            + TABLE_FAVORIS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_INDICEMUSIQUE
            + " integer not null);";

    public mySQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORIS);
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(mySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORIS);
        onCreate(database);
    }
}
