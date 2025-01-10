/*
package ee.ut.cs.dsg.esperadapter.util;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;
import java.util.UUID;

public class Util {

    public static Properties getConfiguration(ParameterTool parameterTool){
        Properties props = new Properties();

        props.put(EsperCustomAdapterConfig.TOPIC_NAME, parameterTool.get("topic", "linear-road-data"));
        props.put(EsperCustomAdapterConfig.EVENT_NAME, "SpeedEvent");
        props.put(EsperCustomAdapterConfig.INPUT_FILE_NAME, parameterTool.get("file"));
        props.put(EsperCustomAdapterConfig.PERF_FILE_NAME, parameterTool.get("performance"));
        props.put(EsperCustomAdapterConfig.EXPERIMENT_ID, parameterTool.get("exp", "exp") );
        props.put(EsperCustomAdapterConfig.STATEMENT_NAME, parameterTool.get("query", "Aggregate"));
        props.put(EsperCustomAdapterConfig.ON_CLUSTER, parameterTool.get("cluster", "false"));

        return props;
    }
}
*/
