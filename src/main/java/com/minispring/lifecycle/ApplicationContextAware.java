package com.minispring.lifecycle;

import com.minispring.context.ApplicationContext;

public interface ApplicationContextAware {

    void setApplicationContext(
            ApplicationContext applicationContext
    );
}