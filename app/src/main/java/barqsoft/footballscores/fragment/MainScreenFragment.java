package barqsoft.footballscores.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utility;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.adapter.scoresAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    // unique loader id
    public static final int SCORES_LOADER = 0;

    // cursor adapter for loading the scores
    public scoresAdapter mAdapter;

    // date of this pagefragment
    private String fragmentdate;
    private View mEmptyView;


    public MainScreenFragment()
    {
    }
    /*private String[] fragmentdate = new String[1];
    private int last_selected_item = -1;
    private void update_scores()
    {
        Intent service_start = new Intent(getActivity(), myFetchService.class);
        getActivity().startService(service_start);
    }
    public void setFragmentDate(String date)
    {
        fragmentdate[0] = date;
    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mEmptyView = rootView.findViewById(R.id.scores_empty_view);
        // create the scoresadapter
        mAdapter = new scoresAdapter(getActivity(),null,0);

        // get the scores listview and attach the adapter and clickhandler
        ListView score_list = (ListView) rootView.findViewById(R.id.scores_list);
        score_list.setAdapter(mAdapter);
        score_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // toggle detailsview of selected item
                View detailsView = view.findViewById(R.id.match_details);
                if (detailsView.getVisibility() != View.VISIBLE) {
                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.score_selected));
                    detailsView.setVisibility(View.VISIBLE);
                    if ((position + 1) == parent.getCount()) {
                        // if this is the last listview item scroll to it by selecting it
                        parent.setSelection(position);
                    }
                } else {
                    view.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
                    detailsView.setVisibility(View.GONE);
                }

                // hide detailsview of all other items in the list
                for (int i = 0; i < parent.getCount(); i++) {
                    View otherItem = parent.getChildAt(i);
                    if (otherItem != null && otherItem != view) {
                        otherItem.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
                        View otherItemDetailsView = otherItem.findViewById(R.id.match_details);
                        otherItemDetailsView.setVisibility(View.GONE);
                    }
                }
            }
        });
        score_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             * Hide the detailsview of all listitems on scroll
             * @param absListView AbsListView
             * @param i int
             */
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                for (int j = 0; j < absListView.getCount(); j++) {
                    View otherItem = absListView.getChildAt(j);
                    if (otherItem != null) {
                        otherItem.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
                        View otherItemDetailsView = otherItem.findViewById(R.id.match_details);
                        otherItemDetailsView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });

        // initialise the loader
        getLoaderManager().initLoader(SCORES_LOADER,null,this);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_refresh == item.getItemId()) {
            onRefresh();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    public void onRefresh() {
        if (Utility.isNetworkAvailable(getActivity())) {

            // start the football-data service to trigger loading the teams and fixtures
            Utility.startFootballDataService(getActivity());

        } else {

            // if without internet, show a notice to the user in the form of a toast
            Toast.makeText(getContext(), getString(R.string.network_required_notice), Toast.LENGTH_SHORT).show();
        }
    }


    public void setFragmentDate(String date) {
        fragmentdate = date;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(getActivity(),
                DatabaseContract.scores_table.buildScoreWithDate(),
                null,
                null,
                new String[] { fragmentdate },
                DatabaseContract.scores_table.TIME_COL +" ASC, "+ DatabaseContract.scores_table.HOME_COL +" ASC"
        );
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        final int size = cursor.getCount();
        mAdapter.swapCursor(cursor);
        showEmptyView(size == 0);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mAdapter.swapCursor(null);
        showEmptyView(true);
    }
    private void showEmptyView(boolean show) {
        if (mEmptyView != null) mEmptyView.animate().alpha(show ? 1 : 0).setDuration(200).start();
    }


}
