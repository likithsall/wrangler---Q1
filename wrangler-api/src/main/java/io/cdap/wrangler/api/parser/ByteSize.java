public class ByteSize extends Token {
    // Conversion logic with unit mapping
    private static final Map<String, Long> UNIT_FACTORS = new HashMap<>();
    static {
        UNIT_FACTORS.put("B", 1L);
        UNIT_FACTORS.put("KB", 1000L);
        UNIT_FACTORS.put("MB", 1000_000L);
        // ... other units
    }
    
    public ByteSize(String value) {
        // Parsing logic
    }
}
