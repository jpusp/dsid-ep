package model;

public enum Action {
    HELLO("HELLO"),
    LIST_PEERS("LIST_PEERS"),
    GET_PEERS("GET_PEERS"),
    BYE("BYE");

    private final String value;

    Action(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Action fromValue(String value) {
        for (Action type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
