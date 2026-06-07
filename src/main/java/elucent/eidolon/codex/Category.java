package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Category {
    private final String id;
    private final String titleKey;
    private final ItemStack icon;
    private final int color;
    private final List<Chapter> chapters;

    public Category(String id, ItemStack icon, int color, Chapter... chapters) {
        this.id = id;
        this.titleKey = "eidolon.codex.category." + id;
        this.icon = icon == null ? ItemStack.EMPTY : icon.copy();
        this.color = color;
        List<Chapter> chapterList = new ArrayList<>();
        if (chapters != null) {
            Collections.addAll(chapterList, chapters);
        }
        this.chapters = Collections.unmodifiableList(chapterList);
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

    public int getColor() {
        return color;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }
}
