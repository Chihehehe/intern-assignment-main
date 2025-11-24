package com.getenrola.aidemo.agent;

import com.getenrola.aidemo.model.AgentRequest;
import com.openai.models.ChatModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PenSalesOpenAiAgentTest {

    private final PenSalesOpenAiAgent penSalesOpenAiAgent = new PenSalesOpenAiAgent();
    String chatModel = ChatModel.GPT_3_5_TURBO.asString();

    @Test
    void testScript() {
        var req = new AgentRequest("Hi, my name is Fred", null, chatModel);
        System.out.println("User: " + req.userText());

        var agentReply = penSalesOpenAiAgent.execute(req);
        System.out.println("Agent: " + agentReply.text());

        assertThat(agentReply.responseId()).isNotNull();
        assertThat(agentReply.text()).isNotNull();
    }

    @Test
    void testDiscoveryStep() {
        var req = new AgentRequest("Hi", null, chatModel);
        System.out.println("User: " + req.userText());

        var agentReply = penSalesOpenAiAgent.execute(req);
        System.out.println("Discovery Test -> " + agentReply.text());

        assertThat(agentReply.text().toLowerCase())
                .containsAnyOf(
                        "use a pen",
                        "what do you usually",
                        "what do you use a pen for",
                        "tell me what you need it for");
    }

    @Test
    void testPresentationStep() {
        var req = new AgentRequest("How much is the pen?", null, chatModel);
        System.out.println("User: " + req.userText());

        var agentReply = penSalesOpenAiAgent.execute(req);
        System.out.println("Presentation Test -> " + agentReply.text());

        assertThat(agentReply.text().toLowerCase())
                .containsAnyOf(
                        "5000",
                        "titanium",
                        "diamonds",
                        "black ink",
                        "premium");
    }

    @Test
    void testTemperatureCheckStep() {
        var req = new AgentRequest("Okay sounds good?", null, chatModel);
        System.out.println("User: " + req.userText());

        var agentReply = penSalesOpenAiAgent.execute(req);
        System.out.println("Temperature Check -> " + agentReply.text());

        assertThat(agentReply.text().toLowerCase())
                .containsAnyOf(
                        "how does that sound",
                        "anything you'd like to know",
                        "are you interested",
                        "does that work for you",
                        "questions about it");
    }

    @Test
    void testCommitmentStep() {
        var req = new AgentRequest("No more questions", null, chatModel);
        System.out.println("User: " + req.userText());

        var agentReply = penSalesOpenAiAgent.execute(req);
        System.out.println("Commitment Step-> " + agentReply.text());

        assertThat(agentReply.text().toLowerCase())
                .containsAnyOf(
                        "would you like me to",
                        "should i send you the link",
                        "ready to move forward",
                        "keen to grab one");
    }

    @Test
    void testActionStep() {
        var req = new AgentRequest("Yes send link", null, chatModel);
        System.out.println("User: " + req.userText());

        var agentReply = penSalesOpenAiAgent.execute(req);
        System.out.println("Action Step -> " + agentReply.text());

        assertThat(agentReply.text().toLowerCase())
                .containsAnyOf(
                        "here’s your link",
                        "i can send you the link",
                        "purchase",
                        "valid for");
    }

    // Test: Agent should keep messages short and natural, not too long.
    @Test
    void testSmsStyleLength() {
        var req = new AgentRequest("Tell me about the pen", null, chatModel);
        System.out.println("SMS Style Test → " + req.userText());

        var agentReply = penSalesOpenAiAgent.execute(req);
        assertThat(agentReply.text().length()).isLessThan(350);
    }

    @Test
    void testObjectionHandling() {
        AgentRequest req = new AgentRequest("Seems expensive!", null, chatModel);
        var reply = penSalesOpenAiAgent.execute(req);

        System.out.println("Objection Handling Test → " + reply.text());

        assertThat(reply.text().toLowerCase())
                .containsAnyOf(
                        "i get that",
                        "totally fair",
                        "understand",
                        "makes sense",
                        "reason why"
                );
    }

    // Tiny test to score the agent's response
    @Test
    void testLightweightScoringEval() {
        AgentRequest req = new AgentRequest("I'm not sure if it's worth the price", null, chatModel);
        var reply = penSalesOpenAiAgent.execute(req);

        String text = reply.text().toLowerCase();
        int score = 0;

        if (text.contains("understand") || text.contains("get that")) score++;   // acknowledges objection
        if (text.contains("titanium") || text.contains("diamonds") || text.contains("premium")) score++; // mentions value
        if (text.length() < 350) score++; // sms-style clarity

        System.out.println("Lightweight Eval Score: " + score);
        System.out.println("Reply: " + reply.text());

        assertThat(score)
                .as("Agent should score at least 2/3")
                .isGreaterThanOrEqualTo(2);
    }
}
