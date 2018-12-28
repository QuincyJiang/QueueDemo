package com.example.administrator.queuedemo.tasks;

import android.content.Context;
import android.util.Log;
import com.example.administrator.queuedemo.config.CurrentShowingTask;
import com.example.administrator.queuedemo.config.TaskPriority;
import com.example.administrator.queuedemo.interf.IShowTask;
import com.example.administrator.queuedemo.scheduler.ShowTaskQueue;
import com.example.administrator.queuedemo.scheduler.TaskScheduler;
import java.lang.ref.WeakReference;

/**
 * Created by QuincyJiang at 2018/10/8 .
 * Description:
 * 任务基类 实现了默认的入队排队优先级
 */
public abstract class BaseShowTask implements IShowTask {
    //默认优先级
    private TaskPriority mTaskPriority = TaskPriority.DEFAULT;
    // 入队次序
    private int mSequence;
    protected WeakReference<Context> context;
    // 标志是否仍在展示
    private Boolean mShowStatus = false;
    protected WeakReference<ShowTaskQueue> taskQueue;
    protected int duration = 1;
    protected String id = "";
    protected String json = "";
    protected String content = "";
    protected String title =" ";
    protected String confirmButton = "Confirm";
    protected String cancelButton = "Cancel";
    private final String TAG = getClass().getSimpleName();

    @Override
    public BaseShowTask setPriority(TaskPriority mTaskPriority) {
        this.mTaskPriority = mTaskPriority;
        return this;
    }

    @Override
    public TaskPriority getPriority() {
        return mTaskPriority;
    }

    @Override
    public void setSequence(int mSequence) {
        this.mSequence = mSequence;
    }

    @Override
    public int getSequence() {
        return mSequence;
    }

    /**
     * 优先级的标准如下：
     * TaskPriority.LOW < TaskPriority.DEFAULT < TaskPriority.HIGH
     * 当优先级相同 按照插入次序排队
     * TaskPriority.IMMEDIATELY 优先级最高 但是不参与排队  有这样级别的showTask就直接显示 不做入队 出队的操作
     */
    @Override
    public int compareTo(IShowTask another) {
        final TaskPriority me = this.getPriority();
        final TaskPriority it = another.getPriority();
        return me == it ?  this.getSequence() - another.getSequence() :
                it.ordinal() - me.ordinal();
    }

    @Override
    public void enqueue(){
        TaskScheduler.getInstance().enqueue(this);
    }

    @Override
    public  void show(){
        mShowStatus = true;
        if(getPriority() == TaskPriority.IMMEDIATELY)
        CurrentShowingTask.setCurrentShowingTask(this);
    }

    @Override
    public void dismiss() {
        this.mShowStatus = false;
        this.taskQueue.get().remove(this);
        if(getPriority() == TaskPriority.IMMEDIATELY)
        CurrentShowingTask.removeCurrentShowingTask();
        Log.d(TAG,taskQueue.get().size()+"");
    }

    @Override
    public String toString() {
        return "task name : "+ getClass().getSimpleName()+" sequence : "+ mSequence +" TaskPriority : "+mTaskPriority;
    }

    @Override
    public boolean getShowStatus() {
        return mShowStatus;
    }

    @Override
    public int getDuration() {
        return duration;
    }
}
