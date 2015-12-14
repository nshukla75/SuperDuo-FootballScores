package barqsoft.footballscores.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utility;
import barqsoft.footballscores.data.DatabaseContract;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class scoresAdapter extends CursorAdapter
{
    public scoresAdapter(Context context,Cursor cursor,int flags)
    {
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        // get the listitem layout
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        // add the matchholder
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        final ViewHolder mHolder = (ViewHolder) view.getTag();

        // home club name
        String homeTeamName = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
        mHolder.home_name.setText(homeTeamName);

        // home club logo
        Glide.with(context.getApplicationContext()).load(
                cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_LOGO_COL)))
                .error(R.drawable.football)
                .into( mHolder.home_crest);
        mHolder.home_crest.setContentDescription(homeTeamName);

        // match score

        mHolder.score.setText(Utility.getScores(
                cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL)),
                cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL))));

        // match time
        mHolder.date.setText(
                cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.TIME_COL)));

        // away club name
        String awayTeamName = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));
        mHolder.away_name.setText(awayTeamName);

        // away club logo
        Glide.with(context.getApplicationContext()).load(
                cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_LOGO_COL)))
                .error(R.drawable.football)
                .into(mHolder.away_crest);
        mHolder.away_crest.setContentDescription(awayTeamName);

        // detail: league
        TextView league = (TextView) view.findViewById(R.id.league_textview);
        league.setText(Utility.getLeague(context,
                cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.LEAGUE_COL))));


        // detail: match day
        TextView match_day = (TextView) view.findViewById(R.id.matchday_textview);
        match_day.setText(Utility.getMatchDay(context,
                cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.MATCH_DAY)),
                cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.LEAGUE_COL))));

        // detail: share button
        Button share_button = (Button) view.findViewById(R.id.share_button);
        share_button.setOnClickListener(new View.OnClickListener() {
            /**
             * Add share action onclick
             * @param v View
             */
            @Override
            public void onClick(View v) {
                context.startActivity(createShareScoreIntent(mHolder.home_name.getText() + " " +
                                mHolder.score.getText() + " " +
                                mHolder.away_name.getText() + " " +
                                context.getString(R.string.share_hashtag)
                ));
            }
        });
    }


    public Intent createShareScoreIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText);
        return shareIntent;
    }

}
