package com.example.administrator.queuedemo.scheduler;

import com.example.administrator.queuedemo.interf.IShowTask;

import java.lang.ref.WeakReference;

/**
 * Created by QuincyJiang at 2018/10/9 .
 * Description:
 */
public class TaskEvent {
    private WeakReference<IShowTask> mTask;
    int mEventType;

    public IShowTask getTask() {
        return mTask.get();
    }

    public void setTask(IShowTask mTask) {
        this.mTask = new WeakReference<>(mTask);
    }

    public int getEventType() {
        return mEventType;
    }

    public void setEventType(int mEventType) {
        this.mEventType = mEventType;
    }
    public static class EventType{
        public static final int SHOW = 0X00;
        public static final int DISMISS = 0X01;
    }
}
