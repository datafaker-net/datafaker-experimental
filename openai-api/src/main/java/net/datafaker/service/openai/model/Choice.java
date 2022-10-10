
package net.datafaker.service.openai.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class Choice {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("index")
    @Expose
    private Integer index;
    @SerializedName("logprobs")
    @Expose
    private Object logprobs;
    @SerializedName("finish_reason")
    @Expose
    private String finishReason;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Object getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(Object logprobs) {
        this.logprobs = logprobs;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }

}
