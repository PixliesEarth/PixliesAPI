package net.pixlies.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author MickMMars
 */
@Data
@AllArgsConstructor
public class Receipt {

    private double amount;
    private boolean lost;
    private String reason;
    private long time;

    /**
     * @param string serialized receipt
     * @return Receipt object
     */
    public static Receipt fromString(String string) {
        String[] split = string.split(";");
        return new Receipt(Double.parseDouble(split[0]), Boolean.parseBoolean(split[1]), split[2], Long.parseLong(split[3]));
    }

    /**
     * @param amount transaction amount
     * @param lost withdraw = true | deposit = false
     * @param reason reason e.g. "Tax from nation"
     * @return serialized receipt.
     */
    public static String create(double amount, boolean lost, String reason) {
        return amount + ";" + lost + ";" + reason + ";" + System.currentTimeMillis();
    }

    /**
     * @param millis transaction date
     * @return time as {@link LocalDateTime} object
     */
    public static LocalDateTime millsToLocalDateTime(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return date;
    }

}