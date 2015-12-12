package barqsoft.footballscores.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utility;
import barqsoft.footballscores.activity.MainActivity;
import barqsoft.footballscores.fragment.MainScreenFragment;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment
{
    // set the amount of pages in the viewpager
    public static final int NUM_PAGES = 5;

    // reference to the viewpager object
    public ViewPager mPagerHandler;

    // reference to the viewpager object
    private myPageAdapter mPagerAdapter;

    // array containing the pagefragments
    private MainScreenFragment[] viewFragments = new MainScreenFragment[NUM_PAGES];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);

        // call the myFetchservice to load the scores (not on rotate)
        if (savedInstanceState == null) {
            updateScores();
        }

        // get a reference to the viewpager and set the pageadapter
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new myPageAdapter(getChildFragmentManager());
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);

        // create the pagefragments and set the date for each page (for days ago until 2 days from now)
        for (int i = 0;i < NUM_PAGES;i++)
        {
            // create the date for each pagefragment
            Date fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            // create the fragment with date and add it to the array
            viewFragments[i] = new MainScreenFragment();
            viewFragments[i].setFragmentDate(mformat.format(fragmentdate));
        }
        // reverse the order of the page date fragments when in rtl mode
        if (Utility.isRtl(getContext())) {
            Collections.reverse(Arrays.asList(viewFragments));
        }

        // set tabindicator color and tab padding programmatically
        PagerTabStrip strip = (PagerTabStrip) rootView.findViewById(R.id.pager_header);
        strip.setTabIndicatorColor(ContextCompat.getColor(getContext(), android.R.color.white));
        strip.setPadding(
                (int) getResources().getDimension(R.dimen.tabstrip_tab_padding_horizontal),
                0,
                (int) getResources().getDimension(R.dimen.tabstrip_tab_padding_horizontal),
                0);
        return rootView;
    }

    public void updateScores() {

        // check if we have a network connection
        if (Utility.isNetworkAvailable(getActivity())) {

            // start the football-data service to trigger loading the teams and fixtures
            Utility.startFootballDataService(getActivity());

        } else {

            // if without internet, show a notice to the user in the form of a toast
            Toast.makeText(getContext(), getString(R.string.network_required_notice), Toast.LENGTH_SHORT).show();
        }
    }

    private class myPageAdapter extends FragmentStatePagerAdapter
    {
        public myPageAdapter(FragmentManager fm){super(fm);}

        @Override
        public Fragment getItem(int i)
        {
            return viewFragments[i];
        }

        @Override
        public int getCount(){return NUM_PAGES;}

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position)
        {
            long dateInMillis = 0;
            // get the time of given day position -xdays in milliseconds (or +xdays when in rtl mode)
            if (Utility.isRtl(getContext())) {
                dateInMillis = System.currentTimeMillis() - ((position - 2) * 86400000);
            } else {
                dateInMillis = System.currentTimeMillis() + ((position - 2) * 86400000);
            }
            return getDayName(dateInMillis);
        }
        public String getDayName(long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return getActivity().getString(R.string.today);
            } else if ( julianDay == currentJulianDay +1 ) {
                return getActivity().getString(R.string.tomorrow);
            }
             else if ( julianDay == currentJulianDay -1)
            {
                return getActivity().getString(R.string.yesterday);
            }
            else
            {
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE",Locale.US);
                return dayFormat.format(dateInMillis).toUpperCase();
            }
        }

    }
}
