package symbolics.division.spirit.vector.logic.input;

public enum Input {
    JUMP("key.jump"), CROUCH("key.sneak"), SPRINT("key.sprint");

    public String key;
    Input(String key) {
        this.key = key;
    }
}
