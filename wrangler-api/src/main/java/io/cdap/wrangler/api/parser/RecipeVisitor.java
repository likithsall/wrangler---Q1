@Override
public Directive visitValue(DirectivesParser.ValueContext ctx) {
    if (ctx.BYTE_SIZE() != null) {
        return new ByteSize(ctx.BYTE_SIZE().getText());
    }
    if (ctx.TIME_DURATION() != null) {
        return new TimeDuration(ctx.TIME_DURATION().getText());
    }
    // Existing implementation
}
