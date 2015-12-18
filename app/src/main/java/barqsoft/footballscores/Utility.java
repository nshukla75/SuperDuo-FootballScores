package barqsoft.footballscores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import barqsoft.footballscores.service.myFetchService;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utility
{
    /*public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;*/
    public static String getLeague(Context context,int league_num)
    {
        // get league codes and label from resources
        int[] leagueCodes = context.getResources().getIntArray(R.array.league_codes);
        String[] leagueLabels = context.getResources().getStringArray(R.array.league_labels);

        // find the position of the league code and we get the league label (same index)
        for(int i=0; i < leagueCodes.length; i++) {
            if (leagueCodes[i] == league_num) {
                return leagueLabels[i];
            }
        }

        return context.getString(R.string.league_unknown);
    }
    public static String getMatchDay(Context context, int match_day,int league_num)
    {
        // use an algoritm for the champions league
        if (league_num == R.integer.league_champions_league_code) {
            if (match_day <= 6) {
                return context.getString(R.string.group_stage_text) +", "+
                        context.getString(R.string.matchday_text) +": "+ String.valueOf(match_day);
            } else if (match_day == 7 || match_day == 8) {
                return context.getString(R.string.first_knockout_round);
            } else if (match_day == 9 || match_day == 10) {
                return context.getString(R.string.quarter_final);
            } else if (match_day == 11 || match_day == 12) {
                return context.getString(R.string.semi_final);
            } else {
                return context.getString(R.string.final_text);
            }
        } else {
            // else just return the match day
            return context.getString(R.string.matchday_text) +": " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static boolean isNetworkAvailable(Context context) {

        // get the connectivity manager service
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // get info about the network
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        // return true if we have networkinfo and are connected
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean contains(final int[] array, final int key) {

        // loop and return true when found
        for (final int i : array) {
            if (i == key) {
                return true;
            }
        }

        return false;
    }
    public static boolean startFootballDataService(Activity activity) {

        // check if we have a network connection and the football data api key is set
        if (Utility.isNetworkAvailable(activity)) {

            // start the football-data service to trigger loading the teams and fixtures
            Intent footballDataService = new Intent(activity, myFetchService.class);
            footballDataService.putExtra(activity.getString(R.string.pref_apikey_key), BuildConfig.FOOTBALL_DATA_API_KEY);
            activity.startService(footballDataService);

            return true;
        } else {
            return false;
        }
    }
    public static boolean isRtl(Context context) {
        boolean rtl = false;

        // check the direction depending on the api level the device is running
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            // if we are running on API 17 or higher we can use getlayoutdirection
            Configuration config = context.getResources().getConfiguration();
            if(config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                rtl = true;
            }
        } else {

            // else we have to base it on the locale setting and a collection of rtl language codes
            Set<String> lang = new HashSet<String>();
            lang.add("ar");
            lang.add("dv");
            lang.add("fa");
            lang.add("ha");
            lang.add("he");
            lang.add("iw");
            lang.add("ji");
            lang.add("ps");
            lang.add("ur");
            lang.add("yi");
            Set<String> RTL = Collections.unmodifiableSet(lang);

            // get the users' locale
            Locale locale = Locale.getDefault();

            // check if the rtl collection contains the local language setting
            rtl = RTL.contains(locale.getLanguage());
        }

        return rtl;
    }
    public static Bitmap scaleBitmap(Context context, Bitmap bitmap, int height) {

        // get the device density
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        // calculate the height, and relative width
        int newHeight = (int) (height * densityMultiplier);
        int newWidth = (int) (newHeight * bitmap.getWidth() / ((double) bitmap.getHeight()));

        // scale the bitmap
        bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        return bitmap;
    }


   /* public static int getTeamCrestByTeamName (String teamname)
    {
        if (teamname==null){return R.drawable.no_icon;}
        switch (teamname)
        { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_icon;
        }
    }*/

}
