package test.events;

import java.io.Serializable;
import java.time.Instant;

public class PersonEvent implements Serializable {

    public long timestamp;
    public long key;
    /*  Here we have the fields of the person  */

    public long id;
    public String name;
    public String emailAddress;
    public String creditCard;
    public String city;
    public String state;
    public long dateTime;
    public String extra;

    public PersonEvent(long timestamp, long key, long id, String name, String emailAddress, String creditCard, String city, String state, long dateTime, String extra) {
        this.timestamp = timestamp;
        this.key = key;
        this.id = id;
        this.name = name;
        this.emailAddress = emailAddress;
        this.creditCard = creditCard;
        this.city = city;
        this.state = state;
        this.dateTime = dateTime;
        this.extra = extra;
    }

    public PersonEvent(String value, long ts) {
        this.timestamp = ts;
        String[] fields = value.split(", ");
        for (String field : fields) {
            String[] keyval = field.split("=");
            if (keyval[1].charAt(0) == '\'') {
                //remove the ' delimiters from the string
                try {
                    PersonEvent.class.getField(keyval[0]).set(this, keyval[1].substring(1, keyval[1].length() - 1));
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            } else if (!keyval[0].equals("dateTime") && !keyval[0].equals("expires")) {
                try {
                    PersonEvent.class.getField(keyval[0]).setLong(this, Long.parseLong(keyval[1]));
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    PersonEvent.class.getField(keyval[0]).setLong(this, Instant.parse(keyval[1]).toEpochMilli());
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

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public long getDateTime() {
        return dateTime;
    }

    public String getExtra() {
        return extra;
    }
}
