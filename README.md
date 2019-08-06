在Android应用开发中，热修复技术被越来越多的开发者所使用，也出现了很多热修复框架，比如：AndFix、Tinker、Dexposed和Nuwa等等。如果只是会这些热修复框架的使用那意义并不大，我们还需要了解它们的原理，这样不管热修复框架如何变化，只要基本原理不变，我们就可以很快的掌握它们。这一个系列不会对某些热修复框架源码进行解析，而是讲解热修复框架的通用原理。

### 1.热修复的产生概述

在开发中我们会遇到如下的情况：
1. 刚发布的版本出现了严重的bug，这就需要去解决bug、测试并打渠道包在各个应用市场上重新发布，这会耗费大量的人力物力，代价会比较大。
2. 已经改正了此前发布版本的bug，如果下一个版本是一个大版本，那么两个版本的间隔时间会很长，这样要等到下个大版本发布再修复bug，这样此前版本的bug会长期的影响用户。
3. 版本升级率不高，并且需要很长时间来完成版本覆盖，此前版本的bug就会一直影响不升级版本的用户。
4. 有一个小而重要的功能，需要短时间内完成版本覆盖，比如节日活动。

为了解决上面的问题，热修复框架就产生了。对于Bug的处理，开发人员不要过于依赖热修复框架，在开发的过程中还是要按照标准的流程做好自测、配合测试人员完成测试流程。
### 2.热修复框架的对比

热修复框架的种类繁多，按照公司团队划分主要有以下几种：

