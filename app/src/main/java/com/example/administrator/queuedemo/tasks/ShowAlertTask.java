package com.example.administrator.queuedemo.tasks;


import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.administrator.queuedemo.scheduler.ShowTaskQueue;

import java.lang.ref.WeakReference;

/**
 * Created by QuincyJiang at 2018/10/8 .
 * Description:
 * AlertDialog 展示任务
 */
public class ShowAlertTask extends BaseShowTask {
    private AlertDialog mDialog;

    public ShowAlertTask(ShowTaskQueue taskQueue, Context context, String title, String content, int duration) {
        this.context = new WeakReference<>(context);
        this.taskQueue = new WeakReference<>(taskQueue);
        this.content = content;
        this.title = title;
        this.content = content;
        this.duration = duration;
    }

    @Override
    public void enqueue() {
        super.enqueue();
    }

    @Override
    public void show() {
        super.show();
        Log.d("ShowTask","Task is showing..."+toString());
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(mDialog!=null && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    @Override
    public boolean getShowStatus() {
        return mDialog!=null && mDialog.isShowing();
    }
}
