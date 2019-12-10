package com.funnycode.blog.service;

import com.funnycode.blog.model.Reason;

import java.util.List;

/**
 * @author CC
 * @date 2019-10-04 23:29
 */
public interface ReasonService {
    boolean addReason(Reason reason);

    List<Reason> getAllReasons();
}
