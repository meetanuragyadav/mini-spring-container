package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

/**
 * Simple service used to demonstrate constructor injection of another bean.
 */
@Component
public class UserService {

    private final Database database;

    @Inject
    public UserService(Database database) {
        System.out.println("UserService constructor");
        this.database = database;
    }
}
