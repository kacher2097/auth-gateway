package com.authenhub.service.interfaces;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.ai.AiPromptRequest;

public interface AiService {
    ApiResponse<?> getAnswer(AiPromptRequest request);
}
