package ee.ut.cs.dsg.esperadapter.queries;

import com.espertech.esper.common.internal.collection.Pair;
import ee.ut.cs.dsg.esperadapter.environment.AdaptedEsperEnvironmentBuilder;
import test.events.AuctionEvent;
import test.events.BidEvent;
import test.events.PersonEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
/*
SELECT bid.itemid
FROM bid [RANGE 60 MINUTES PRECEDING]
WHERE (SELECT COUNT(bid.itemid)
FROM bid [PARTITION BY bid.itemid
RANGE 60 MINUTES PRECEDING])
>= ALL (SELECT COUNT(bid.itemid)
FROM bid [PARTITION BY bid.itemid
RANGE 60 MINUTES PRECEDING];
 */
public class Query5 implements Query{
    public double parsingTime= 0;

    public String query="@name('q5') " +
            "SELECT auction, COUNT(*)\n" +
            "FROM BidEvent.win:time(60 minutes)\n" +
            "GROUP BY auction\n" +
            "HAVING COUNT(*)>=ALL(" +
            "SELECT COUNT(*)\n" +
            "FROM BidEvent.win:time(60 minutes)\n" +
            "GROUP BY auction)";

    @Override
    public void execute() {
        AdaptedEsperEnvironmentBuilder builder = new AdaptedEsperEnvironmentBuilder();
        parsingTime = 0;
        builder.withBeanType(AuctionEvent.class)
                .withBeanType(BidEvent.class)
                .withBeanType(PersonEvent.class)
                .addQueryName("query-5")
                .addStatement(query, "q5", true)
                .buildRuntime(true, true)
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
