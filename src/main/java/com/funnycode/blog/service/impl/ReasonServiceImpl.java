package com.funnycode.blog.service.impl;

import com.funnycode.blog.dao.ReasonDAO;
import com.funnycode.blog.model.Reason;
import com.funnycode.blog.service.ReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author CC
 * @date 2019-10-04 23:29
 */
@Service
public class ReasonServiceImpl implements ReasonService {
    @Autowired
    private ReasonDAO reasonDAO;

    @Override
    public boolean addReason(Reason reason) {
        return reasonDAO.addReason(reason) > 0;
    }

    @Override
    public List<Reason> getAllReasons() {
        return reasonDAO.getAllReasons();
    }
}
