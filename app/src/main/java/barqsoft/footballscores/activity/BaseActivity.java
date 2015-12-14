package barqsoft.footballscores.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import barqsoft.footballscores.ScoresApplication;


public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScoresApplication.get(this).getRefWatcher().watch(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void setTitle(int titleId) {
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setTitle(titleId);
        else
            super.setTitle(titleId);
    }
}
