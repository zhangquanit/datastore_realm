package com.example.realm.demo;

import android.app.Application;
import android.os.Process;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author zhangquan
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("App process=" + Process.myPid());
        Realm.init(this);

        /**
         * 修改默认配置
         * 默认名字为 default.realm，保存在Context.getFilesDir()目录下
         */
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("demo.realm")
                .deleteRealmIfMigrationNeeded() //如果需要整合(字段修改什么的) 就删除realm，之前的保存的数据会被删除
                .schemaVersion(BuildConfig.VERSION_CODE)
                .build();
        Realm.setDefaultConfiguration(config);

        /**
         * 多个配置，根据不同config获取不同的Realm实例
         */
/*        RealmConfiguration myConfig = new RealmConfiguration.Builder()
                .name("myrealm.realm")
                .schemaVersion(2)
                .modules(new MyCustomSchema())
                .build();

        RealmConfiguration otherConfig = new RealmConfiguration.Builder()
                .name("otherrealm.realm")
                .schemaVersion(5)
                .modules(new MyOtherSchema())
                .build();

        Realm myRealm = Realm.getInstance(myConfig);
        Realm otherRealm = Realm.getInstance(otherConfig);
 */


        /**
         * 关闭Realm,释放native memory
         * Realm实例采用计数引用，如果调用两次getInstance(),就要调用两次close()。
         */

/*        Realm realm = Realm.getDefaultInstance();
        try {
            // ... Use the Realm instance ...
        } finally {
            realm.close();
        }
        //或者 try...with..resources
        try (Realm realm = Realm.getDefaultInstance()) {
            // No need to close the Realm instance manually
        }
 */


        /**
         * Auto-Refresh自动刷新机制
         * 1、如果Realm实例创建于一个与Looper绑定的线程，比如android主线程，则会自动更新
         * 2、如果Realm实例创建于一个没有与Looper绑定的线程，则不会自动更新，除非你调用waitForChange。
         * 通过isAutoRefresh 判断是否支持auto-refresh
         */
        try (Realm realm = Realm.getDefaultInstance()) {
            boolean autoRefresh = realm.isAutoRefresh();
            System.out.println("当前线程创建的Realm支持auto-refresh：" + autoRefresh);
        }
    }


}

