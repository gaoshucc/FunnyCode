package com.funnycode.blog.service;

import com.funnycode.blog.model.QuestType;
import com.funnycode.blog.model.Question;
import java.util.List;

/**
 * @author CC
 * @date 2020-01-02 22:13
 */
public interface QuestionService {
    List<QuestType> getQuestType();

    boolean addQuestion(Question question);
}
