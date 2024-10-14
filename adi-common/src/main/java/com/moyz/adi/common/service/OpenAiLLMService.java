package com.moyz.adi.common.service;

import com.moyz.adi.common.cosntant.AdiConstant;
import com.moyz.adi.common.entity.AiModel;
import com.moyz.adi.common.enums.ErrorEnum;
import com.moyz.adi.common.exception.BaseException;
import com.moyz.adi.common.interfaces.AbstractLLMService;
import com.moyz.adi.common.util.JsonUtil;
import com.moyz.adi.common.vo.LLMBuilderProperties;
import com.moyz.adi.common.vo.OpenAiSetting;
import com.theokanning.openai.OpenAiError;
import dev.ai4j.openai4j.OpenAiHttpException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.util.Strings;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * OpenAi LLM service
 */
@Slf4j
@Accessors(chain = true)
public class OpenAiLLMService extends AbstractLLMService<OpenAiSetting> {

    public OpenAiLLMService(AiModel model) {
        super(model, AdiConstant.SysConfigKey.OPENAI_SETTING, OpenAiSetting.class);
    }

    @Override
    public boolean isEnabled() {
        return StringUtils.isNotBlank(modelPlatformSetting.getSecretKey()) && aiModel.getIsEnable();
    }

    @Override
    protected ChatLanguageModel doBuildChatLLM(LLMBuilderProperties properties) {
        if (StringUtils.isBlank(modelPlatformSetting.getSecretKey())) {
            throw new BaseException(ErrorEnum.B_LLM_SECRET_KEY_NOT_SET);
        }
        double temperature = 0.7;
        if (null != properties && properties.getTemperature() > 0 && properties.getTemperature() <= 1) {
            temperature = properties.getTemperature();
        }
        OpenAiChatModel.OpenAiChatModelBuilder builder = OpenAiChatModel.builder()
                .temperature(temperature)
                .apiKey(modelPlatformSetting.getSecretKey());
        if (null != proxy) {
            builder.proxy(proxy);
        }
        return builder.build();
    }

    @Override
    public StreamingChatLanguageModel buildStreamingChatLLM(LLMBuilderProperties properties) {
        if (StringUtils.isBlank(modelPlatformSetting.getSecretKey())) {
            throw new BaseException(ErrorEnum.B_LLM_SECRET_KEY_NOT_SET);
        }
        double temperature = 0.7;
        if (null != properties && properties.getTemperature() > 0 && properties.getTemperature() <= 1) {
            temperature = properties.getTemperature();
        }
        OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder builder = OpenAiStreamingChatModel
                .builder()
                .modelName(aiModel.getName())
                .temperature(temperature)
                .apiKey(modelPlatformSetting.getSecretKey())
                .timeout(Duration.of(60, ChronoUnit.SECONDS));
        if (null != proxy) {
            builder.proxy(proxy);
        }
        return builder.build();
    }

    @Override
    protected String parseError(Object error) {
        if (error instanceof OpenAiHttpException) {
            OpenAiHttpException openAiHttpException = (OpenAiHttpException) error;
            OpenAiError openAiError = JsonUtil.fromJson(openAiHttpException.getMessage(), OpenAiError.class);
            return openAiError.getError().getMessage();
        }
        return Strings.EMPTY;
    }

}
