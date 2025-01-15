package ee.ut.cs.dsg.esperadapter.queries;

import com.espertech.esper.common.client.module.ParseException;
import com.espertech.esper.common.internal.collection.Pair;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import ee.ut.cs.dsg.esperadapter.environment.AdaptedEsperEnvironmentBuilder;
import test.events.AuctionEvent;
import test.events.BidEvent;
import test.events.PersonEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/*
SELECT AVG(CA.price), CA.sellerId
FROM closed auction CA
[PARTITION BY CA.sellerId
ROWS 10 PRECEDING];
 */
public class Query6 implements Query{
    public double parsingTime= 0;

    File queryFile = new File(Objects.requireNonNull(Query6.class.getResource("/query6.epl")).getPath());
    @Override
    public void execute() throws EPDeployException, IOException, ParseException, EPCompileException {
        AdaptedEsperEnvironmentBuilder builder = new AdaptedEsperEnvironmentBuilder();
        parsingTime = 0;
        builder.withBeanType(AuctionEvent.class)
                .withBeanType(BidEvent.class)
                .withBeanType(PersonEvent.class)
                .addQueryName("query-6")
                .addStatementFromFile(true, true, queryFile, "q6-11")
                .fromFile("src/main/resources/events.txt").start(s -> {
                    long start = System.currentTimeMillis();
                    String[] valAndTs = s.split(",", 2);
                    int i = 0;
                    StringBuilder b = new StringBuilder();
                    long ts = Long.parseLong(valAndTs[0]);
                    String tuple = valAndTs[1];
                    /*----Phase 1: parse the type of tuple (Auction, Person or Bid)----*/
                    while (tuple.charAt(i) != '{') {
                        b.append(tuple.charAt(i));
                        i++;
                    }
                    String eventName = b.toString();
                    //get the actual values
                    tuple = tuple.substring(i + 1, tuple.length() - 1);
                    Serializable event;
                    if (eventName.equals("Auction")) {
                        event = new AuctionEvent(tuple, ts);
                    } else if (eventName.equals("Person")) {
                        event = new PersonEvent(tuple, ts);
                    } else if (eventName.equals("Bid")) {
                        event = new BidEvent(tuple, ts);
                    } else {
                        throw new RuntimeException("Wrong event name in file line");
                    }
                    this.parsingTime +=System.currentTimeMillis()-start;
                    return new Pair<>(event, ts);

                });
        String path = "src/main/resources/performances.csv";
        File performances = new File(path);
        if(!performances.exists()){
            throw new RuntimeException("Performance file does not exist");
        }

        try {

            List<String> lines = Files.readAllLines(Paths.get(path));

            String lastLine = lines.remove(lines.size() - 1);
            lastLine = lastLine.trim();
            lastLine += ",\""+this.parsingTime+"\"";
            lines.add(lastLine);


            Files.write(Paths.get(path), lines);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
