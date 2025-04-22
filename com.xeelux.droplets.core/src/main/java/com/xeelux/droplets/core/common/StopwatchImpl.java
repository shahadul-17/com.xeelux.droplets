package com.xeelux.droplets.core.common;

import com.xeelux.droplets.core.utilities.DateTimeFormatter;

class StopwatchImpl implements Stopwatch {

    private long startTime = 0L;
    private long endTime = 0L;
    private long elapsedTimeInMilliseconds = 0L;

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public Stopwatch reset() {
        startTime = 0L;
        endTime = 0L;
        elapsedTimeInMilliseconds = 0L;

        return this;
    }

    @Override
    public Stopwatch start() {
        // if the stopwatch is already started or
        // the stopwatch is already stopped...
        if (startTime != 0L || endTime != 0L) { return this; }

        // gets the current timestamp...
        startTime = System.currentTimeMillis();

        return this;
    }

    @Override
    public Stopwatch startNew() {
        reset();

        return start();
    }

    @Override
    public Stopwatch stop() {
        // if the stopwatch is not started, we'll return...
        if (startTime == 0L) { return this; }

        // gets the current timestamp...
        endTime = System.currentTimeMillis();
        // measures the elapsed time...
        elapsedTimeInMilliseconds = endTime - startTime;

        return this;
    }

    @Override
    public long getElapsedTime() {
        return elapsedTimeInMilliseconds;
    }

    @Override
    public String getHumanReadableElapsedTime() {
        // gets the elapsed time in milliseconds...
        final var elapsedTimeInMilliseconds = getElapsedTime();
        // formatting the elapsed time (in milliseconds) to make it more human-readable...
        final var humanReadableElapsedTime = DateTimeFormatter.formatTime(elapsedTimeInMilliseconds);

        // returning the human-readable elapsed time...
        return humanReadableElapsedTime;
    }
}
