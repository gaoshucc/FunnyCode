package com.funnycode.blog.service.impl;

import com.funnycode.blog.dao.FeedbackDAO;
import com.funnycode.blog.model.Feedback;
import com.funnycode.blog.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CC
 * @date 2019-10-28 23:56
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackDAO feedbackDAO;

    @Override
    public boolean addFeedback(Feedback feedback) {
        return feedbackDAO.addFeedback(feedback) > 0;
    }
}
