package barqsoft.footballscores;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.data.DatabaseContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    // unique loader id
    public static final int SCORES_LOADER = 0;

    // cursor adapter for loading the scores
    public scoresAdapter mAdapter;

    // date of this pagefragment
    private String fragmentdate;

    /*private String[] fragmentdate = new String[1];
    private int last_selected_item = -1;

    public MainScreenFragment()
    {
    }

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
        // create the scoresadapter
        mAdapter = new scoresAdapter(getActivity(),null,0);

        // get the scores listview and attach the adapter and clickhandler
        ListView score_list = (ListView) rootView.findViewById(R.id.scores_list);
        score_list.setAdapter(mAdapter);
        score_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // toggle detailsview of selected item
                ViewHolder selected = (ViewHolder) view.getTag();
                mAdapter.detail_match_id = selected.match_id;
                MainActivity.selected_match_id = (int) selected.match_id;
                mAdapter.notifyDataSetChanged();
            }
        });
        getLoaderManager().initLoader(SCORES_LOADER,null,this);
        mAdapter.detail_match_id = MainActivity.selected_match_id;

        return rootView;
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
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mAdapter.swapCursor(null);
    }


}
