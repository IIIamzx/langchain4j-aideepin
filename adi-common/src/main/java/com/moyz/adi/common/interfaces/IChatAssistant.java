package com.moyz.adi.common.interfaces;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.service.*;

import java.util.List;

public interface IChatAssistant {

    /**
     * 记忆+系统角色
     */
    @SystemMessage("{{sm}}")
    TokenStream chatWith(@MemoryId String memoryId, @V("sm") String systemMessage, @UserMessage String prompt, @UserMessage List<ImageContent> images);

    /**
     * 系统角色
     */
    @SystemMessage("{{sm}}")
    TokenStream chatWithSystem(@V("sm") String systemMessage, @UserMessage String prompt, @UserMessage List<ImageContent> images);

    /**
     * 记忆
     */
    TokenStream chatWithMemory(@MemoryId String memoryId, @UserMessage String prompt, @UserMessage List<ImageContent> images);

    TokenStream chatSimple(@UserMessage String prompt, @UserMessage List<ImageContent> images);

}
