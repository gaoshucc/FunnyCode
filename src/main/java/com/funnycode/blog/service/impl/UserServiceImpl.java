package com.funnycode.blog.service.impl;

import com.funnycode.blog.configration.Constants;
import com.funnycode.blog.dao.UserDAO;
import com.funnycode.blog.model.Code;
import com.funnycode.blog.model.Ticket;
import com.funnycode.blog.model.User;
import com.funnycode.blog.model.vo.UserVO;
import com.funnycode.blog.service.SensitiveService;
import com.funnycode.blog.service.TicketService;
import com.funnycode.blog.service.UserService;
import com.funnycode.blog.util.BlogUtil;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author CC
 * @date 2019-09-10 14:28
 */
@Service
public class UserServiceImpl implements UserService {
    private final Constants constants;
    private final UserDAO userDAO;
    private final TicketService ticketService;
    private final SensitiveService sensitiveService;

    public UserServiceImpl(Constants constants, UserDAO userDAO, TicketService ticketService, SensitiveService sensitiveService) {
        this.constants = constants;
        this.userDAO = userDAO;
        this.ticketService = ticketService;
        this.sensitiveService = sensitiveService;
    }

    @Override
    public boolean userexists(String username) {
        int exists = userDAO.existsByUsername(username);

        return exists > 0;
    }

    @Override
    public boolean nicknameexists(String nickname) {
        int exists = userDAO.existsByNickname(nickname);

        return exists > 0;
    }

    @Override
    public boolean nicknameexistsExcept(String nickname, Long exceptId) {
        return userDAO.existsByNicknameAndExceptId(nickname, exceptId) > 0;
    }

    @Override
    public Long regist(String username, String nickname, String password) {
        User user = new User();
        user.setUsername(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setPassword(BlogUtil.MD5(password+user.getSalt()));
        user.setNickname(nickname);
        user.setGender(Code.FEMALE);
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setProfilePath(head);
        user.setExperience(100L);
        user.setRegtime(new Date());
        user.setStatus(Code.REGISTING);
        userDAO.add(user);

        User register = userDAO.getByUsername(user.getUsername());

        return register.getUserId();
    }

    private String addLoginTicket(long userId) {
        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24*5);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        ticketService.addTicket(ticket);
        return ticket.getTicket();
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        User user = userDAO.getByUsername(username);
        if (user == null || !BlogUtil.MD5(password+user.getSalt()).equals(user.getPassword())) {
            return map;
        }
        userDAO.updateLastLogintimeByUserId(user.getUserId(), new Date());
        String ticket = addLoginTicket(user.getUserId());
        map.put(constants.getTOKEN(), ticket);
        map.put("userId", user.getUserId());

        return map;
    }

    @Override
    public Map<String, Object> thirdLogin(Long userId) {
        Map<String, Object> map = new HashMap<>();
        userDAO.updateLastLogintimeByUserId(userId, new Date());
        String ticket = addLoginTicket(userId);
        map.put(constants.getTOKEN(), ticket);
        map.put("userId", userId);

        return map;
    }

    @Override
    public void logout(String ticket) {
        ticketService.updateStatus(ticket, 1);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDAO.getByUsername(username);
    }

    @Override
    public User getUserByUserId(long userId) {
        return userDAO.getByUserId(userId);
    }

    @Override
    public UserVO getUserVOByUserId(long userId){
        return userDAO.getUserVOByUserId(userId);
    }

    @Override
    public List<UserVO> getUserVOSByNickname(String nickname, Long userId) {
        return userDAO.findAllUserVOByNickname(nickname, userId);
    }

    @Override
    public String getNicknameById(Long userId){
        return userDAO.getNicknameById(userId);
    }

    @Override
    public String getProfilePathById(Long userId) {
        return userDAO.getProfilePathById(userId);
    }

    @Override
    public boolean updateUserInfo(String profilePath, String nickname, String email, String motto, Integer gender, Long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setGender(gender);
        user.setMotto(motto);
        user.setEmail(email);
        user.setNickname(nickname);
        user.setProfilePath(profilePath);
        return userDAO.updateByUser(user) > 0;
    }

    @Override
    public boolean updateUserExperience(long userId, int increment) {
        return userDAO.updateExperienceByUserId(userId, increment) > 0;
    }

    @Override
    public boolean updateUserSignature(long userId, String signature) {
        return userDAO.updateSignatureByUserId(userId, sensitiveService.filter(signature)) > 0;
    }
}
