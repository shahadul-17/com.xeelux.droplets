package com.xeelux.droplets.core.configurations;

import com.xeelux.droplets.core.text.JsonSerializable;

public final class Configuration implements JsonSerializable {

    private boolean includeStackTrace;
    private String host;
    private int port;
    private double version;
    private String instanceId;
    private String applicationName;
    private String profile;
    private String uniqueValue;
    private VirtualThreadSchedulerConfiguration virtualThreadScheduler;

    public boolean shallIncludeStackTrace() {
        return includeStackTrace;
    }

    public Configuration setIncludeStackTrace(final boolean includeStackTrace) {
        this.includeStackTrace = includeStackTrace;

        return this;
    }

    public String getHost() {
        return host;
    }

    public Configuration setHost(final String host) {
        this.host = host;

        return this;
    }

    public int getPort() {
        return port;
    }

    public Configuration setPort(final int port) {
        this.port = port;

        return this;
    }

    public double getVersion() {
        return version;
    }

    public Configuration setVersion(final double version) {
        this.version = version;

        return this;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public Configuration setInstanceId(final String instanceId) {
        this.instanceId = instanceId;

        return this;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Configuration setApplicationName(final String applicationName) {
        this.applicationName = applicationName;

        return this;
    }

    public String getProfile() {
        return profile;
    }

    public Configuration setProfile(final String profile) {
        this.profile = profile;

        return this;
    }

    public String getUniqueValue() {
        return uniqueValue;
    }

    public Configuration setUniqueValue(final String uniqueValue) {
        this.uniqueValue = uniqueValue;

        return this;
    }

    public VirtualThreadSchedulerConfiguration getVirtualThreadScheduler() {
        return virtualThreadScheduler;
    }

    public Configuration setVirtualThreadScheduler(final VirtualThreadSchedulerConfiguration virtualThreadScheduler) {
        this.virtualThreadScheduler = virtualThreadScheduler;

        return this;
    }

    @Override
    public String toString() {
        return toJson(true);
    }
}
