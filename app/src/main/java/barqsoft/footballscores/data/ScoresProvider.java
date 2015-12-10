package barqsoft.footballscores.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.data.ScoresDBHelper;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresProvider extends ContentProvider
{
    // reference to our sqlitedbhelper
    private static ScoresDBHelper mOpenHelper;

    // uri matcher
    private UriMatcher mUriMatcher = buildUriMatcher();

    // uris
    private static final int MATCHES = 100;
    private static final int MATCHES_WITH_LEAGUE = 101;
    private static final int MATCHES_WITH_ID = 102;
    private static final int MATCHES_WITH_DATE = 103;
    private static final int MATCHES_MOST_RECENT = 104;

    // sql filters
    private static final String SCORES_BY_LEAGUE =
            DatabaseContract.scores_table.LEAGUE_COL + " = ?";
    private static final String SCORES_BY_DATE =
            DatabaseContract.scores_table.DATE_COL + " LIKE ?";
    private static final String SCORES_BY_ID =
            DatabaseContract.scores_table.MATCH_ID + " = ?";
    private static final String SCORES_MOST_RECENT =
            DatabaseContract.scores_table.DATE_COL + " <= ? AND "+
            DatabaseContract.scores_table.HOME_GOALS_COL + " != 'null' AND "+
            DatabaseContract.scores_table.AWAY_GOALS_COL + " != 'null'";

    // to identify the broadcast message that data has been updated
    public static final String ACTION_DATA_UPDATED = DatabaseContract.CONTENT_AUTHORITY + ".ACTION_DATA_UPDATED";


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.BASE_CONTENT_URI.toString();
        matcher.addURI(authority, null , MATCHES);
        matcher.addURI(authority, DatabaseContract.scores_table.LEAGUE_COL , MATCHES_WITH_LEAGUE);
        matcher.addURI(authority, DatabaseContract.scores_table.MATCH_ID , MATCHES_WITH_ID);
        matcher.addURI(authority, DatabaseContract.scores_table.DATE_COL , MATCHES_WITH_DATE);
        matcher.addURI(authority, DatabaseContract.PATH_RECENT, MATCHES_MOST_RECENT);
        return matcher;
    }

    private int match_uri(Uri uri)
    {
        String link = uri.toString();

        if(link.contentEquals(DatabaseContract.BASE_CONTENT_URI.toString()))
        {
           return MATCHES;
        }
        else if(link.contentEquals(DatabaseContract.scores_table.buildScoreWithDate().toString()))
        {
           return MATCHES_WITH_DATE;
        }
        else if(link.contentEquals(DatabaseContract.scores_table.buildScoreWithId().toString()))
        {
           return MATCHES_WITH_ID;
        }
        else if(link.contentEquals(DatabaseContract.scores_table.buildScoreWithLeague().toString()))
        {
           return MATCHES_WITH_LEAGUE;
        }
        else if(link.contentEquals(DatabaseContract.scores_table.buildScoreMostRecent().toString()))
        {
           return MATCHES_MOST_RECENT;
        }
        return -1;
    }

    @Override
    public boolean onCreate()
    {
        mOpenHelper = new ScoresDBHelper(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri)
    {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case MATCHES:
                return DatabaseContract.scores_table.CONTENT_TYPE;
            case MATCHES_WITH_LEAGUE:
                return DatabaseContract.scores_table.CONTENT_TYPE;
            case MATCHES_WITH_DATE:
                return DatabaseContract.scores_table.CONTENT_TYPE;
            case MATCHES_WITH_ID:
                return DatabaseContract.scores_table.CONTENT_ITEM_TYPE;
            case MATCHES_MOST_RECENT:
                return DatabaseContract.scores_table.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri );
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor retCursor;
        int match = match_uri(uri);

        switch (match)
        {
            //all matches
            case MATCHES:
                retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.SCORES_TABLE,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder);
                break;
            case MATCHES_WITH_DATE:
                // matches by date
                retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.SCORES_TABLE,
                    projection,
                    SCORES_BY_DATE,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
                break;
            case MATCHES_WITH_ID:
                // matches by id
                retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.SCORES_TABLE,
                    projection,
                    SCORES_BY_ID,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
                break;
            case MATCHES_WITH_LEAGUE:
                // matches by league
                retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.SCORES_TABLE,
                    projection,
                    SCORES_BY_LEAGUE,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
                break;
            case MATCHES_MOST_RECENT:
                // most recent matches
                retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.SCORES_TABLE,
                    projection,
                    SCORES_MOST_RECENT,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
                break;
            default: throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        if (getContext() != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (match_uri(uri))
        {
            case MATCHES:
                // insert matches
                db.beginTransaction();
                int returncount = 0;
                try
                {
                    for(ContentValues value : values)
                    {
                        long _id = db.insertWithOnConflict(
                                DatabaseContract.SCORES_TABLE,
                                null,
                                value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1)
                        {
                            returncount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } catch (SQLiteException e) {
                    Log.d("Bulk Insert", "");
                } finally {
                    db.endTransaction();
                }
                if (getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return returncount;
            default:
                return super.bulkInsert(uri,values);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
