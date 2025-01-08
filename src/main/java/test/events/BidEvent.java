package test.events;

import java.io.Serializable;
import java.time.Instant;

public class BidEvent implements Serializable {

    public long timestamp;
    public long key;
    /*  Here we have the fields of the bid */
    public long auction;
    public long bidder;
    public long price;
    public String channel;
    public String url;
    public long dateTime;
    public String extra;

    public BidEvent(long timestamp, long key, long auction, long bidder, long price, String channel, String url, long dateTime, String extras) {
        this.timestamp = timestamp;
        this.key = key;
        this.auction = auction;
        this.bidder = bidder;
        this.price = price;
        this.channel = channel;
        this.url = url;
        this.dateTime = dateTime;
        this.extra = extras;
    }
    public BidEvent(String value, long ts) {
        this.timestamp = ts;
        String[] fields = value.split(", ");
        for (String field : fields) {
            String[] keyval = field.split("=");
            if (keyval[1].charAt(0) == '\'') {
                //remove the ' delimiters from the string
                try {
                    BidEvent.class.getField(keyval[0]).set(this, keyval[1].substring(1, keyval[1].length() - 1));
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            } else if (!keyval[0].equals("dateTime") && !keyval[0].equals("expires")) {
                try {
                    BidEvent.class.getField(keyval[0]).setLong(this, Long.parseLong(keyval[1]));
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    BidEvent.class.getField(keyval[0]).setLong(this, Instant.parse(keyval[1]).toEpochMilli());
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getKey() {
        return key;
    }

    public long getAuction() {
        return auction;
    }

    public long getBidder() {
        return bidder;
    }

    public long getPrice() {
        return price;
    }

    public String getChannel() {
        return channel;
    }

    public String getUrl() {
        return url;
    }

    public long getDateTime() {
        return dateTime;
    }

    public String getExtras() {
        return extra;
    }
    @Override
    public String toString(){
        return "auction: "+ this.auction+" ,bidder: "+this.bidder+" ,price: "+this.price+" ,channel: "+this.channel+"\n";
    }
}
