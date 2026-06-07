package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

public class TitlePage extends Page {
    public TitlePage(String textKey, ItemStack... displayStacks) {
        super(Kind.TITLE, textKey, textKey, stacks(displayStacks), null);
    }
}
