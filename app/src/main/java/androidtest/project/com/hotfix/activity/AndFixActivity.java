package androidtest.project.com.hotfix.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import androidtest.project.com.hotfix.R;
import androidtest.project.com.hotfix.utils.AndFixPatchManager;

/**
 * @author liuboyu  E-mail:545777678@qq.com
 * @Date 2019-08-02
 */
public class AndFixActivity extends AppCompatActivity {
    private static final String FILE_END = ".apatch";
    private String mPatchDir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_andfix);
        initPatchDir();
    }

    /**
     * 初始化文件夹
     */
    private void initPatchDir() {
        mPatchDir = getExternalCacheDir().getAbsolutePath() + "/apatch/";
        File file = new File(mPatchDir);
        if (file == null || !file.exists()) {
            file.mkdir();
        }
    }


    public void makeBug(View view) {
//        int i = 1/0;
        Toast.makeText(AndFixActivity.this, "bug 修复啦～", Toast.LENGTH_LONG).show();
    }

    public void fixBug(View view) {
        String path = getPath();
        AndFixPatchManager.getInstance().addPatch(path);
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
