package com.example.administrator.queuedemo.tasks;

import android.content.Context;

import com.example.administrator.queuedemo.scheduler.ShowTaskQueue;

import java.lang.ref.WeakReference;

/**
 * Created by QuincyJiang at 2018/10/8 .
 * Description:
 * H5 页面展示任务
 */
public class ShowH5Task extends BaseShowTask {

    public ShowH5Task(Context context, ShowTaskQueue taskQueue, int duration, String id, String json) {
        this.context = new WeakReference<>(context);
        this.taskQueue = new WeakReference<>(taskQueue);
        this.id = id;
        this.json = json;
    }

    @Override
    public void enqueue() {
        super.enqueue();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
