package barqsoft.footballscores;

import android.app.Application;
import android.content.Context;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public final class ScoresApplication extends Application {

    private RefWatcher mRefWatcher;

    public static ScoresApplication get(Context context) {
        return (ScoresApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRefWatcher = installLeakCanary();
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    protected RefWatcher installLeakCanary() {
        return LeakCanary.install(this);
    }

}
