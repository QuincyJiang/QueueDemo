package com.example.administrator.queuedemo.scheduler;

import android.os.Handler;
import android.os.Message;

import com.example.administrator.queuedemo.config.TaskPriority;
import com.example.administrator.queuedemo.interf.IShowTask;

/**
 * Created by QuincyJiang at 2018/10/8 .
 * Description:
 * 任务调度器 根据任务类型来决定是否插队显示 还是入队
 */
public class TaskScheduler {
    private final String TAG = "TaskScheduler";
    private ShowTaskQueue mTaskQueue = ShowTaskQueue.getInstance();
    private Handler mHandler = new MyHandler();
    private ShowTaskExecutor mExecutor;
    private static class ShowDurationHolder {
        private final static TaskScheduler INSTANCE = new TaskScheduler();
    }
    private TaskScheduler(){
        initExecutor();
    }
    private void initExecutor(){
        mExecutor = new ShowTaskExecutor(mTaskQueue);
        mExecutor.start();
    }
    public static TaskScheduler getInstance() {
        return ShowDurationHolder.INSTANCE;
    }
    public void enqueue(IShowTask task){
        int duration = task.getDuration();
        // 入队时 有无正在显示的task
        // 最高优先级任务 不插入队列 直接立刻播放
        if(task.getPriority() == TaskPriority.IMMEDIATELY){
            task.show();
            Message message = new Message();
            message.obj = task;
            mHandler.sendMessageDelayed(message,duration*1000);
            return;
        }
        // 其他任务 按照优先级插入队列 依次播放
        mTaskQueue.add(task);
    }
    public void resetExecutor(){
        mExecutor.resetExecutor();
    }

    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.obj instanceof IShowTask){
                IShowTask task =   ((IShowTask)msg.obj);
                task.dismiss();
            }
        }
    }
}
