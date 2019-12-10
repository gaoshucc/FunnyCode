package com.funnycode.blog.service;

import com.funnycode.blog.model.Ticket;

/**
 * @author CC
 * @date 2019-09-19 20:31
 */
public interface TicketService {
    /**
     * 生成token
     * @param ticket token
     */
    int addTicket(Ticket ticket);

    Ticket selectByTicket(String ticket);

    void updateStatus(String ticket, int status);
}
