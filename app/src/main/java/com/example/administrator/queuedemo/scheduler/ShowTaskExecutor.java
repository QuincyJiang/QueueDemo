package com.example.administrator.queuedemo.scheduler;

import android.util.Log;

import com.example.administrator.queuedemo.config.CurrentShowingTask;
import com.example.administrator.queuedemo.interf.IShowTask;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by QuincyJiang at 2018/10/9 .
 * Description:
 * 任务排队执行器 使用RxJava 在单线程中循环取PriorityBlockingQueue队列的数据 在主线程操作ui
 * 队列为空 阻塞在 taskQueue.take()
 * 队列一旦非空 立刻跳出阻塞 取出task并展示  令线程睡眠duration时间 使得view可以完整展示duration时长
 * 当前如果有插队任务（最高优先级任务）, 将当前排队任务丢弃
 */
public class ShowTaskExecutor {
    private final String TAG = "ShowTaskExecutor";
    private ShowTaskQueue taskQueue;
    private boolean isRunning = true;

    public ShowTaskExecutor(ShowTaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }
    public Disposable start(){
        return  Observable.create(new ObservableOnSubscribe<TaskEvent>() {
            @Override
            public void subscribe(ObservableEmitter<TaskEvent> emitter) throws InterruptedException {
                while (isRunning) {
                    IShowTask iTask;
                    iTask = taskQueue.take();
                    if(iTask!=null) {
                        TaskEvent showEvent = new TaskEvent();
                        showEvent.setTask(iTask);
                        showEvent.setEventType(TaskEvent.EventType.SHOW);
                        emitter.onNext(showEvent);
                        try {
                            iTask.lock();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        TaskEvent dismissEvent = new TaskEvent();
                        dismissEvent.setTask(iTask);
                        dismissEvent.setEventType(TaskEvent.EventType.DISMISS);
                        emitter.onNext(dismissEvent);
                    }
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<TaskEvent>() {
            @Override
            public void accept(TaskEvent task) throws Exception {
                if(CurrentShowingTask.getCurrentShowingStatus()){
                    taskQueue.remove(task.getTask());
                    return;
                }
                if(task.getEventType() == TaskEvent.EventType.DISMISS)
                    task.getTask().dismiss();
                if(task.getEventType() == TaskEvent.EventType.SHOW)
                    task.getTask().show();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG,throwable.getMessage());
            }
        });
    }
    public void pause(){
        isRunning = false;
    }
    public void resetExecutor(){
        isRunning = true;
        taskQueue.clear();
    }
}
