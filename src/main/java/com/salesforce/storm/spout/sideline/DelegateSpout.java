package com.salesforce.storm.spout.sideline;

import com.salesforce.storm.spout.sideline.kafka.ConsumerState;

public interface DelegateSpout {

    void open();

    void close();

    Message nextTuple();

    void ack(Object msgId);

    void fail(Object msgId);

    String getVirtualSpoutId();

    void flushState();

    void requestStop();

    boolean isStopRequested();

    ConsumerState getCurrentState();

    double getMaxLag();

    int getNumberOfFiltersApplied();
}