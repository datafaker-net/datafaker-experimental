package net.datafaker.service.openai.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class Request {

    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("prompt")
    @Expose
    private String prompt;
    @SerializedName("max_tokens")
    @Expose
    private Integer maxTokens;
    @SerializedName("temperature")
    @Expose
    private Double temperature;

    /**
     * @param maxTokens
     * @param temperature
     * @param model
     * @param prompt
     */
    public Request(String model, String prompt, Integer maxTokens, Double temperature) {
        super();
        this.model = model;
        this.prompt = prompt;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

}
