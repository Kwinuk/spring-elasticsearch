package com.xcurenet.arkime.service;

import com.xcurenet.arkime.vo.ResultVO;
import com.xcurenet.arkime.vo.file.FileVO;
import com.xcurenet.arkime.vo.session.SessionVO;

import java.util.List;
import java.util.Map;

public interface PacketService {

    ResultVO<FileVO> search(Map<String, Object> expression, String[] includeFields,
                               String[] excludeFields, List<Map<String, Object>> sortInfo,
                               long startTime, long stopTime, String bounding, int offset, int limit);
}
