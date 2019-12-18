package com.funnycode.blog.service.impl;

import com.funnycode.blog.dao.AccountBindingDAO;
import com.funnycode.blog.model.AccountBinding;
import com.funnycode.blog.service.AccountBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CC
 * @date 2019-10-26 19:31
 */
@Service
public class AccountBindingServiceImpl implements AccountBindingService {

    @Autowired
    private AccountBindingDAO accountBindingDAO;

    @Override
    public boolean addAccountBinding(AccountBinding binding) {
        return accountBindingDAO.add(binding) > 0;
    }

    @Override
    public boolean removeAccountBinding(Long userId, Integer type) {
        return accountBindingDAO.removeByUserIdAndType(userId, type) > 0;
    }

    @Override
    public AccountBinding getAccountBinding(String thirdId, Integer type) {
        return accountBindingDAO.getByThirdIdAndType(thirdId, type);
    }

    @Override
    public boolean getAccountBindingState(Long userId, Integer type) {
        return accountBindingDAO.existsByUserIdAndType(userId, type) > 0;
    }
}
