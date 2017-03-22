package com.salesforce.storm.spout.sideline;

import com.google.common.base.Strings;
import com.salesforce.storm.spout.sideline.config.SidelineSpoutConfig;
import com.salesforce.storm.spout.sideline.kafka.deserializer.Deserializer;
import com.salesforce.storm.spout.sideline.kafka.failedMsgRetryManagers.FailedMsgRetryManager;
import com.salesforce.storm.spout.sideline.metrics.MetricsRecorder;
import com.salesforce.storm.spout.sideline.persistence.PersistenceManager;
import com.salesforce.storm.spout.sideline.tupleBuffer.TupleBuffer;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * Handles creating instances of specific interface implementations, based off of
 * our configuration.
 * Methods are marked Synchronized because the FactoryManager instance is shared between threads, but
 * its methods are rarely invoked after the spout is initial started up, so it shouldn't present much of
 * a problem w/ contention.
 */
public class FactoryManager implements Serializable {

    /**
     * Holds our configuration so we know what classes to create instances of.
     */
    private final Map topologyConfig;

    /**
     * Class instance of our Deserializer.
     */
    private transient Class<? extends Deserializer> deserializerClass;

    /**
     * Class instance of our FailedMsgRetryManager.
     */
    private transient Class<? extends FailedMsgRetryManager> failedMsgRetryManagerClass;

    /**
     * Class instance of our PersistenceManager.
     */
    private transient Class<? extends PersistenceManager> persistenceManagerClass;

    /**
     * Class instance of our Metrics Recorder.
     */
    private transient Class<? extends MetricsRecorder> metricsRecorderClass;

    /**
     * Class instance of our Tuple Buffer.
     */
    private transient Class<? extends TupleBuffer> tupleBufferClass;

    public FactoryManager(Map topologyConfig) {
        // Create immutable clone of configuration.
        this.topologyConfig = Collections.unmodifiableMap(topologyConfig);
    }

    /**
     * @return returns a new instance of the configured deserializer.
     */
    public synchronized Deserializer createNewDeserializerInstance() {
        if (deserializerClass == null) {
            final String classStr = (String) topologyConfig.get(SidelineSpoutConfig.DESERIALIZER_CLASS);
            if (Strings.isNullOrEmpty(classStr)) {
                throw new IllegalStateException("Missing required configuration: " + SidelineSpoutConfig.DESERIALIZER_CLASS);
            }

            try {
                deserializerClass = (Class<? extends Deserializer>) Class.forName(classStr);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return deserializerClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return returns a new instance of the configured FailedMsgRetryManager.
     */
    public synchronized FailedMsgRetryManager createNewFailedMsgRetryManagerInstance() {
        if (failedMsgRetryManagerClass == null) {
            String classStr = (String) topologyConfig.get(SidelineSpoutConfig.FAILED_MSG_RETRY_MANAGER_CLASS);
            if (Strings.isNullOrEmpty(classStr)) {
                throw new IllegalStateException("Missing required configuration: " + SidelineSpoutConfig.TUPLE_BUFFER_CLASS);
            }

            try {
                failedMsgRetryManagerClass = (Class<? extends FailedMsgRetryManager>) Class.forName(classStr);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return failedMsgRetryManagerClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return returns a new instance of the configured persistence manager.
     */
    public synchronized PersistenceManager createNewPersistenceManagerInstance() {
        if (persistenceManagerClass == null) {
            final String classStr = (String) topologyConfig.get(SidelineSpoutConfig.PERSISTENCE_MANAGER_CLASS);
            if (Strings.isNullOrEmpty(classStr)) {
                throw new IllegalStateException("Missing required configuration: " + SidelineSpoutConfig.PERSISTENCE_MANAGER_CLASS);
            }

            try {
                persistenceManagerClass = (Class<? extends PersistenceManager>) Class.forName(classStr);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return persistenceManagerClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return returns a new instance of the configured Metrics Recorder manager.
     */
    public synchronized MetricsRecorder createNewMetricsRecorder() {
        if (metricsRecorderClass == null) {
            String classStr = (String) topologyConfig.get(SidelineSpoutConfig.METRICS_RECORDER_CLASS);
            if (Strings.isNullOrEmpty(classStr)) {
                throw new IllegalStateException("Missing required configuration: " + SidelineSpoutConfig.METRICS_RECORDER_CLASS);
            }

            try {
                metricsRecorderClass = (Class<? extends MetricsRecorder>) Class.forName(classStr);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return metricsRecorderClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return returns a new instance of the configured TupleBuffer interface.
     */
    public synchronized TupleBuffer createNewTupleBufferInstance() {
        if (tupleBufferClass == null) {
            String classStr = (String) topologyConfig.get(SidelineSpoutConfig.TUPLE_BUFFER_CLASS);
            if (Strings.isNullOrEmpty(classStr)) {
                throw new IllegalStateException("Missing required configuration: " + SidelineSpoutConfig.TUPLE_BUFFER_CLASS);
            }

            try {
                tupleBufferClass = (Class<? extends TupleBuffer>) Class.forName(classStr);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return tupleBufferClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
