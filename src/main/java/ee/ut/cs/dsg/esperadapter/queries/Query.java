package ee.ut.cs.dsg.esperadapter.queries;

import com.espertech.esper.common.client.module.ParseException;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;

import java.io.IOException;

public interface Query {

    void execute() throws EPDeployException, IOException, ParseException, EPCompileException;

}

