package com.funnycode.blog.service.impl;

import com.funnycode.blog.service.CollectService;
import com.funnycode.blog.util.JedisAdapter;
import com.funnycode.blog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author CC
 * @date 2019-10-02 16:54
 */
@Service
public class CollectServiceImpl implements CollectService {

    @Autowired
    private JedisAdapter jedisAdapter;

    @Override
    public int collect(Long userId, int entityType, long noteId) {
        String collectKey = RedisKeyUtil.getCollectKey(userId, entityType);
        Long rank = jedisAdapter.zrank(collectKey, String.valueOf(noteId));
        if(rank == null){
            jedisAdapter.zadd(collectKey, System.currentTimeMillis(), String.valueOf(noteId));
            return 1;
        }else{
            jedisAdapter.zrem(collectKey, String.valueOf(noteId));
            return -1;
        }
    }

    @Override
    public int getCollectStatus(Long userId, int entityType, long noteId) {
        String collectKey = RedisKeyUtil.getCollectKey(userId, entityType);
        Long rank = jedisAdapter.zrank(collectKey, String.valueOf(noteId));
        if(rank != null){
            return 1;
        }

        return -1;
    }

    @Override
    public long getCollectCount(Long userId, int entityType) {
        String key = RedisKeyUtil.getCollectKey(userId, entityType);
        return jedisAdapter.zcard(key);
    }

    @Override
    public Map<Long, Double> getCollectList(Long userId, int entityType, Integer offset, Integer limit) {
        String key = RedisKeyUtil.getCollectKey(userId, entityType);
        List<Long> ids = getIdsFromSet(jedisAdapter.zrevrange(key, offset, offset+limit-1));
        Map<Long, Double> map = new HashMap<>();
        for(Long id: ids){
            map.put(id, jedisAdapter.zscore(key, String.valueOf(id)));
        }
        return map;
    }

    @Override
    public List<Long> getCollectBrief(Long userId, int entityType, Integer offset, Integer limit) {
        String key = RedisKeyUtil.getCollectKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(key, offset, offset+limit-1));
    }

    private List<Long> getIdsFromSet(Set<String> idset) {
        List<Long> ids = new ArrayList<>();
        for (String str : idset) {
            ids.add(Long.parseLong(str));
        }
        return ids;
    }
}
