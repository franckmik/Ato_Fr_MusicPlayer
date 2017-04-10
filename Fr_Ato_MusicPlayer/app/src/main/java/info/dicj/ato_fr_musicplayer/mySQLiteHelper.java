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
    public static final String TABLE_THEME = "tblTheme";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INDICEMUSIQUE = "indiceMusique";
    public static final String COLUMN_NOMTHEME = "nomTheme";

    private static final String DATABASE_NAME = "favoris.db";
    private static final int DATABASE_VERSION = 10;


    private static final String CREATE_FAVORIS = "create table "
            + TABLE_FAVORIS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_INDICEMUSIQUE
            + " integer not null); ";

    private static final String CREATE_THEME = "create table "
            + TABLE_THEME + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NOMTHEME
            + " text not null) ;";

    private static final String INITIALISE_THEME = " insert into tblTheme(nomTheme) values('bleu'); ";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_FAVORIS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_INDICEMUSIQUE
            + " integer not null); " +
            "create table "
            + TABLE_THEME + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NOMTHEME
            + " text not null) ;" +
            " insert into " + TABLE_THEME + " values('bleu'); ";

    public mySQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(" DICJ "," Creation du mySQLHELPER ");
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        Log.i(" DICJ "," Drop des tables et creation de ma BD ");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORIS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_THEME);
        database.execSQL(CREATE_FAVORIS);
        database.execSQL(CREATE_THEME);
        database.execSQL(INITIALISE_THEME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(mySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORIS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_THEME);
        onCreate(database);
    }
}
