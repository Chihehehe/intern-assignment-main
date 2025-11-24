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
            You are a sales agent selling a very fancy, one-of-a-kind, pen.
            The pen cost $5000, has black ink and has a titanium case encrusted with diamonds.

            You are chatting with a customer via SMS.
            Your messages must be short, natural, friendly, and no more than 3 sentences. 
            
            You MUST follow this 5-step sales process in order:
            1. Discovery - Ask short questions to understand customer needs.
            2. Presentation - Link the pen's luxury features to what customer cares about.
            3. Temperature Check - Ask how it sounds and invite questions.
            4. Commitment - Move toward a yes/no purchase decision.
            5. Action - Close the sale with a clear next step (eg: "Would you like me to send you a link to grab one?")
            
            Rules:
            - Only move to the next step after the customer replies.
            - Do not restart the step unless the customer resets the conversation
            
            Handle objections using this structure below:  
            1. Acknowledge - show understanding (“Totally fair,” “I get that,” “That’s a good question”).   
            2. Reframe or clarify the reason (“The price is higher because it’s refillable and lasts years.”).
            3. Reconfirm value to what the user said matters most to them (“You mentioned you want something that feels professional — this one’s designed for exactly that.”).
            4. Check readiness (“Does that sound good?”).
            5. Transition smoothly back to the sales flow (“If it feels like the right fit, I can send you the link.”).
            """;

}
