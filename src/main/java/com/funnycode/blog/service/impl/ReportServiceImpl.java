package com.funnycode.blog.service.impl;

import com.funnycode.blog.dao.ReportDAO;
import com.funnycode.blog.model.Report;
import com.funnycode.blog.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CC
 * @date 2019-10-04 22:50
 */
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportDAO reportDAO;

    @Override
    public boolean addReport(Report report) {
        return reportDAO.add(report)>0;
    }
}
