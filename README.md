# 概述
这是一个实现了单窗口排队插队demo，实现思路源自一个服务器动态下发展示视图的需求，需要将多个展示任务按照优先级进行排队，并且可以支持插队任务

特点
*  优先级不同 优先级高的优先入队
*  优先级相同 按照插入时间入队
*  任务按照入队次序依次取出执行
*  最高优先级的插队任务立刻执行，可以不参与排队，执行过程中有其他正在执行的任务，丢弃。
*  任务的开始和结束由任务执行器维护，每个任务只关注任务内容，不关注逻辑

# 思路
##  队列选择：
### PriorityBlockingQueue 优先级阻塞队列
`PriorityBlockingQueue` ， 一个支持优先级排序的无界阻塞队列。
这个队列是线程安全的，当`take()`元素出来的时候，如果队列为空，会一直阻塞，直到队列非空时跳出阻塞。
同时，如果它的`element`实现了`Comparable<>`接口，会默认按照`Comparable`返回的结果进行入队操作

`PriorityBlockingQueue`内部使用一个独占锁来控制同时只有一个线程可以进行入队和出队

`PriorityBlockingQueue`始终保证出队的元素是优先级最高的元素，并且可以定制优先级的规则，内部通过使用一个二叉树最小堆算法来维护内部数组，这个数组是可扩容的，当当前元素个数>=最大容量时候会通过算法扩容

PriorityBlockedQueue，当优先级相同时，本身是无法保证插入次序的，为了达到优先级相同，按照插入时间排序，可以重写一下它的add()方法，每当一个task入队的时候，为它设置一个sequence，可以用原子类保证多线程安全性。
#### ShowTaskQueue.add(T task)
```java
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
```
同时，每一个task的compare()也要相应处理一下：
#### BaseShowTask.compareTo(IshowTask another)
```java
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
```
### TaskScheduler 任务调度器
调度器的功能很简单，它只负责调度两类任务，一种是普通的高中低优先级任务，这类任务不需要插队，所以只按照优先级进行入队就好了。
另一类是插队任务，它们优先级最高，会中断当前正在进行的队列中任务，并且立刻执行，被中断的任务会被丢弃 
同时调度器持有任务执行器的实例，会在调度器初始化的时候，打开执行器的线程。 

调度器为单例实现，对外暴露唯一的接口 `enqueue()` ，但是具体是入队，还是插队立即执行，由调度器决定。

```java
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
```

### TaskExecutor 任务执行器
被调度器持有，它在**子线程**里轮询取队列的任务并切换到**主线程**执行，在执行时间到期后，在**主线程**结束任务，如果队列非空，则继续循环取下一个任务执行，以此往复，当队列为空，阻塞在take()方法

这个功能使用RxJava实现的 具体逻辑见下方注释

```java
public Disposable start(){
        return  Observable.create(new ObservableOnSubscribe<TaskEvent>() {
            @Override
            public void subscribe(ObservableEmitter<TaskEvent> emitter) throws InterruptedException {
                while (isRunning) {
                    IShowTask iTask;
                    // 队列为空 阻塞 非空 跳出阻塞 
                    iTask = taskQueue.take();
                    // 告知观察者 开始执行任务
                    if(iTask!=null) {
                        TaskEvent showEvent = new TaskEvent();
                        showEvent.setTask(iTask);
                        showEvent.setEventType(TaskEvent.EventType.SHOW);
                        emitter.onNext(showEvent);
                        try {
                        // 当前轮询线程睡眠任务执行时间 
                            TimeUnit.SECONDS.sleep(iTask.getDuration());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 睡眠时间结束 通知观察者结束任务
                        TaskEvent dismissEvent = new TaskEvent();
                        dismissEvent.setTask(iTask);
                        dismissEvent.setEventType(TaskEvent.EventType.DISMISS);
                        emitter.onNext(dismissEvent);
                    }
                }
            }
            // 在单线程环境里轮询取队列
            // 在主线程操作UI
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<TaskEvent>() {
            @Override
            public void accept(TaskEvent task) throws Exception {
            //当前有正在展示的插队任务 当前队列中任务丢弃
            if(CurrentShowingTask.getCurrentShowingStatus()){
                    taskQueue.remove(task.getTask());
                    return;
                }
                // 接收上游发射的消息 如果是执行任务 就开始执行 如果是结束任务 就结束当前任务
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
```

