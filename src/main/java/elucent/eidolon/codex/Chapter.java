package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chapter {
    private final String id;
    private final String titleKey;
    private final ItemStack icon;
    private final List<Page> pages;

    public Chapter(String id, String titleKey, ItemStack icon, Page... pages) {
        this.id = id;
        this.titleKey = titleKey;
        this.icon = icon == null ? ItemStack.EMPTY : icon.copy();
        List<Page> pageList = new ArrayList<>();
        if (pages != null) {
            Collections.addAll(pageList, pages);
        }
        this.pages = Collections.unmodifiableList(pageList);
    }

    public String getId() {
        return id;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public ItemStack getIcon() {
        return icon.copy();
    }

    public List<Page> getPages() {
        return pages;
    }
}
