package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Page {
    public enum Kind {
        TITLE,
        TEXT,
        CRAFTING,
        SMELTING,
        WORKTABLE,
        CRUCIBLE,
        RITUAL,
        INDEX,
        LIST,
        ENTITY,
        SIGN,
        RUNE,
        CHANT
    }

    private final Kind kind;
    private final String titleKey;
    private final String textKey;
    private final List<ItemStack> displayStacks;
    private final List<String> entries;

    public Page(Kind kind, String titleKey, String textKey, List<ItemStack> displayStacks, List<String> entries) {
        this.kind = kind;
        this.titleKey = titleKey;
        this.textKey = textKey;
        this.displayStacks = copyStacks(displayStacks);
        this.entries = entries == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public Kind getKind() {
        return kind;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public String getTextKey() {
        return textKey;
    }

    public List<ItemStack> getDisplayStacks() {
        return copyStacks(displayStacks);
    }

    public List<String> getEntries() {
        return entries;
    }

    protected static List<ItemStack> stacks(ItemStack... stacks) {
        List<ItemStack> result = new ArrayList<>();
        if (stacks != null) {
            for (ItemStack stack : stacks) {
                if (stack != null && !stack.isEmpty()) {
                    result.add(stack.copy());
                }
            }
        }
        return result;
    }

    protected static List<String> entries(String... entries) {
        List<String> result = new ArrayList<>();
        if (entries != null) {
            Collections.addAll(result, entries);
        }
        return result;
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
        if (stacks == null || stacks.isEmpty()) {
            return Collections.emptyList();
        }
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (stack != null && !stack.isEmpty()) {
                result.add(stack.copy());
            }
        }
        return Collections.unmodifiableList(result);
    }
}
