package com.example.administrator.queuedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.administrator.queuedemo.config.TaskPriority;
import com.example.administrator.queuedemo.scheduler.ShowTaskQueue;
import com.example.administrator.queuedemo.scheduler.TaskScheduler;
import com.example.administrator.queuedemo.tasks.ShowAlertTask;
import com.squareup.leakcanary.RefWatcher;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button bt1;
    Button bt2;
    Button bt3;
    Button bt4;
    Button bt5;
    private ShowTaskQueue taskQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt1 = findViewById(R.id.bt_1);
        bt2 = findViewById(R.id.bt_2);
        bt3 = findViewById(R.id.bt_3);
        bt4 = findViewById(R.id.bt_4);
        bt5 = findViewById(R.id.bt_5);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
        bt5.setOnClickListener(this);
        taskQueue = ShowTaskQueue.getInstance();
        bt1.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_1:
                addImmediately();
                break;
            case R.id.bt_2:
                addHigh();
                break;
            case R.id.bt_3:
                addmiddle();
                break;
            case R.id.bt_4:
                addlow();
                break;
            case R.id.bt_5:
                task2.unlock();
                break;
                default:
        }
    }
    ShowAlertTask task2;
    private void addImmediately(){
        new ShowAlertTask(taskQueue,this,"immediately","23333333",1).setPriority(TaskPriority.IMMEDIATELY).enqueue();
    }
    private void addHigh(){
        task2 = (ShowAlertTask) (new ShowAlertTask(taskQueue,this,"high","23333333",1).setPriority(TaskPriority.HIGH));
        task2.enqueue();
    }
    private void addmiddle(){
        new ShowAlertTask(taskQueue,this,"middle","23333333",1).setPriority(TaskPriority.DEFAULT).enqueue();
    }
    private void addlow(){
        new ShowAlertTask(taskQueue,this,"low","23333333",1).setPriority(TaskPriority.LOW).enqueue();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TaskScheduler.getInstance().resetExecutor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RefWatcher refWatcher = MyApplication.getRefWatcher(this);//1
        refWatcher.watch(this);
    }
}
