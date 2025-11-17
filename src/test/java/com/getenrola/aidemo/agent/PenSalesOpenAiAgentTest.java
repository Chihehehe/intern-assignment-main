package com.getenrola.aidemo.agent;

import com.getenrola.aidemo.model.AgentRequest;
import com.openai.models.ChatModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PenSalesOpenAiAgentTest {

    private final PenSalesOpenAiAgent penSalesOpenAiAgent = new PenSalesOpenAiAgent();


    @Test
    void testScript() {
        String chatModel = ChatModel.GPT_3_5_TURBO.asString();
        String previousResponseId = null;
        var agentRequest = new AgentRequest("Hi, my name is Fred", previousResponseId, chatModel);
        System.out.println("User: " + agentRequest.userText());
        var agentReply = penSalesOpenAiAgent.execute(agentRequest);
        System.out.println("Agent: " + agentReply.text());
        assertThat(agentReply.responseId()).isNotNull();
        assertThat(agentReply.text()).isNotNull();
        previousResponseId = agentReply.responseId();

        agentRequest = new AgentRequest("How much is the pen?", previousResponseId, chatModel);
        System.out.println("User: " + agentRequest.userText());
        agentReply = penSalesOpenAiAgent.execute(agentRequest);
        System.out.println("Agent: " + agentReply.text());
        assertThat(agentReply.responseId()).isNotNull();
        assertThat(agentReply.text()).isNotNull();
        previousResponseId = agentReply.responseId();

        agentRequest = new AgentRequest("Seems expensive!", previousResponseId, chatModel);
        System.out.println("User: " + agentRequest.userText());
        agentReply = penSalesOpenAiAgent.execute(agentRequest);
        System.out.println("Agent: " + agentReply.text());
        assertThat(agentReply.responseId()).isNotNull();
        assertThat(agentReply.text()).isNotNull();
        previousResponseId = agentReply.responseId();

        agentRequest = new AgentRequest("Can you email me a brochure?", previousResponseId, chatModel);
        System.out.println("User: " + agentRequest.userText());
        agentReply = penSalesOpenAiAgent.execute(agentRequest);
        System.out.println("Agent: " + agentReply.text());
        assertThat(agentReply.responseId()).isNotNull();
        assertThat(agentReply.text()).isNotNull();
        previousResponseId = agentReply.responseId();

    }

}
