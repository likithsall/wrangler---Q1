import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A custom token class to represent byte sizes with units (B, KB, MB, etc.)
 */
public class ByteSize extends CommonToken {
    // Conversion logic with unit mapping
    private static final Map<String, Long> UNIT_FACTORS = new HashMap<>();
    
    static {
        // SI units (decimal)
        UNIT_FACTORS.put("B", 1L);
        UNIT_FACTORS.put("KB", 1_000L);
        UNIT_FACTORS.put("MB", 1_000_000L);
        UNIT_FACTORS.put("GB", 1_000_000_000L);
        UNIT_FACTORS.put("TB", 1_000_000_000_000L);
        
        // Binary units
        UNIT_FACTORS.put("KIB", 1_024L);
        UNIT_FACTORS.put("MIB", 1_048_576L);
        UNIT_FACTORS.put("GIB", 1_073_741_824L);
        UNIT_FACTORS.put("TIB", 1_099_511_627_776L);
    }
    
    private final double numericValue;
    private final String unit;
    private final long bytes;
    
    /**
     * Constructs a ByteSize token from an existing token
     * 
     * @param token The original token from the lexer
     */
    public ByteSize(Token token) {
        super(token);
        String text = token.getText().trim();
        
        // Extract numeric value and unit using regex
        Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(B|KB|MB|GB|TB|KIB|MIB|GIB|TIB)");
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.matches()) {
            this.numericValue = Double.parseDouble(matcher.group(1));
            this.unit = matcher.group(2);
            
            Long factor = UNIT_FACTORS.get(unit);
            if (factor == null) {
                throw new IllegalArgumentException("Unknown byte unit: " + unit);
            }
            
            this.bytes = Math.round(numericValue * factor);
        } else {
            throw new IllegalArgumentException("Invalid byte size format: " + text);
        }
    }
    
    /**
     * Creates a ByteSize token with the specified token type and text
     * 
     * @param type The token type
     * @param text The token text
     */
    public ByteSize(int type, String text) {
        super(type, text);
        String cleanText = text.trim();
        
        // Extract numeric value and unit using regex
        Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(B|KB|MB|GB|TB|KIB|MIB|GIB|TIB)");
        Matcher matcher = pattern.matcher(cleanText);
        
        if (matcher.matches()) {
            this.numericValue = Double.parseDouble(matcher.group(1));
            this.unit = matcher.group(2);
            
            Long factor = UNIT_FACTORS.get(unit);
            if (factor == null) {
                throw new IllegalArgumentException("Unknown byte unit: " + unit);
            }
            
            this.bytes = Math.round(numericValue * factor);
        } else {
            throw new IllegalArgumentException("Invalid byte size format: " + text);
        }
    }
    
    /**
     * Gets the numeric part of the byte size
     * 
     * @return The numeric value
     */
    public double getNumericValue() {
        return numericValue;
    }
    
    /**
     * Gets the unit part of the byte size
     * 
     * @return The unit (B, KB, MB, etc.)
     */
    public String getUnit() {
        return unit;
    }
    
    /**
     * Gets the byte size converted to bytes
     * 
     * @return The size in bytes
     */
    public long getBytes() {
        return bytes;
    }
    
    /**
     * Convert to a specific unit
     * 
     * @param targetUnit The unit to convert to (B, KB, MB, etc.)
     * @return The size in the target unit
     */
    public double convertTo(String targetUnit) {
        Long targetFactor = UNIT_FACTORS.get(targetUnit);
        if (targetFactor == null) {
            throw new IllegalArgumentException("Unknown byte unit: " + targetUnit);
        }
        
        return (double) bytes / targetFactor;
    }
    
    @Override
    public String toString() {
        return numericValue + " " + unit + " (" + bytes + " bytes)";
    }
}
