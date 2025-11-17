package com.getenrola.aidemo.ui;

import com.getenrola.aidemo.agent.PenSalesOpenAiAgent;
import com.getenrola.aidemo.model.AgentReply;
import com.getenrola.aidemo.model.AgentRequest;
import com.openai.models.ChatModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleChat implements CommandLineRunner {

    private final PenSalesOpenAiAgent penSalesOpenAiAgent;

    public ConsoleChat(PenSalesOpenAiAgent penSalesOpenAiAgent) {
        this.penSalesOpenAiAgent = penSalesOpenAiAgent;
    }

    @Override
    public void run(String... args) {
        System.out.println("Pen Sales Agent (type 'exit' to quit)\n");

        String previousResponseId = null;
        final String model = ChatModel.GPT_3_5_TURBO.asString();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("You: ");
                if (!scanner.hasNextLine()) break;       // EOF (Ctrl+D/Ctrl+Z)
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) break;

                AgentReply reply = penSalesOpenAiAgent.execute(new AgentRequest(line, previousResponseId, model));
                previousResponseId = reply.responseId();

                System.out.println("Agent: " + reply.text() + "\n");
            }
        }

        System.out.println("Goodbye 👋");
    }
}
