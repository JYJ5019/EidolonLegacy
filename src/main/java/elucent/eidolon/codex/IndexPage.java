package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

public class IndexPage extends Page {
    public IndexPage(String textKey, ItemStack... displayStacks) {
        super(Kind.INDEX, textKey, textKey, stacks(displayStacks), null);
    }
}
