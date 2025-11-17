package com.getenrola.aidemo.model;

public record AgentRequest(String userText, String previousResponseId, String chatModel) {
}
