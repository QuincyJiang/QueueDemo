package com.example.administrator.queuedemo.scheduler;

import android.util.Log;

import com.example.administrator.queuedemo.tasks.BaseShowTask;
import com.example.administrator.queuedemo.interf.IShowTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by QuincyJiang at 2018/10/8 .
 * Description:
 * 使用优先级阻塞队列实现的一个可以插队的排队机制
 */
public class ShowTaskQueue {
    private String TAG = "ShowTaskQueue";
    private AtomicInteger mAtomicInteger = new AtomicInteger();
    private final BlockingQueue<IShowTask> mTaskQueue = new PriorityBlockingQueue<>();

    private ShowTaskQueue() {
    }
    private static class ShowTaskQueueHolder {
        private final static ShowTaskQueue INSTANCE = new ShowTaskQueue();
    }
    public static ShowTaskQueue getInstance() {
        return ShowTaskQueueHolder.INSTANCE;
    }

    /**
     * 插入时 因为每一个showTask都实现了comparable接口 所以队列会按照showTask复写的compare()方法定义的优先级次序进行插入
     * 当优先级相同时，使用AtomicInteger原子类自增 来为每一个task 设置sequence
     * <b>sequence</b>的作用是标记两个相同优先级的任务入队的次序
     * @see BaseShowTask#compareTo(IShowTask)
     */
    public <T extends IShowTask> int add(T task) {
        if (!mTaskQueue.contains(task)) {
            task.setSequence(mAtomicInteger.incrementAndGet());
            mTaskQueue.add(task);
            Log.d(TAG,"\n add task "+task.toString());
        }
        return mTaskQueue.size();
    }
    public <T extends IShowTask> void remove(T task){
        if (mTaskQueue.contains(task)) {
            Log.d(TAG,"\n"+"task has been finished. remove it from task queue" );
            mTaskQueue.remove(task);
        }
   }
   public IShowTask poll(){
        return mTaskQueue.poll();
   }
   public IShowTask take () throws InterruptedException{
        return mTaskQueue.take();
   }
   public void clear(){
        mTaskQueue.clear();
   }


   public int size(){
        return mTaskQueue.size();
   }
}
