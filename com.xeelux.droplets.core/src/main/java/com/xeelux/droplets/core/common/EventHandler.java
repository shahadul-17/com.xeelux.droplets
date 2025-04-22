package com.xeelux.droplets.core.common;

public interface EventHandler<EventArgumentsType, EventListenerType> {
    void addEventListener(final EventListenerType eventListener);
    void removeEventListener(final int index);
    void removeEventListener(final EventListenerType eventListener);
    void removeAllEventListeners();
    void executeEventListener(final EventArgumentsType eventArguments);
}
