package com.xeelux.droplets.core.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEventHandler<EventArgumentsType, EventListenerType>
        implements EventHandler<EventArgumentsType, EventListenerType> {

    private final Logger logger = LogManager.getLogger(AbstractEventHandler.class);
    private final List<EventListenerType> eventListeners = new ArrayList<>();

    protected List<EventListenerType> getEventListeners() {
        return eventListeners;
    }

    @Override
    public void addEventListener(final EventListenerType eventListener) {
        if (eventListener == null) { return; }

        eventListeners.add(eventListener);
    }

    @Override
    public void removeEventListener(final int index) {
        // checking if the given index is out of bounds...
        if (index < 0 || index >= eventListeners.size()) { return; }

        eventListeners.remove(index);
    }

    @Override
    public void removeEventListener(final EventListenerType eventListener) {
        if (eventListener == null) { return; }

        eventListeners.remove(eventListener);
    }

    @Override
    public void removeAllEventListeners() {
        eventListeners.clear();
    }

    @Override
    public abstract void executeEventListener(final EventArgumentsType eventArguments);
}
