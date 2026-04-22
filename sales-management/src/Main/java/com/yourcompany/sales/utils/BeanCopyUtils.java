package com.yourcompany.sales.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Bean 拷贝工具类，基于 Spring BeanUtils 增强
 */
public class BeanCopyUtils {

    /**
     * 单个对象拷贝
     *
     * @param source 源对象
     * @param targetSupplier 目标对象构造器（例如: UserDto::new）
     * @return 目标对象
     */
    public static <S, T> T copyBean(S source, Supplier<T> targetSupplier) {
        if (source == null) {
            return null;
        }
        T target = targetSupplier.get();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 单个对象拷贝，指定目标类型
     *
     * @param source 源对象
     * @param targetClass 目标类
     * @return 目标对象
     */
    public static <S, T> T copyBean(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("创建目标对象失败", e);
        }
    }

    /**
     * 列表拷贝
     *
     * @param sourceList 源列表
     * @param targetSupplier 目标对象构造器
     * @return 目标列表
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Supplier<T> targetSupplier) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            targetList.add(copyBean(source, targetSupplier));
        }
        return targetList;
    }

    /**
     * 列表拷贝，指定目标类型
     *
     * @param sourceList 源列表
     * @param targetClass 目标类
     * @return 目标列表
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        try {
            for (S source : sourceList) {
                T target = targetClass.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(source, target);
                targetList.add(target);
            }
        } catch (Exception e) {
            throw new RuntimeException("创建目标对象失败", e);
        }
        return targetList;
    }
}