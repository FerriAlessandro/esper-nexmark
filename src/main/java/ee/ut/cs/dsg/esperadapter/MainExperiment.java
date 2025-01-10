package ee.ut.cs.dsg.esperadapter;


import com.opencsv.CSVWriter;
import ee.ut.cs.dsg.esperadapter.queries.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class MainExperiment {

    public static void main(String[] args) {

        List<Query> queries = new ArrayList<>();
        int iterations = 10;

        //queries.add(new Query1());
        //queries.add(new Query2());
        //queries.add(new Query3());
        //queries.add(new Query4());
        queries.add(new Query5());
        //queries.add(new Query6());
        //queries.add(new Query7());
        //queries.add(new Query8());
        String path = "src/main/resources/performances.csv";
        try {
            File file = new File(path);
            CSVWriter writer = new CSVWriter(new FileWriter(file, false));
            String[] firstRow = new String[]{"Experiment-Name", "Throughput(events/ms)", "InputSize", "MillisecondsPassed", "ParsingTime(ms)"};
            writer.writeNext(firstRow);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Query q : queries) {

            for(int i = 0; i<iterations; i++)
                q.execute();

        }
    }
}
