package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

public class RitualPage extends Page {
    public RitualPage(String textKey, ItemStack... displayStacks) {
        super(Kind.RITUAL, textKey, textKey, stacks(displayStacks), null);
    }
}
