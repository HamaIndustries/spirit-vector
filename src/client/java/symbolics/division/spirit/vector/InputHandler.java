package symbolics.division.spirit.vector;

import net.minecraft.client.MinecraftClient;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;
import symbolics.division.spirit.vector.logic.input.Input;
import symbolics.division.spirit.vector.logic.input.InputManager;

public final class InputHandler {
    public static void tick(MinecraftClient client) {
        if (client.player != null && !client.player.isDead() && client.player instanceof ISpiritVectorUser user) {
            user.getSpiritVector().ifPresent(sv -> {
                InputManager input = sv.inputManager();
                input.update(Input.JUMP, client.options.jumpKey.isPressed());
                input.update(Input.CROUCH, client.options.sneakKey.isPressed());
                input.update(Input.SPRINT, client.options.sprintKey.isPressed());
            });
        }
    }
}
