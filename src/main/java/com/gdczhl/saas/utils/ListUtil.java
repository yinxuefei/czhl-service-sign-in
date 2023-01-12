package com.gdczhl.saas.utils;

import java.util.*;

public class ListUtil {

    /** b中包含的,a中不包含的结果;
     *  a 与 b 的差集
     * @param listA
     * @param listB
     * @return 差集
     */
    public static <T> List<T> difference(List<T> listA,List<T> listB){
        ArrayList<T> result = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        HashSet<T> hashSet = new HashSet<>();
        hashSet.addAll(listA);
        listA.clear();

        for (int i = 0; i < listB.size(); i++) {
            if (!hashSet.contains(listB.get(i))) {
                result.add(listB.get(i));
            }
        }
        long endTime = System.currentTimeMillis();

        System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
        return result;
    }
}
