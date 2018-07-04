package com.sun.collections;

import java.util.SortedMap;
import java.util.TreeMap;

public class TreeMapTest {
    public static void main(String[] args) {
        TreeMap<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("a", 1);
        treeMap.put("c", 2);
        treeMap.put("b", 3);
        treeMap.put("d", 4);
        treeMap.put("e", 5);
        treeMap.put("f", 6);

        SortedMap<String, Integer> tail = treeMap.tailMap("d");
        //System.out.println(tail.entrySet().size());
        for(Integer value :treeMap.values()){
            System.out.println(value);
        }

        SortedMap<String, Integer> head = treeMap.headMap("b");
        //System.out.println(head.entrySet().size());

        SortedMap<String, Integer> sub = treeMap.subMap("b", "d");
        //System.out.println(sub.entrySet().size());
    }
}