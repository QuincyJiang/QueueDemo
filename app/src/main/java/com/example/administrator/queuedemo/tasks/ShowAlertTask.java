package com.example.administrator.queuedemo.tasks;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.administrator.queuedemo.R;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context.get());
        builder.setTitle(title);
        builder.setMessage(getSequence()+getPriority().toString());
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setCancelable(true);
        builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("不是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        mDialog = builder.create();
        mDialog.show();
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
