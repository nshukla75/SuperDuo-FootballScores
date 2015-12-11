package barqsoft.footballscores.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import barqsoft.footballscores.data.DatabaseContract.scores_table;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresDBHelper extends SQLiteOpenHelper
{
    // version of the database, triggers onUpgrade if version of previous installed is lower
    private static final int DATABASE_VERSION = 3;

    // name of the database file
    public static final String DATABASE_NAME = "Scores.db";

    public ScoresDBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // sql string for creating the scores table
        final String CreateScoresTable = "CREATE TABLE "+ DatabaseContract.SCORES_TABLE +" ("+
                DatabaseContract.scores_table._ID +" INTEGER PRIMARY KEY,"+
                DatabaseContract.scores_table.DATE_COL +" TEXT NOT NULL,"+
                DatabaseContract.scores_table.TIME_COL +" INTEGER NOT NULL,"+
                DatabaseContract.scores_table.HOME_COL +" TEXT NOT NULL,"+
                DatabaseContract.scores_table.HOME_ID_COL +" INTEGER NOT NULL,"+
                DatabaseContract.scores_table.HOME_LOGO_COL +" TEXT,"+
                DatabaseContract.scores_table.HOME_GOALS_COL +" TEXT NOT NULL,"+
                DatabaseContract.scores_table.AWAY_COL +" TEXT NOT NULL,"+
                DatabaseContract.scores_table.AWAY_ID_COL +" INTEGER NOT NULL,"+
                DatabaseContract.scores_table.AWAY_LOGO_COL +" TEXT,"+
                DatabaseContract.scores_table.AWAY_GOALS_COL +" TEXT NOT NULL,"+
                DatabaseContract.scores_table.LEAGUE_COL +" INTEGER NOT NULL,"+
                DatabaseContract.scores_table.MATCH_ID +" INTEGER NOT NULL,"+
                DatabaseContract.scores_table.MATCH_DAY +" INTEGER NOT NULL,"+
                " UNIQUE ("+ DatabaseContract.scores_table.MATCH_ID +") ON CONFLICT REPLACE);";

        // execute the scores table creation sql
        db.execSQL(CreateScoresTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.SCORES_TABLE);

        // create the upgraded database
        onCreate(db);
    }
}
