package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

public class EntityPage extends Page {
    public EntityPage(String textKey, ItemStack... displayStacks) {
        super(Kind.ENTITY, textKey, textKey, stacks(displayStacks), null);
    }
}
