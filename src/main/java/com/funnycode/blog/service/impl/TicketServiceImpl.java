package com.funnycode.blog.service.impl;

import com.funnycode.blog.dao.TicketDAO;
import com.funnycode.blog.model.Ticket;
import com.funnycode.blog.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CC
 * @date 2019-09-19 20:34
 */
@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketDAO ticketDAO;

    @Override
    public int addTicket(Ticket ticket) {
        return ticketDAO.add(ticket);
    }

    @Override
    public Ticket selectByTicket(String ticket) {
        return ticketDAO.getByToken(ticket);
    }

    @Override
    public void updateStatus(String ticket, int status) {
        ticketDAO.updateStatusByToken(ticket, status);
    }
}
