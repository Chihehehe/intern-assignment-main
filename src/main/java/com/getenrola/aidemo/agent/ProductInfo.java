package com.getenrola.aidemo.agent;

public final class ProductInfo {

    private ProductInfo() {}

    /**
     * Simulates a local knowledge/tool call. If product specs change,
     * we only update them here and the agent will pick up the new data.
     */
    public static String getLuxuryPenDetails() {
        return """
                Product: One-of-a-kind luxury fountain pen
                Price: $5000 USD
                Ink: Premium black ink cartridge
                Body: Titanium case encrusted with hand-set diamonds
                Ideal for: Signing important documents, gifting VIP clients, showcasing executive style
                """;
    }
}

