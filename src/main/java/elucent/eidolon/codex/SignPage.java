package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

public class SignPage extends Page {
    public SignPage(String textKey, ItemStack... displayStacks) {
        super(Kind.SIGN, textKey, textKey, stacks(displayStacks), null);
    }
}
