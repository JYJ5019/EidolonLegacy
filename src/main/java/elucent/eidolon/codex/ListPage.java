package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

public class ListPage extends Page {
    public ListPage(String textKey, String... entries) {
        super(Kind.LIST, textKey, textKey, null, entries(entries));
    }

    public ListPage(String textKey, ItemStack[] displayStacks, String... entries) {
        super(Kind.LIST, textKey, textKey, stacks(displayStacks), entries(entries));
    }
}
