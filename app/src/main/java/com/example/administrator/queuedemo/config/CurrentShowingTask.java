package com.example.administrator.queuedemo.config;

import com.example.administrator.queuedemo.interf.IShowTask;

/**
 * Created by QuincyJiang at 2018/10/9 .
 * Description:
 * 维护当前正在展示的任务
 */
public class CurrentShowingTask {
    private static IShowTask sCurrentShowingTask;
    public static void setCurrentShowingTask(IShowTask task){
        sCurrentShowingTask = task;
    }
    public static void removeCurrentShowingTask(){
        sCurrentShowingTask = null;
    }
    public static IShowTask getCurrentShowingTask() {
        return sCurrentShowingTask;
    }
    public static boolean getCurrentShowingStatus(){
        return sCurrentShowingTask!=null && sCurrentShowingTask.getShowStatus();
    }
}
