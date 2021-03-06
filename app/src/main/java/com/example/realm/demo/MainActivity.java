package com.example.realm.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;

import com.example.realm.demo.adapter.RecyclerViewExampleActivity;
import com.example.realm.demo.dao.PersonDao;
import com.example.realm.demo.entity.Dog;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    PersonDao personDao=new PersonDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_adapter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RecyclerViewExampleActivity.class));
            }
        });
        findViewById(R.id.btn_deleteall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDB();
            }
        });
        findViewById(R.id.btn_process).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, ProcessService.class));
            }
        });

        test();
    }

    private void test() {
        try {
            personDao.test1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除数据库
     */
    private void deleteDB() {
        File filesDir = getFilesDir();
        try {

            File[] files = filesDir.listFiles();
            for (File file : files) {
                if (file.getName().contains(".realm")) {
                    if (file.isDirectory()) {
                        File[] subFiles = file.listFiles();
                        for (File subFile : subFiles) {
                            subFile.delete();
                        }
                    }
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Process.killProcess(Process.myPid());
    }

}