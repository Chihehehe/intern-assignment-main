# 🖊️ AI Demo Assignment — “Sell a Pen”

## What I Built

I built an AI sales agent that sells a luxury pen through SMS-style conversations. The agent follows a 5-step sales process:

1. **Discovery** - Asks what the customer uses a pen for
2. **Presentation** - Shows pen features and links them to the customer's needs
3. **Temperature Check** - Asks "how does that sound?" to gauge interest
4. **Commitment** - Acknowledges excitement and asks if they have questions
5. **Action** - Offers the purchase link when ready

The agent can also handle objections (like "too expensive") using a structured approach: acknowledge, reframe, reconfirm, check readiness, and transition back to the sale.

## Why I Made These Design Choices

### 1. **Combined Presentation and Temperature Check**
I put the temperature check ("how does that sound?") right after presenting features. This makes the conversation flow more naturally - instead of waiting for the customer to respond before checking interest, we ask immediately. It feels more like a real conversation.

### 2. **Strict Step Separation**
I made very clear rules about which phrases belong to which step. This prevents the agent from mixing steps (like asking discovery questions during the commitment phase). Each step has specific phrases it must use, and it can't use phrases from other steps.

### 3. **Linking Features to Customer Needs**
When the customer says they need the pen for "signing" or "writing", the agent explains why the pen is perfect for that specific use case. This makes the sales pitch more personal and relevant, not just a list of features.

### 4. **Concise Prompt**
I kept the system prompt as short as possible while still being clear. This helps avoid hitting the context window limit when conversations get long. I removed repetitive instructions and combined similar rules.

### 5. **Objection Handling Flow**
After handling objections, if the customer shows positive interest (like saying "nice"), the agent moves directly to offering the link. It doesn't ask "any questions?" again because we already went through that phase. This prevents the conversation from going in circles.

## How to Run and Test Your Agent

### Prerequisites
- Java 24 (check with `java -version`)
- OpenAI API key
- Maven (included as `mvnw` wrapper)

### Setup

1. **Set your OpenAI API key:**
   ```bash
   export OPENAI_API_KEY=your_api_key_here
   ```

   On Windows PowerShell:
   ```powershell
   $env:OPENAI_API_KEY="your_api_key_here"
   ```

2. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

   On Windows:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

3. **Chat with the agent:**
   - Type your messages and press Enter
   - Type `exit` or `quit` to stop

### Running Tests

Run all tests:
```bash
./mvnw test
```

Run a specific test:
```bash
./mvnw -Dtest=PenSalesOpenAiAgentTest test
```

The test file includes 9 test cases that check:
- Discovery step (asks about pen usage)
- Presentation step (mentions price/features)
- Temperature check step (uses interest-checking phrases)
- Commitment step (moves toward purchase)
- Action step (provides link)
- SMS-style length (keeps messages under 350 characters)
- Objection handling (acknowledges concerns)
- Lightweight scoring (evaluates response quality)

### Example Conversation

```
You: hi
Agent: What do you usually use a pen for?

You: signing
Agent: This pen is perfect for signing with its premium black ink and elegant titanium case encrusted with diamonds. It's priced at $5000. How does that sound to you?

You: nice
Agent: Looks like you're excited about our pen! Do you have any questions?

You: how much
Agent: This exclusive pen is priced at $5000, reflecting its exceptional craftsmanship, titanium case, diamonds, and premium black ink. Any other questions?

You: no
Agent: Would you like me to send you the link?

You: yes
Agent: Here's your link to purchase...
```

## Anything Else You Should Know

### Conversation Memory (Context Window Limitation)
If conversations get very long, the model eventually hits its context limit. I tried two ideas for handling memory:

1. **Use `previousResponseId` (current setup).** Easiest option—let OpenAI track the chat for me. When the thread gets too long I just reset `previousResponseId` and start fresh. Fast to build, but the agent forgets everything once it resets.
2. **Keep a short local buffer.** I could store the last few user/agent messages in memory and resend them every time. That would keep some context after a reset, but it requires more code and token management. I skipped it for now to keep things simple.
3. **Persist history in MySQL.** In a previous RAG project I built a full MySQL chat-history service (users, sessions, messages). It’s great for multi-user apps, but it felt like overkill here, so I mentioned it only as future work.

All work; I stuck with option 1 because it covered the assignment without extra overhead.

### The Pen Details
- Price: $5000
- Features: Titanium case, encrusted with diamonds, black ink
- It's a luxury, one-of-a-kind pen

### Project Structure
- `PenSalesOpenAiAgent.java` - The main agent with the system prompt and API calls
- `PenSalesOpenAiAgentTest.java` - Test cases to verify the agent works correctly
- `ConsoleChat.java` - Simple console interface for chatting with the agent

### What I Learned
Building this helped me understand:
- How to structure prompts for consistent behavior
- The importance of clear step separation in sales processes
- How to handle edge cases (objections, questions, context limits)
- Balancing prompt detail with context window constraints

The key challenge was getting the agent to follow the exact steps without mixing them up. It took several iterations to get the prompt right, but now the agent reliably follows the sales process!

### Structured Output
Each agent reply now contains:
1. A customer-facing SMS line (<=3 sentences).
2. A metadata line like `Stage: Presentation; Interest: warm`.
Both lines are visible in the console so I can inspect behavior, but in a real UI we'd only show the SMS line and log the metadata for analytics.

### Local Tool Use
I also added a small helper method called ProductInfo.getLuxuryPenDetails().
The idea is really straightforward: before the agent replies, it checks this method to get the latest pen information — like the price, ink type, body material, and extra features.

This makes things a lot easier because I can change the product details in one place, and I don’t have to edit the whole prompt every time.
If the price changes or I want to add a new feature, I just update the helper method and the agent will automatically use the new info in its responses.

### Few-Shot Prompting (Plan)
I’m also thinking about adding a few example conversations directly into the prompt. Instead of only giving instructions, I would include 3–5 short samples, like:

- User says "Hi" → Agent asks about pen usage (Discovery step)
- User says "signing" → Agent presents features linked to signing (Presentation step)
- User says "too expensive" → Agent handles objection properly

**How it would improve performance:**
- Models learn better when they can see real examples.
- It gives the agent a clear pattern to follow, so responses are more consistent.
- Even if the instructions are confusing, the examples show the model exactly how it should act.

Right now I'm using instruction-based prompting, but adding few-shot examples would likely make the agent more reliable and closer to the desired behavior.

### Lightweight Eval Framework (Plan)
One thing I'd like to add next is a tiny evaluation system:
- I would send a list of preset user messages into the agent, and test how it responds when I change the prompt, model, or temperature.
- For each reply, I'd use another AI call score things like stage accuracy, tone, and objection handling.
- I'd save all the scores and notes so I can compare different versions and see when the agent gets better or worse.

