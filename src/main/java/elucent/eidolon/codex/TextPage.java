package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

public class TextPage extends Page {
    public TextPage(String textKey, ItemStack... displayStacks) {
        super(Kind.TEXT, textKey, textKey, stacks(displayStacks), null);
    }
}
