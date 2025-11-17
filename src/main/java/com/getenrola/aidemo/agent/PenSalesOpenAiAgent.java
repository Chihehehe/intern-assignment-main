package com.getenrola.aidemo.agent;

import com.getenrola.aidemo.model.AgentReply;
import com.getenrola.aidemo.model.AgentRequest;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseInputItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PenSalesOpenAiAgent {

    private final OpenAIClient client = OpenAIOkHttpClient.fromEnv();

    public AgentReply execute(AgentRequest agentRequest) {

        List<ResponseInputItem> inputs = new ArrayList<>();

        inputs.add(ResponseInputItem.ofMessage(ResponseInputItem.Message.builder()
                .addInputTextContent(SYSTEM_PROMPT)
                .role(ResponseInputItem.Message.Role.SYSTEM)
                .build()));
        inputs.add(ResponseInputItem.ofMessage(ResponseInputItem.Message.builder()
                .addInputTextContent(agentRequest.userText())
                .role(ResponseInputItem.Message.Role.USER)
                .build()));

        ResponseCreateParams createParams = ResponseCreateParams.builder()
                .previousResponseId(agentRequest.previousResponseId())
                .input(ResponseCreateParams.Input.ofResponse(inputs))
                .model(ChatModel.of(agentRequest.chatModel()))
                .build();

        var response = client.responses().create(createParams);

        var a = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .findFirst().orElseThrow();

        return new AgentReply(a.text(), response.id());

    }

    private final static String SYSTEM_PROMPT = """
            You are a sales agent who must sell a very fancy, one-of-a-kind, pen.
            The pen cost $5000. It has black ink. It has a titanium case, encrusted with diamonds.
            You are communicating with a customer via SMS.
            """;

}
