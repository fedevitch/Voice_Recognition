package com.eliftech.hawk.voicerecognition.api;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by hawk on 08.07.16.
 */
public class ServerResponse {

    private String status;
    private String command;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The command
     */
    public String getCommand() {
        return command;
    }

    /**
     *
     * @param command
     * The command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
