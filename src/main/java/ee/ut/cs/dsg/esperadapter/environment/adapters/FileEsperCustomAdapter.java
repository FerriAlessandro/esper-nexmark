package ee.ut.cs.dsg.esperadapter.environment.adapters;

import com.espertech.esper.common.client.EventSender;
import com.espertech.esper.common.internal.collection.Pair;
import com.espertech.esper.runtime.client.EPEventService;
import ee.ut.cs.dsg.esperadapter.util.PerformanceFileBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Function;

/**
 * An adapter for consuming events from a specific file.
 * Since the input is coming from a file, the input events will arrive
 * as {@link String}. Currently, it support homogeneous files, i.e., files that contain
 * events of the same type, sent through the {@link EventSender} object.
 * Every time an event is sent, the time in the {@link com.espertech.esper.runtime.client.EPRuntime}
 * is advanced accordingly, since we are using the external time by default.
 * Optionally, it can register the performance and report it in the performance file.
 *
 */

public class FileEsperCustomAdapter implements EsperCustomAdapter {

    private FileReader fileReader;
    private Properties props;
    private EventSender sender;
    private EventSender senderAuction;

    private EventSender senderPeople;
    private EventSender senderBid;

    private EPEventService epEventService;
    private boolean registerPerf;
    private long maxEvents;
    private long counter = 0L;
    private String queryName;

    /**
     * Constructor for normal events generation. It consumes the file until it end.
     * The {@link FileEsperCustomAdapter#maxEvents} variable is thus set to -1.
     *
     * @param eventService The event service used to advance time
     * @param registerPerf whether we want to generate the performance file
     */

    public FileEsperCustomAdapter(String fileName, EPEventService eventService, boolean registerPerf, String queryName){
        try {
            this.fileReader = new FileReader(fileName);
            this.senderAuction = eventService.getEventSender("AuctionEvent");
            this.senderPeople = eventService.getEventSender("PersonEvent");
            this.senderBid = eventService.getEventSender("BidEvent");
            this.epEventService = eventService;
            this.registerPerf=registerPerf;
            this.maxEvents = -1;
            this.queryName = queryName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void process(Function<String, Pair<Object, Long>> transformationFunction) {
        // RegisterIng the starting time
        long startTime = System.currentTimeMillis();
        long readTime = 0;
        long startRead = 0;
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;

            /*
            In case of maxEvents=-1, we consume the whole file.
             */
            if(maxEvents==-1) {
                startRead = System.currentTimeMillis();
                line = bufferedReader.readLine();
                readTime+= (System.currentTimeMillis()-startRead);
                while (line != null) {
                    send(transformationFunction.apply(line), parseName(line));
                    startRead = System.currentTimeMillis();
                    line = bufferedReader.readLine();
                    readTime+= (System.currentTimeMillis()-startRead);
                }
            }
            /*
            Else, we stop when we reach the end of the file, or we reach maximum number of events.
             */
            /*else while ((line = bufferedReader.readLine()) != null && counter<maxEvents) {
                send(transformationFunction.apply(line), parseName(line));
            }*/

            // Registering the ending time
            long endTime = System.currentTimeMillis();

            if(registerPerf)
                registerPerformance(endTime - startTime, queryName, readTime);

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
        This function extracts from the file line the name of the event (auction, bid, person)
     */
    private String parseName(String line){
        //split the timestamp from the rest of the string
        String[] valAndTs = line.split(",", 2);
        int i = 0;
        StringBuilder b = new StringBuilder();
        String tuple = valAndTs[1];
        /*----Phase 1: parse the type of tuple (Auction, Person or Bid)----*/
        while(tuple.charAt(i)!= '{'){
            b.append(tuple.charAt(i));
            i++;
        }
        return b.toString();
    }


    private void registerPerformance(double diff, String query){
        double throughput = (double)counter;
        throughput = throughput/diff;

        PerformanceFileBuilder performanceFileBuilder = new PerformanceFileBuilder("src/main/resources/performances.csv", "esper", 1);
        performanceFileBuilder.register(throughput,
                query,  counter, diff);
        performanceFileBuilder.close();
    }

    private void registerPerformance(double diff, String query, double readTime){
        double throughput = (double)counter;
        throughput = throughput/diff;

        PerformanceFileBuilder performanceFileBuilder = new PerformanceFileBuilder("src/main/resources/performances.csv", "esper", 1);
        performanceFileBuilder.register(throughput,
                query,  counter, diff, readTime);
        performanceFileBuilder.close();
    }

    private void send(Pair<Object,Long> eventTimestamp, String eventName){
        EventSender sender;
        if(eventName.equals("Auction")){
            sender = senderAuction;
        }
        else if(eventName.equals("Person")){
            sender = senderPeople;
        }

        else if(eventName.equals("Bid")){
            sender = senderBid;
        }

        else{
            throw new RuntimeException("Wrong event name in file line");
        }
        // Advancing time assuring monotonic advancement
        if(epEventService.getCurrentTime()<eventTimestamp.getSecond())
            epEventService.advanceTime(eventTimestamp.getSecond());

        // Event sending, use sender because more efficient
        sender.sendEvent(eventTimestamp.getFirst());
        counter++;
    }

}
