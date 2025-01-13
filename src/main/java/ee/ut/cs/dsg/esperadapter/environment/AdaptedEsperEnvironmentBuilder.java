package ee.ut.cs.dsg.esperadapter.environment;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;
import ee.ut.cs.dsg.esperadapter.util.LoggingListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Builder class to create {@link AdaptedEsperEnvironment} instances.
 * It gives the possibility to add statements, and attach the relative listener.
 *

 */
public class AdaptedEsperEnvironmentBuilder {

    private final EPCompiler compiler;
    private final Configuration configuration;
    private final Map<String, EPCompiled> compiledStatementList;
    private final Map<String, Boolean> listeners;
    private String queryName;

    public AdaptedEsperEnvironmentBuilder() {
        compiler = EPCompilerProvider.getCompiler();
        configuration = new Configuration();
        compiledStatementList = new HashMap<>();
        listeners = new HashMap<>();
    }


    // TODO: add supports for multiple types, associating one mapping fucntion for each
    public AdaptedEsperEnvironmentBuilder withBeanType(Class name){
        configuration.getCommon().addEventType(name);
        return this;
    }
    public AdaptedEsperEnvironmentBuilder addQueryName(String queryName){
        this.queryName = queryName;
        return this;
    }

    public AdaptedEsperEnvironmentBuilder addStatement(String query, String statementName, boolean attachListener){

        CompilerArguments compilerArguments = new CompilerArguments(configuration);

        try {
            EPCompiled epCompiled = compiler.compile(query, compilerArguments);
            compiledStatementList.put(statementName, epCompiled);
            listeners.put(statementName, attachListener);

        } catch (EPCompileException ex) {
            // handle exception here
            ex.printStackTrace();
        }
        return this;
    }


    public AdaptedEsperEnvironment buildRuntime(boolean externalClock, boolean registerPerf){
        EPRuntime runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
        if(externalClock)
            runtime.getEventService().clockExternal();

        for (String tempName: compiledStatementList.keySet()
             ) {
            deployStatement(tempName, runtime);
        }

        return new AdaptedEsperEnvironment(runtime, registerPerf, queryName);
    }

    private void deployStatement(String statementName, EPRuntime runtime){
        EPDeployment deployment;
        try {
            deployment = runtime.getDeploymentService().deploy(compiledStatementList.get(statementName));
        } catch (EPDeployException ex) {
            throw new RuntimeException(ex);
        }

        if(listeners.get(statementName)) {
            //attachLoggingListener(runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), statementName));
            attachLoggingListener(runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), statementName));
//The second one works, the first one doesn't. The deployment does not keep the original statement name for some reason
        }
    }

    private void attachLoggingListener(EPStatement statement){

        // Creating the output log file
        File temp = new File("src/main/resources/outputs-"+queryName+".txt");
        try {
            if(!temp.exists()){
                temp.createNewFile();
            }

            // Attaching the listener to the statement
            statement.addListener(new LoggingListener(temp));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
