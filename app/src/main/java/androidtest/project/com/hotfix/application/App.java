package androidtest.project.com.hotfix.application;

import android.app.Application;
import android.support.multidex.MultiDex;

import androidtest.project.com.hotfix.utils.AndFixPatchManager;
import androidtest.project.com.hotfix.utils.TinkerManager;

/**
 * @author liuboyu  E-mail:545777678@qq.com
 * @Date 2019-08-02
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);
        //初始化 Tinker
        TinkerManager.getInstance().init();
        //初始化 AndFix
        AndFixPatchManager.getInstance().initPatch(this);
    }
}
