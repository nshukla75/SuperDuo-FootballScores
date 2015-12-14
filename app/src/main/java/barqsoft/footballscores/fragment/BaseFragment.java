package barqsoft.footballscores.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.view.View;

import barqsoft.footballscores.ScoresApplication;


public abstract class BaseFragment extends Fragment {

    @CallSuper
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @CallSuper
    @Override
    public void onDestroy() {
        super.onDestroy();
        ScoresApplication.get(getActivity()).getRefWatcher().watch(this);
    }
}
