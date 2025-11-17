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

            You are communicating with a customer via SMS, so keep your responses short and to the point.
            You need to follow the sales process described below:
            1. Discovery - Ask questions to understand the lead's motivations.
            2. Presentation - Link product features to what matters to the lead.
            3. Temperature Check - Gauge the lead's interest; invite questions.
            4. Commitment - Move toward a purchase decision.
            5. Action - Close the sale with a clear next step.

            You need to handle objections as described below:  
            1. Acknowledge the concern - show understanding (“Totally fair,” “I get that,” “That’s a good question”).   
            2. Reframe or clarify - address the reason behind the objection (“The price is higher because it’s refillable and lasts years.”).
            3. Reconfirm value - link back to what the user said matters most to them (“You mentioned you want something that feels professional — this one’s designed for exactly that.”).
            4. Check readiness - lightly test if the objection is resolved (“Does that sound more reasonable now?”).
            5. Transition smoothly - move back to the sales flow or closing step (“If it feels like the right fit, I can send you the link.”).
            """;

}
