import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A custom token class to represent time durations with units (NS, MS, S, M, H, D)
 */
public class TimeDuration extends CommonToken {
    // Conversion logic with unit mapping to milliseconds
    private static final Map<String, Long> UNIT_FACTORS = new HashMap<>();
    
    static {
        UNIT_FACTORS.put("NS", 0L);             // Nanoseconds (rounded to 0ms)
        UNIT_FACTORS.put("MS", 1L);             // Milliseconds
        UNIT_FACTORS.put("S", 1_000L);          // Seconds
        UNIT_FACTORS.put("M", 60 * 1_000L);     // Minutes
        UNIT_FACTORS.put("H", 60 * 60 * 1_000L);// Hours
        UNIT_FACTORS.put("D", 24 * 60 * 60 * 1_000L); // Days
    }
    
    private final double numericValue;
    private final String unit;
    private final long milliseconds;
    
    /**
     * Constructs a TimeDuration token from an existing token
     * 
     * @param token The original token from the lexer
     */
    public TimeDuration(Token token) {
        super(token);
        String text = token.getText().trim();
        
        // Extract numeric value and unit using regex
        Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(NS|MS|S|M|H|D)");
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.matches()) {
            this.numericValue = Double.parseDouble(matcher.group(1));
            this.unit = matcher.group(2);
            
            Long factor = UNIT_FACTORS.get(unit);
            if (factor == null) {
                throw new IllegalArgumentException("Unknown time unit: " + unit);
            }
            
            this.milliseconds = Math.round(numericValue * factor);
        } else {
            throw new IllegalArgumentException("Invalid time duration format: " + text);
        }
    }
    
    /**
     * Creates a TimeDuration token with the specified token type and text
     * 
     * @param type The token type
     * @param text The token text
     */
    public TimeDuration(int type, String text) {
        super(type, text);
        String cleanText = text.trim();
        
        // Extract numeric value and unit using regex
        Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(NS|MS|S|M|H|D)");
        Matcher matcher = pattern.matcher(cleanText);
        
        if (matcher.matches()) {
            this.numericValue = Double.parseDouble(matcher.group(1));
            this.unit = matcher.group(2);
            
            Long factor = UNIT_FACTORS.get(unit);
            if (factor == null) {
                throw new IllegalArgumentException("Unknown time unit: " + unit);
            }
            
            this.milliseconds = Math.round(numericValue * factor);
        } else {
            throw new IllegalArgumentException("Invalid time duration format: " + text);
        }
    }
    
    /**
     * Gets the numeric part of the time duration
     * 
     * @return The numeric value
     */
    public double getNumericValue() {
        return numericValue;
    }
    
    /**
     * Gets the unit part of the time duration
     * 
     * @return The unit (NS, MS, S, M, H, D)
     */
    public String getUnit() {
        return unit;
    }
    
    /**
     * Gets the time duration converted to milliseconds
     * 
     * @return The duration in milliseconds
     */
    public long getMilliseconds() {
        return milliseconds;
    }
    
    /**
     * Gets the time duration in seconds
     * 
     * @return The duration in seconds
     */
    public double getSeconds() {
        return milliseconds / 1000.0;
    }
    
    /**
     * Gets the time duration in minutes
     * 
     * @return The duration in minutes
     */
    public double getMinutes() {
        return milliseconds / (60 * 1000.0);
    }
    
    /**
     * Gets the time duration in hours
     * 
     * @return The duration in hours
     */
    public double getHours() {
        return milliseconds / (60 * 60 * 1000.0);
    }
    
    /**
     * Gets the time duration in days
     * 
     * @return The duration in days
     */
    public double getDays() {
        return milliseconds / (24 * 60 * 60 * 1000.0);
    }
    
    /**
     * Convert to a specific unit
     * 
     * @param targetUnit The unit to convert to (NS, MS, S, M, H, D)
     * @return The duration in the target unit
     */
    public double convertTo(String targetUnit) {
        Long targetFactor = UNIT_FACTORS.get(targetUnit);
        if (targetFactor == null) {
            throw new IllegalArgumentException("Unknown time unit: " + targetUnit);
        }
        
        if (targetFactor == 0) {
            // Special case for nanoseconds
            return milliseconds * 1_000_000;
        }
        
        return (double) milliseconds / targetFactor;
    }
    
    @Override
    public String toString() {
        return numericValue + " " + unit + " (" + milliseconds + " ms)";
    }
}
