package com.minispring.scanner;

import com.minispring.annotation.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Low-level classpath discovery.
 *
 * This scanner does not decide which classes are beans. It simply walks the
 * package directory, loads .class files, and returns the discovered classes.
 * Higher-level scanners such as ComponentScanner and ConfigurationScanner
 * perform semantic filtering afterwards.
 */
public class ClassPathScanner {

    public List<Class<?>> scan(String packageName)
            throws Exception {

        List<Class<?>> classes = new ArrayList<>();

        String path = packageName.replace('.', '/');

        ClassLoader classLoader =
                Thread.currentThread().getContextClassLoader();

        URL resource = classLoader.getResource(path);

        if (resource == null) {
            return classes;
        }

        File directory = new File(resource.toURI());

        scanDirectory(directory, packageName, classes);
        return classes;
    }

    private void scanDirectory(
            File directory,
            String packageName,
            List<Class<?>> classes)
            throws Exception {

        File[] files = directory.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(
                        file,
                        packageName + "." + file.getName(),
                        classes
                );
                continue;
            }

            if (!file.getName().endsWith(".class")) {
                continue;
            }

            String className = packageName
                    + "."
                    + file.getName().replace(".class", "");

            classes.add(Class.forName(className));
        }
    }
}
