package com.funnycode.blog.service;

import com.funnycode.blog.model.AccountBinding;

/**
 * @author CC
 * @date 2019-10-26 19:30
 */
public interface AccountBindingService {
    boolean addAccountBinding(AccountBinding binding);

    boolean removeAccountBinding(Long userId, Integer type);

    AccountBinding getAccountBinding(String thirdId, Integer type);

    boolean getAccountBindingState(Long userId, Integer type);
}
