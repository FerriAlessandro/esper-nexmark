package test;

import com.espertech.esper.common.internal.collection.Pair;
import ee.ut.cs.dsg.esperadapter.environment.AdaptedEsperEnvironmentBuilder;
import ee.ut.cs.dsg.esperadapter.environment.adapters.EsperCustomAdapterConfig;
import ee.ut.cs.dsg.esperadapter.queries.event.SpeedEvent;
import ee.ut.cs.dsg.esperadapter.util.Util;
import org.apache.flink.api.java.utils.ParameterTool;

import java.util.Properties;

public class Main {
    public static void main(String[] args){

        ParameterTool parameterTool = ParameterTool.fromSystemProperties();
        Properties props = Util.getConfiguration(parameterTool);
        String query = "";
        AdaptedEsperEnvironmentBuilder builder = new AdaptedEsperEnvironmentBuilder();

        builder.withBeanType(SpeedEvent.class)
                .addStatement(query)
                .buildRuntime(true, true)
                .fromFile(props).start(s -> {
                    String[] data = s.replace("[","").replace("]","").split(", ");
                    long ts = Long.parseLong(data[8].trim());
                    SpeedEvent event = new SpeedEvent(data[0].trim(), ts,Integer.parseInt(data[1].trim()));
                    if(Long.parseLong(data[0].trim())==-1){
                        return new Pair<>(event,-1L);
                    }
                    return new Pair<>(event, ts);
                });

    }
}
