package com.group2.model;

import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

//Class to fetch object size
class ObjectSizeFetcher {
    //private static Instrumentation instrumentation= new ;
    //ObjectSizeCalculator

    public static Integer getObjectSize(Object o) {
        return ((Long)ObjectSizeCalculator.getObjectSize(o)).intValue();
    }
}
