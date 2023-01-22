package net.datafaker.service.openai.model;

public enum OpenAIModel {

    /**
     * Improved model in the GPT-3 series. Handles more complex instructions, better at long form generatioon.
     * Can perform any task the other GPT-3 models can, often with less context.
     */
    TEXT_DAVINCI_003("text-davinci-003"),

    /**
     * Very capable model in the GPT-3 series. Can perform any task the other GPT-3 models can,
     * often with less context.
     */
    TEXT_DAVINCI_002("text-davinci-002"),

    /**
     * Formerly curie-instruct-beta-v2. Very capable, but faster and lower cost than text-davinci-002.
     */
    TEXT_CURIE_001("text-curie-001"),

    /**
     * Formerly babbage-instruct-beta Capable of straightforward tasks, very fast, and lower cost.
     */
    TEXT_BABBAGE_001("text-babbage-001"),

    /**
     * Formerly ada-instruct-beta Capable of simple tasks, usually the fastest model in the GPT-3 series,
     * and lowest cost.
     */
    TEXT_ADA_001("text-ada-001"),

    /**
     * Most capable model in the Codex series, which can understand and generate code, including
     * translating natural language to code.
     */
    CODE_DAVINCI_002("code-davinci-002"),

    /**
     * Formerly cushman-codex. Almost as capable as code- davinci-002, but slightly faster.
     * Part of the Codex series, which can understand and generate code.
     */
    CODE_CUSHMAN_001("code-cushman-001"),

    /**
     * Formerly davinci-codex. Older version of the most capable model in the Codex series, which
     * can understand and generate code, including translating natural language to code. Its per-request
     * token limit is double the usual limit (4,096 tokens instead of 2,048).
     */
    CODE_DAVINCI_001("code-davinci-001");

    private final String modelName;

    OpenAIModel(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }
}
