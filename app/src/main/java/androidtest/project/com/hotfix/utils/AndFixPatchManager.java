package androidtest.project.com.hotfix.utils;

import android.content.Context;

import com.alipay.euler.andfix.patch.PatchManager;

/**
 * @author liuboyu  E-mail:545777678@qq.com
 * @Date 2019-08-02
 * * @function 管理AndFix所有的api
 */
public class AndFixPatchManager {

    private static AndFixPatchManager mInstance = null;

    private static PatchManager mPatchManager = null;

    /**
     * 初始化
     * @return
     */
    public static AndFixPatchManager getInstance() {
        if (mInstance == null) {
            synchronized (AndFixPatchManager.class) {
                if (mInstance == null) {
                    mInstance = new AndFixPatchManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化AndFix方法
     * @param context
     */
    public void initPatch(Context context) {
        mPatchManager = new PatchManager(context);
        mPatchManager.init(Utils.getVersionName(context));
        mPatchManager.loadPatch();
    }

    /**
     * 加载我们的patch文件
     * @param path
     */
    public void addPatch(String path) {
        try {
            if (mPatchManager != null) {
                mPatchManager.addPatch(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}







