package com.example.realm.demo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;

import androidx.annotation.Nullable;

import com.example.realm.demo.entity.Dog;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Realm
 * 1、多进程共享数据库
 * 2、修改数据，只能同步给当前进程的realm实例
 *
 * @author zhangquan
 */
public class ProcessService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Dog> memberPuppies = realm.where(Dog.class).findAll();
        int size = memberPuppies.size();
        Dog dog = null;
        if (!memberPuppies.isEmpty()) {
            dog = memberPuppies.get(0);
        }
        System.out.println("ProcessService process=" + Process.myPid() + ",size=" + size + ",dog=" + dog);

        memberPuppies.addChangeListener(new RealmChangeListener<RealmResults<Dog>>() {
            @Override
            public void onChange(RealmResults<Dog> dogs) {
                System.out.println("process进程  onChange dogs=" + dogs);
            }
        });

        /**
         * 在当前进程修改数据，只能同步给当前进程的realm实例
         */
        realm.beginTransaction();
        dog.setAge(100);
        realm.commitTransaction();

        realm.beginTransaction();
        dog.setAge(3);
        realm.commitTransaction();

        return Service.START_STICKY;
    }
}
