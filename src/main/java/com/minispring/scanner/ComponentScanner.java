package com.minispring.scanner;

import com.minispring.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Semantic discovery for @Component classes.
 *
 * ClassPathScanner answers: "What classes exist?"
 * ComponentScanner answers: "Which of those classes are components?"
 */
public class ComponentScanner {

    public List<Class<?>> scan(List<Class<?>> classes) {

        List<Class<?>> components = new ArrayList<>();

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Component.class)) {
                components.add(clazz);
            }
        }

        return components;
    }
}
