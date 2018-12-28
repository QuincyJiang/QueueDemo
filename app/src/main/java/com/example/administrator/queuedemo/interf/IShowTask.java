package com.example.administrator.queuedemo.interf;

import com.example.administrator.queuedemo.config.TaskPriority;

/**
 * Created by QuincyJiang at 2018/10/8 .
 * Description:
 * 服务器动态下发的动画展示任务
 */
public interface IShowTask extends Comparable<IShowTask>{
    void enqueue();
    void show();
    void dismiss();
    IShowTask setPriority(TaskPriority mTaskPriority);
    TaskPriority getPriority();
    // 当优先级相同 按照插入顺序 先入先出 该方法用来标记插入顺序
    void setSequence(int mSequence);
    int getSequence();
    String toString();
    boolean getShowStatus();
    int getDuration();
    void lock() throws Exception;
    void unlock();
}
