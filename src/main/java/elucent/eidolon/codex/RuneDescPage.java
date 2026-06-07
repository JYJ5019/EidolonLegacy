package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

public class RuneDescPage extends Page {
    public RuneDescPage(String textKey, ItemStack... displayStacks) {
        super(Kind.RUNE, textKey, textKey, stacks(displayStacks), null);
    }
}
