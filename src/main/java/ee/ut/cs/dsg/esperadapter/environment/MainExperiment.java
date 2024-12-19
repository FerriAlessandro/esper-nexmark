package ee.ut.cs.dsg.esperadapter.environment;

import com.espertech.esper.common.internal.collection.Pair;
import ee.ut.cs.dsg.esperadapter.queries.event.SpeedEvent;
import ee.ut.cs.dsg.esperadapter.environment.adapters.EsperCustomAdapterConfig;
import ee.ut.cs.dsg.esperadapter.util.Util;
import org.apache.flink.api.java.utils.ParameterTool;
import test.events.AuctionEvent;
import test.events.BidEvent;
import test.events.PersonEvent;

import java.util.ArrayList;
import java.util.Properties;


public class MainExperiment {

    public static void main(String[] args) {


        String query = "select * from PersonEvent";
        AdaptedEsperEnvironmentBuilder builder = new AdaptedEsperEnvironmentBuilder();

        builder.withBeanType(AuctionEvent.class)
                .withBeanType(BidEvent.class)
                .withBeanType(PersonEvent.class)
                .addStatement(query)
                .buildRuntime(true, false)
                .fromFile("src/main/resources/events.txt").start(s -> {
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

                    if (eventName.equals("Auction")) {
                        AuctionEvent event = new AuctionEvent(tuple, ts);
                        return new Pair<>(event, ts);
                    } else if (eventName.equals("Person")) {
                        PersonEvent event = new PersonEvent(tuple, ts);
                        return new Pair<>(event, ts);
                    } else if (eventName.equals("Bid")) {
                        BidEvent event = new BidEvent(tuple, ts);
                        return new Pair<>(event, ts);

                    } else {
                        throw new RuntimeException("Wrong event name in file line");
                    }
                });

    }
}
