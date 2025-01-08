package ee.ut.cs.dsg.esperadapter.environment;

import com.espertech.esper.common.internal.collection.Pair;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPUndeployException;
import ee.ut.cs.dsg.esperadapter.environment.adapters.EsperCustomAdapter;
import ee.ut.cs.dsg.esperadapter.environment.adapters.FileEsperCustomAdapter;
/*
import ee.ut.cs.dsg.esperadapter.environment.adapters.KafkaEsperCustomAdapter;
*/

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Esper Environment that contains the actual {@link EPRuntime} and the {@link EsperCustomAdapter}.
 * It provides the actual DSL for the creation of the Esper instance.
 *

 */
public class AdaptedEsperEnvironment {

    private final EPRuntime runtime;
    private final Logger LOGGER = Logger.getLogger(AdaptedEsperEnvironment.class.getName());
    private EsperCustomAdapter adapter;
    private boolean registerPerf;
    private String queryName;


    public AdaptedEsperEnvironment(EPRuntime runtime, boolean registerPerf, String queryName) {
        this.runtime = runtime;
        this.registerPerf=registerPerf;
        this.adapter= transformationFunction -> LOGGER.warning("Empty Source");
        this.queryName = queryName;
    }

    private AdaptedEsperEnvironment(EPRuntime runtime, EsperCustomAdapter adapter) {
        this.runtime = runtime;
        this.adapter = adapter;
    }

 /*   public AdaptedEsperEnvironment fromKafka(Properties props){
        this.adapter = new KafkaEsperCustomAdapter<>(props, runtime.getEventService(), registerPerf);
        return this;
    }

    public AdaptedEsperEnvironment fromKafka(Properties props, long maxEvents){
        this.adapter = new KafkaEsperCustomAdapter<>(props, runtime.getEventService(), maxEvents, registerPerf);
        return this;
    }

    public AdaptedEsperEnvironment fromKafka(Properties props, Duration minutes){
        this.adapter = new KafkaEsperCustomAdapter<>(props, runtime.getEventService(), minutes, registerPerf);
        return this;
    }
*/

    public AdaptedEsperEnvironment fromFile(String fileName){
        return new AdaptedEsperEnvironment(this.runtime, new FileEsperCustomAdapter(fileName, runtime.getEventService(), registerPerf, queryName));
    }



    public void start(Function<String, Pair<Object,Long>> transformationFunction){
        adapter.process(transformationFunction);
        try {
            runtime.getDeploymentService().undeployAll();
        } catch (EPUndeployException e) {
            e.printStackTrace();
        }
    }



}
