package com.slamcode.goalcalendar.backup.azure.service;

public class AzureServiceConnection
{
    private String connectionUrl;
    private String functionKey;

    public AzureServiceConnection(String connectionUrl, String functionKey)
    {
        this.connectionUrl = connectionUrl;
        this.functionKey = functionKey;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getFunctionKey() {
        return functionKey;
    }

    public void setFunctionKey(String functionKey) {
        this.functionKey = functionKey;
    }
}
