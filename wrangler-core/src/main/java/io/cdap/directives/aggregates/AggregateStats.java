public class AggregateStats implements Directive {
    @Override
    public UsageDefinition define() {
        return Directive.Usage.def(
            "aggregate-stats",
            "Performs size and time aggregation",
            Arguments.of(
                new Argument("size-col", TokenType.COLUMN_NAME),
                new Argument("time-col", TokenType.COLUMN_NAME),
                new Argument("size-out", TokenType.COLUMN_NAME),
                new Argument("time-out", TokenType.COLUMN_NAME),
                new Argument("unit", TokenType.STRING, true, "MB"),
                new Argument("time-unit", TokenType.STRING, true, "SECONDS")
            )
        );
    }

}
