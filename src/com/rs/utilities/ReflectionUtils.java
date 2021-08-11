package com.rs.utilities;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

    @SuppressWarnings("unchecked")
    public static <T> List<T> getSubclassesOf(Class<T> clazz) {
        val name = clazz.getName();

        val classes = new ArrayList<T>();
        val result = new ClassGraph().enableClassInfo().blacklistClasses(name).scan();
        val subclasses = result.getSubclasses(name);

        for (ClassInfo subclass : subclasses) {

            try {
                val subClazz = result.loadClass(subclass.getName(), true).newInstance();
                classes.add((T) subClazz);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return classes;
    }

}
