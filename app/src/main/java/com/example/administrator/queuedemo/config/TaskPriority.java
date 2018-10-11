package com.example.administrator.queuedemo.config;

/**
 * Created by QuincyJiang at 2018/10/8 .
 * Description:
 * 任务优先级
 */
public enum TaskPriority {
    LOW,
    DEFAULT,
    HIGH,
    IMMEDIATELY // 任何时候插入该消息 都立刻执行 不排队
}
