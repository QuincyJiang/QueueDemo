package com.example.administrator.queuedemo;

import android.content.Intent;
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
    private ShowTaskQueue taskQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt1 = findViewById(R.id.bt_1);
        bt2 = findViewById(R.id.bt_2);
        bt2.setOnClickListener(this);
        taskQueue = ShowTaskQueue.getInstance();
        bt1.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_1:
                addTask();
                break;
            case R.id.bt_2:
                Intent intent = new Intent(this,Main2Activity.class);
                startActivity(intent);
        }
    }

    private void addTask(){
         ShowAlertTask task1 = new ShowAlertTask(taskQueue,this,"low","23333333",1);
//        task1.setPriority(TaskPriority.LOW);
        ShowAlertTask task2 = new ShowAlertTask(taskQueue,this,"default","23333333",1);
//        task2.setPriority(TaskPriority.DEFAULT);
        ShowAlertTask task3 = new ShowAlertTask(taskQueue,this,"high","23333333",1);
//        task3.setPriority(TaskPriority.HIGH);
        ShowAlertTask task4 = new ShowAlertTask(taskQueue,this,"immediately","23333333",1);
        ShowAlertTask task5 = new ShowAlertTask(taskQueue,this,"default","23333333",1);
        ShowAlertTask task6 = new ShowAlertTask(taskQueue,this,"default","23333333",1);
        ShowAlertTask task7 = new ShowAlertTask(taskQueue,this,"default","23333333",1);
        ShowAlertTask task8 = new ShowAlertTask(taskQueue,this,"default","23333333",1);
        ShowAlertTask task9 = new ShowAlertTask(taskQueue,this,"default","23333333",1);
        ShowAlertTask task10 = new ShowAlertTask(taskQueue,this,"default","23333333",1);

        task4.setPriority(TaskPriority.IMMEDIATELY);
        task10.enqueue();
        task9.enqueue();
        task8.enqueue();
        task7.enqueue();
        task6.enqueue();
        task5.enqueue();
        task4.enqueue();
        task3.enqueue();
        task2.enqueue();
        task1.enqueue();
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
