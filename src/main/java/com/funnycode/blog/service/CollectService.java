package com.funnycode.blog.service;

import java.util.List;
import java.util.Map;

/**
 * @author CC
 * @date 2019-10-02 16:54
 */
public interface CollectService {

    int collect(Long userId, int entityType, long noteId);

    int getCollectStatus(Long userId, int entityType, long noteId);

    long getCollectCount(Long userId, int entityType);

    Map<Long, Double> getCollectList(Long userId, int entityType, Integer offset, Integer limit);

    List<Long> getCollectBrief(Long userId, int entityType, Integer offset, Integer limit);
}