![屏幕快照 2019-08-05 下午3.28.10.png](https://upload-images.jianshu.io/upload_images/1959357-b8cc112586d8d56f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


虽然热修复框架很多，但热修复框架的核心技术主要有三类，分别是代码修复、资源修复和动态链接库修复，其中每个核心技术又有很多不同的技术方案，每个技术方案又有不同的实现，另外这些热修复框架仍在不断的更新迭代中，可见热修复框架的技术实现是繁多可变的。作为开发需需要了解这些技术方案的基本原理，这样就可以以不变应万变。

部分热修复框架的对比如下表所示。

![屏幕快照 2019-08-05 下午5.06.17.png](https://upload-images.jianshu.io/upload_images/1959357-a6ea60df2cf80e95.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


我们可以根据上表和具体业务来选择合适的热修复框架，当然上表的信息很难做到完全准确，因为部分的热修复框架还在不断更新迭代。

总的来说:

1. AndFix作为native解决方案，首先面临的是稳定性与兼容性问题，更重要的是它无法实现类替换，它是需要大量额外的开发成本的；
2. Robust兼容性与成功率较高，但是它与AndFix一样，无法新增变量与类只能用做的bugFix方案；
3. Qzone方案可以做到发布产品功能，但是它主要问题是插桩带来Dalvik的性能问题，以及为了解决Art下内存地址问题而导致补丁包急速增大的。

下面我们以 AndFix 和 Tinker 作为例子讲解

## AndFix

废话不多说，先看效果

![andfix.gif](https://upload-images.jianshu.io/upload_images/1959357-01eca810b4b64c28.gif?imageMogr2/auto-orient/strip)

AndFix，全称是Android hot-fix。是阿里开源的一个Android热补丁框架，允许APP在不重新发版本的情况下修复线上的bug。支持Android 2.3 到 6.0。

andfix的github地址： https://github.com/alibaba/AndFix

底层替换是在已经加载了的类中直接在native层替换掉原有方法，见下图。
**注意 andfix 只能替换方法不能替换类文件**

![屏幕快照 2019-08-05 下午3.44.30.png](https://upload-images.jianshu.io/upload_images/1959357-af6e9b1ee556b5ba.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### 集成

和大多数第三方库一样，两种方式集成
#### maven dependency
```
<dependency>
  	<groupId>com.alipay.euler</groupId>
  	<artifactId>andfix</artifactId>
  	<version>0.5.0</version>
  	<type>aar</type>
</dependency>
```
#### gradle dependency
```
dependencies {
	compile 'com.alipay.euler:andfix:0.5.0@aar'
}
```

### 使用
#### 初始化 PatchManager
初始化要尽早，所以推荐在 application 的 onCreate() 中进行初始化
```
  /**
    * 初始化AndFix方法
    * @param context
    */
   public void initPatch(Context context) {
       mPatchManager = new PatchManager(context);
       mPatchManager.init(Utils.getVersionName(context));
       mPatchManager.loadPatch();
   }
```

#### Add patch
```
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
```

### 实践

我们再点击按钮处，写一个bug，再点击修复，加载相应的 apatch 文件进行热修复
```
public void makeBug(View view) {
    int i = 1/0;
}

public void fixBug(View view) {
    String path = getPath();
    AndFixPatchManager.getInstance().addPatch(path);
}
```

修改后的方法

```
public void makeBug(View view) {
//        int i = 1/0;
        Toast.makeText(AndFixActivity.this, "bug 修复啦～", Toast.LENGTH_LONG).show();
    }
```

生成 apatch 文件需要 andfix 提供的 [apkpatch 工具](https://github.com/alibaba/AndFix/raw/master/tools/apkpatch-1.0.3.zip)
apkpatch 语法相对简单，照着敲就好了
```
usage: apkpatch -f <new> -t <old> -o <output> -k <keystore> -p <***> -a <alias> -e <***>
 -a,--alias <alias>     keystore entry alias.
 -e,--epassword <***>   keystore entry password.
 -f,--from <loc>        new Apk file path.
 -k,--keystore <loc>    keystore path.
 -n,--name <name>       patch name.
 -o,--out <dir>         output dir.
 -p,--kpassword <***>   keystore password.
 -t,--to <loc>          old Apk file path.
```

以我们的代码为例
```
./apkpatch.sh -f fix-bug-release.apk -t bug-release.apk -o output/ -k key.jks -p 123456 -a testhotfix -e 123456
```
我们会看到 makeBug 方法被修改了，并生成了对应的 testhotfix.apatch 文件

![屏幕快照 2019-08-02 下午7.51.57.png](https://upload-images.jianshu.io/upload_images/1959357-0f8a12296d2da922.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

再将 testhotfix.apatch 文件push到我们的内存卡中，调用替换方法即可
```
adb push testhotfix.apatch /storage/emulated/0/Android/data/androidtest.project.com.hotfix/cache/apatch/testhotfix.apatch
```

## Tinker

废话不多说，先看效果

![tinker.gif](https://upload-images.jianshu.io/upload_images/1959357-8517251dfec613a6.gif?imageMogr2/auto-orient/strip)


Tinker 是一个开源项目([Github链接](https://github.com/Tencent/tinker))，它是微信官方的 Android 热补丁解决方案，它支持动态下发代码、So 库以及资源，让应用能够在不需要重新安装的情况下实现更新。

Tinker热补丁方案不仅支持类、So 以及资源的替换，它还是2.X－7.X的全平台支持。利用Tinker我们不仅可以用做 bugfix,甚至可以替代功能的发布
### 集成
#### 第一步 添加 gradle 插件依赖
gradle 远程仓库依赖 jcenter
```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        // TinkerPatch 插件
        classpath "com.tinkerpatch.sdk:tinkerpatch-gradle-plugin:1.2.13.3"
    }
}
```
注意，在这里 SDK 使用了 fat 打包的模式，我们不能再引入任何 Tinker 的相关依赖，否则会造成版本冲突。
#### 第二步 集成 TinkerPatch SDK
添加 TinkerPatch SDK 库的 denpendencies 依赖
```
dependencies {
    // 若使用annotation需要单独引用,对于tinker的其他库都无需再引用
    provided("com.tinkerpatch.tinker:tinker-android-anno:1.9.13.3")
    compile("com.tinkerpatch.sdk:tinkerpatch-android-sdk:1.2.13.3")
}
```

注意,若使用 annotation 自动生成 Application， 需要单独引入 Tinker 的 tinker-android-anno 库。除此之外，我们无需再单独引入 tinker 的其他库。

为了简单方便，我们将 TinkerPatch 相关的配置都放于 tinkerpatch.gradle 中, 我们需要将其引入：
```
apply from: 'tinkerpatch.gradle'
```
#### 第三步 配置 tinkerpatchSupport 参数
打开引入的 tinkerpatch.gradle 文件，它的具体参数如下：
```
apply plugin: 'tinkerpatch-support'

def bakPath = file("${buildDir}/bakApk/")
def baseInfo = "app-1.0.0-0805-14-39-56"
def variantName = "release"

/**
 * 对于插件各参数的详细解析请参考
 * http://tinkerpatch.com/Docs/SDK
 */
tinkerpatchSupport {
    /** 可以在debug的时候关闭 tinkerPatch, isRelease() 可以判断BuildType是否为Release **/
    tinkerEnable = true

    /** 注意: 如果没有改造application 这句话必须写，不然报错 **/
    reflectApplication = true

    autoBackupApkPath = "${bakPath}"

    appKey = "9ee82e4246771934"

    /** 注意: 若发布新的全量包, appVersion一定要更新 **/
    appVersion = "1.0.0"

    def pathPrefix = "${bakPath}/${baseInfo}/${variantName}"
    def name = "${project.name}-${variantName}"

    /** 基础 apk 命名 **/
    baseApkFile = "${pathPrefix}/${name}.apk"
    /** 基础 mapping 文件命名 **/
    baseProguardMappingFile = "${pathPrefix}/${name}-mapping.txt"
    /** 基础 R 文件命名 **/
    baseResourceRFile = "${pathPrefix}/${name}-R.txt"
}

/**
 * 用于用户在代码中判断tinkerPatch是否被使能
 */
android {
    defaultConfig {
        buildConfigField "boolean", "TINKER_ENABLE", "${tinkerpatchSupport.tinkerEnable}"
    }
}

/**
 * 一般来说,我们无需对下面的参数做任何的修改
 * 对于各参数的详细介绍请参考:
 * https://github.com/Tencent/tinker/wiki/Tinker-%E6%8E%A5%E5%85%A5%E6%8C%87%E5%8D%97
 */
tinkerPatch {
    ignoreWarning = false
    useSign = true
    dex {
        dexMode = "jar"
        pattern = ["classes*.dex"]
        loader = []
    }
    lib {
        pattern = ["lib/*/*.so"]
    }

    res {
        pattern = ["res/*", "r/*", "assets/*", "resources.arsc", "AndroidManifest.xml"]
        ignoreChange = []
        largeModSize = 100
    }

    packageConfig {
    }
    sevenZip {
        zipArtifact = "com.tencent.mm:SevenZip:1.1.10"
    }
    buildConfig {
        keepDexApply = false
    }
}
```
它的具体含义如下：

![屏幕快照 2019-08-05 下午4.37.59.png](https://upload-images.jianshu.io/upload_images/1959357-7ece2448dcb7f160.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


一般来说，我们无需修改引用 android 的编译配置，也不用修改 tinker 插件原来的配置
#### 第四步 初始化 TinkerPatch SDK
最后在我们的代码中，只需简单的初始化 TinkerPatch 的 SDK 即可，我们无需考虑 Tinker 是如何下载/合成/应用补丁包， 也无需引入各种各样 Tinker 的相关类。

##### 1. reflectApplication = true 的情况
若我们使用 reflectApplication 模式，我们无需为接入 Tinker 而改造我们的 Application 类。
```
public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
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
```

##### 2. reflectApplication = false 的情况
若我们已经完成了应用的 Application 改造，即将 Application 的逻辑移动到 ApplicationLike类中
```
public class SampleApplicationLike extends DefaultApplicationLike {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化TinkerPatch SDK, 更多配置可参照API章节中的,初始化 SDK
        TinkerPatch.init(this)
            .reflectPatchLibrary()
            .setPatchRollbackOnScreenOff(true)
            .setPatchRestartOnSrceenOff(true)
            .setFetchPatchIntervalByHours(3);

        // 每隔3个小时（通过setFetchPatchIntervalByHours设置）去访问后台时候有更新,通过handler实现轮训的效果
        TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();
    }
}
```

注意：初始化的代码建议紧跟 super.onCreate(),并且所有进程都需要初始化，已达到所有进程都可以被 patch 的目的

如果你确定只想在主进程中初始化 tinkerPatch，那也请至少在 :patch 进程中初始化，否则会有造成 :patch 进程crash，无法使补丁生效

### 实践
我们再点击按钮处，写一个bug，再点击修复，加载相应的 apatch 文件进行热修复
```
public void makeBug(View view) {
    int i = 1/0;
}

public void fixBug(View view) {
    String path = getPath();
    AndFixPatchManager.getInstance().addPatch(path);
}
```

修改后的方法

```
public void makeBug(View view) {
//        int i = 1/0;
        Toast.makeText(AndFixActivity.this, "bug 修复啦～", Toast.LENGTH_LONG).show();
}
```
#### 命令行方式
同样，Tinker 也支持命令行生成 apatch，不过 tinker 与 andfix 不同的是 tinker 的后缀是 .apk

命令行工具tinker-patch-cli.jar提供了基准包与新安装包做差异，生成补丁包的功能

```
java -jar tinker-patch-cli.jar -old old.apk -new new.apk -config tinker_config.xml -out output_path
```
参数与gradle基本一致，新增的sign参数，我们需要输入签名路径与签名信息。

同时，我们需要自己保证proguard文件以及main dex类是正确的。具体配置可参考以下几个文件：

- [tinker_config.xml](https://github.com/Tencent/tinker/blob/master/tinker-build/tinker-patch-cli/tool_output/tinker_config.xml)
- [tinker_proguard.pro](https://github.com/Tencent/tinker/blob/master/tinker-build/tinker-patch-cli/tool_output/tinker_proguard.pro)
- [tinker_multidexkeep.pro](https://github.com/Tencent/tinker/blob/master/tinker-build/tinker-patch-cli/tool_output/tinker_multidexkeep.pro)

需要注意的是 tinker_config.xml 有两处需要修改

![1564997478653.jpg](https://upload-images.jianshu.io/upload_images/1959357-c7e0bf322a768482.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![1564997514473.jpg](https://upload-images.jianshu.io/upload_images/1959357-e8ff0f3dab1f7244.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



与 andfix 一样，将生成的 patch_signed_7zip.apk 导入手机内存卡

```
 adb push patch_signed_7zip.apk /storage/emulated/0/Android/data/androidtest.project.com.hotfix/cache/apatch_tinker/testhotfix.apk
```

**小弟有一处不解,layout 中的修改生效了，类文件中的修改却没有生效。。。试了各种办法,哪位大神知晓，麻烦告诉小弟一下**

#### gradle方式

修改文件 tinkerpatch.gradle 中的值即可
```
def baseInfo = "app-1.0.0-0805-14-39-56"
/** 注意: 若发布新的全量包, appVersion一定要更新 **/
appVersion = "1.0.0"
```

点击android studio 右侧 gradle 按钮

![屏幕快照 2019-08-05 下午5.44.24.png](https://upload-images.jianshu.io/upload_images/1959357-a77b2bdd3baff09a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


即可生成相应的 patch_signed_7zip.apk

![屏幕快照 2019-08-05 下午3.10.29.png](https://upload-images.jianshu.io/upload_images/1959357-8bba869d8c259124.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

同理 push 到手机内存卡即可

#### 接入时可能遇到的问题

接入Tinker时，打包的时候出现以下错误com.tencent.tinker.loader.TinkerRuntimeException: Tinker Exception:applicationLike must not be null.：是因为你的 tinkerPatch.gradle中配置 reflectApplication = false，但是你又没有相应的改造你的Application类。本文介绍的是不改造我们的 Application 类接入Tinker，所以 配置应该为：reflectApplication = ture。

![屏幕快照 2019-08-05 下午6.10.39.png](https://upload-images.jianshu.io/upload_images/1959357-bbf4606f9530424a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


最后奉上Demo下载地址：[HotFix Github链接](https://github.com/Bob-liuboyu/HotFix)
