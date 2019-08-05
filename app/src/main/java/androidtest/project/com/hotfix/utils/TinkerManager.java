package androidtest.project.com.hotfix.utils;

import android.content.Context;

import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;

/**
 * @author liuboyu  E-mail:545777678@qq.com
 * @Date 2019-08-04
 * @Description tinker manager 方便使用
 */
public class TinkerManager {
    private static TinkerManager manager;
    private ApplicationLike tinkerApplicationLike;

    private TinkerManager() {

    }

    public static TinkerManager getInstance() {
        if (manager == null) {
            synchronized (TinkerManager.class) {
                if (manager == null) {
                    manager = new TinkerManager();
                }
            }
        }
        return manager;
    }

    /**
     * tinker 初始化
     */
    public void init() {
        // 我们可以从这里获得Tinker加载过程的信息
        tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
        // 初始化TinkerPatch SDK, 更多配置可参照API章节中的,初始化SDK
        TinkerPatch.init(tinkerApplicationLike)
                .reflectPatchLibrary()
                .setPatchRollbackOnScreenOff(true)
                .setPatchRestartOnSrceenOff(true)
                .setFetchPatchIntervalByHours(3);

        // 每隔3个小时(通过setFetchPatchIntervalByHours设置)去访问后台时候有更新,通过handler实现轮训的效果
        TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();
    }

    public void loadPatch(String path) {
        TinkerInstaller.onReceiveUpgradePatch(getContext(), path);
    }

    private Context getContext() {
        if (tinkerApplicationLike == null) {
            return null;
        }
        return tinkerApplicationLike.getApplication();
    }
}
