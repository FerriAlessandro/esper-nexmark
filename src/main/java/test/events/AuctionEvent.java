package test.events;

import java.io.Serializable;
import java.time.Instant;

public class AuctionEvent implements Serializable {
    public long timestamp;
    public long key;
    /*  Here we have the fields of the auction  */
    public long id;
    public String itemName;
    public String description;
    public long initialBid;
    public long reserve;
    public long dateTime;
    public long expires;
    public long seller;
    public long category;
    public String extra;

    public AuctionEvent(long timestamp, long key, long id, String itemName,
                        String description, long initialBid, long reserve,
                        long dateTime, long expires, long seller, long category, String extra) {
        this.timestamp = timestamp;
        this.key = key;
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.initialBid = initialBid;
        this.reserve = reserve;
        this.dateTime = dateTime;
        this.expires = expires;
        this.seller = seller;
        this.category = category;
        this.extra = extra;
    }
    public AuctionEvent(String value, long ts){
        this.timestamp = ts;
        String[] fields = value.split(", ");
        for(String field : fields){
            String[] keyval = field.split("=");
            if(keyval[1].charAt(0) == '\''){
                //remove the ' delimiters from the string
                try {
                    AuctionEvent.class.getField(keyval[0]).set(this, keyval[1].substring(1, keyval[1].length()-1));
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (!keyval[0].equals("dateTime") && !keyval[0].equals("expires")){
                try {
                    AuctionEvent.class.getField(keyval[0]).setLong(this, Long.parseLong(keyval[1]));
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                try {
                    AuctionEvent.class.getField(keyval[0]).setLong(this, Instant.parse(keyval[1]).toEpochMilli());
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        /*for(String field : fields){
            String[] keyval = field.split("=");
            String colName = keyval[0];

            if(colName.equals("id")){
                this.id = Long.parseLong(keyval[1]);
            }
            else if(colName.equals("itemName")){
                this.itemName = keyval[1].substring(1, keyval[1].length()-1); //remove the ' ' from the string
            }
            else if(colName.equals("description")){
                this.description = keyval[1].substring(1, keyval[1].length()-1); //remove the ' ' from the string
            }
            else if(colName.equals("initialBid")){
                this.initialBid = Long.parseLong(keyval[1]);
            }
            else if(colName.equals("reserve")){
                this.reserve = Long.parseLong(keyval[1]);
            }
            else if(colName.equals("dateTime")){
                this.dateTime = Instant.parse(keyval[1]).toEpochMilli();
            }
            else if(colName.equals("expires")){
                this.expires = Instant.parse(keyval[1]).toEpochMilli();
            }
            else if(colName.equals("seller")){
                this.seller = Long.parseLong(keyval[1]);
            }
            else if(colName.equals("category")){
                this.category = Long.parseLong(keyval[1]);
            }
            else if(colName.equals("extra")){
                this.extra = keyval[1].substring(1, keyval[1].length()-1);
            }*/

    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getKey() {
        return key;
    }

    public long getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public long getInitialBid() {
        return initialBid;
    }

    public long getReserve() {
        return reserve;
    }

    public long getDateTime() {
        return dateTime;
    }

    public long getExpires() {
        return expires;
    }

    public long getSeller() {
        return seller;
    }

    public long getCategory() {
        return category;
    }

    public String getExtra() {
        return extra;
    }

}
