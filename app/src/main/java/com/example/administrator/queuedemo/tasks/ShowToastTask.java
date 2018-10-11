package com.example.administrator.queuedemo.tasks;

import android.content.Context;
import android.widget.Toast;

import com.example.administrator.queuedemo.scheduler.ShowTaskQueue;

import java.lang.ref.WeakReference;

/**
 * Created by QuincyJiang at 2018/10/8 .
 * Description:
 */
public class ShowToastTask extends BaseShowTask {


    public ShowToastTask(ShowTaskQueue taskQueue, Context context, String content) {
        this.context = new WeakReference<>(context);
        this.taskQueue = new WeakReference<>(taskQueue);
        this.content = content;
    }


    @Override
    public void enqueue() {
        super.enqueue();
    }

    @Override
    public void show() {
        super.show();
        Toast.makeText(context.get(),content,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
