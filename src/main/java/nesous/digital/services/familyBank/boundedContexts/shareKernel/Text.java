package nesous.digital.services.familyBank.boundedContexts.shareKernel;

public record Text(String value){


    private static final Text SPACE = new Text(" ");

    public boolean isNullOrWhitespace() {
        return value == null || value.trim().isEmpty();
    }

    public boolean isNull() {
        return value == null;
    }

    public Text add(Text other) {
        return new Text(value.concat(other.value));
    }

    public Text addSpace() {
        return add(SPACE);
    }

    @Override
    public String toString() {
        return value();
    }
}
