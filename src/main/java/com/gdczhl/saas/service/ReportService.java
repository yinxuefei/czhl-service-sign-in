package com.gdczhl.saas.service;

import java.time.LocalDateTime;

/**
 * WS主动上报数据
 */
public interface ReportService {

    void handleFace(LocalDateTime now, String sn, String pic64);

}
