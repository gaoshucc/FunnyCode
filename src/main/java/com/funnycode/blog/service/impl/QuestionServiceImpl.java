package com.funnycode.blog.service.impl;

import com.funnycode.blog.dao.QuestionDAO;
import com.funnycode.blog.model.QuestType;
import com.funnycode.blog.model.Question;
import com.funnycode.blog.service.QuestionService;
import com.funnycode.blog.service.SensitiveService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author CC
 * @date 2020-01-02 22:17
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionDAO questionDAO;
    private final SensitiveService sensitiveService;

    public QuestionServiceImpl(QuestionDAO questionDAO, SensitiveService sensitiveService) {
        this.questionDAO = questionDAO;
        this.sensitiveService = sensitiveService;
    }

    @Override
    public List<QuestType> getQuestType() {
        return questionDAO.findAllType();
    }

    @Override
    public boolean addQuestion(Question question) {
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        return questionDAO.add(question) > 0;
    }
}
