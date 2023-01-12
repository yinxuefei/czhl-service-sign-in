package com.gdczhl.saas.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdczhl.saas.bo.PageBo;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class PageBoUtil {
    public static <T> PageBo<T> createPageBo(Page<T> page) {
        PageBo<T> of = PageBo.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), page.getRecords());
        return of;
    }

    public static <T, S> PageBo<T> createPageBo(Page<S> page, Supplier<T> target) {
        return createPageBo(page, target, null);
    }

    public static <T, S> PageBo<T> createPageBo(Page<S> page, Supplier<T> target, CzBeanUtilsCallBack<S, T> callBack) {
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return createPageBo(page.getCurrent(), page.getSize());
        }
        List<T> list = CzBeanUtils.copyListProperties(page.getRecords(), target, callBack);
        PageBo<T> of = PageBo.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), list);
        return of;
    }


    public static PageBo createPageBo(long current, long size) {
        PageBo of = PageBo.of(current, size, 0, 0, Collections.emptyList());
        return of;
    }
}
