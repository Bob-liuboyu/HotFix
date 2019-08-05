package androidtest.project.com.hotfix.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import androidtest.project.com.hotfix.R;
import androidtest.project.com.hotfix.utils.TinkerManager;

/**
 * @author liuboyu  E-mail:545777678@qq.com
 * @Date 2019-08-02
 */
public class TinkerActivity extends AppCompatActivity {

    private static final String FILE_END = ".apk";
    private String mPatchDir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tinker);
        initPatchDir();
    }

    /**
     * 初始化文件夹
     */
    private void initPatchDir() {
        mPatchDir = getExternalCacheDir().getAbsolutePath() + "/apatch_tinker/";
        File file = new File(mPatchDir);
        if (file == null || !file.exists()) {
            file.mkdir();
        }
    }

    public void makeBug(View view) {
        Toast.makeText(TinkerActivity.this, "bug 修复啦～", Toast.LENGTH_LONG).show();
    }

    public void fixBug(View view) {
        String path = getPath();
        Log.e("XXX", path);
        TinkerManager.getInstance().loadPatch(path);
    }

    /**
     * apatch 路径
     *
     * @return
     */
    private String getPath() {
        return mPatchDir.concat("testhotfix").concat(FILE_END);
    }
}
