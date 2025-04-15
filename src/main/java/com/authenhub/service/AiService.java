package com.authenhub.service;

import com.authenhub.bean.ApiResponse;
import com.authenhub.bean.ai.AiPromptRequest;

public interface AiService {
    ApiResponse<?> getAnswer(AiPromptRequest request);
}
