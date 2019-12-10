package com.funnycode.blog.async;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CC
 * @date 2019-09-23 00:01
 */
public class EventModel {
    private EventType type;
    private long actorId;
    private int entityType;
    private long entityId;
    private long entityOwnerId;
    private Map<String, String> exts = new HashMap<String, String>();

    public EventModel() {}

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public EventModel(EventType type) {
        this.type = type;
    }

    public String getExt(String key) {
        return exts.get(key);
    }


    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public long getActorId() {
        return actorId;
    }

    public EventModel setActorId(long actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public long getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(long entityId) {
        this.entityId = entityId;
        return this;
    }

    public long getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(long entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
