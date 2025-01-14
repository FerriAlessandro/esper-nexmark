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
SELECT person.name, person.city,
person.state, open auction.id
FROM open auction, person, item
WHERE open auction.sellerId = person.id
AND person.state = ‘OR’
AND open auction.itemid = item.id
AND item.categoryId = 10;
 */
public class Query3 implements Query{
    public double parsingTime= 0;

    public String query="@name('q3') " +
            "SELECT PersonEvent.name, PersonEvent.city, PersonEvent.state, AuctionEvent.id AS auctionId\n" +
            "FROM AuctionEvent#keepall\n" +
            "JOIN PersonEvent#keepall ON AuctionEvent.seller = PersonEvent.id\n" +
            "WHERE PersonEvent.state = 'OR'\n" +
            "AND AuctionEvent.category = 10;\n";

    @Override
    public void execute() {
        AdaptedEsperEnvironmentBuilder builder = new AdaptedEsperEnvironmentBuilder();
        parsingTime = 0;
        builder.withBeanType(AuctionEvent.class)
                .withBeanType(BidEvent.class)
                .withBeanType(PersonEvent.class)
                .addQueryName("query-3")
                .addStatement(query, "q3", true)
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
            // Step 1: Read all lines from the file
            List<String> lines = Files.readAllLines(Paths.get(path));

            String lastLine = lines.remove(lines.size() - 1); // Get and remove the last line
            lastLine = lastLine.trim(); // Remove any trailing newlines or spaces
            lastLine += ",\""+this.parsingTime+"\""; // Append the new value
            lines.add(lastLine); // Add the modified line back to the list

            // Step 3: Write the updated content back to the file
            Files.write(Paths.get(path), lines);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
