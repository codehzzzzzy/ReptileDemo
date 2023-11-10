package com.hzzzzzy.constants;

/**
 * @author hzzzzzy
 * @date 2023/11/3
 * @description redis key 前缀
 */
public interface RedisKeyPrefix {

    /**
     * 一位老师全部课程的缓存前缀
     */
    String TEACHER_ALL_COURSE_CACHE_PREFIX = "course:all:";
}