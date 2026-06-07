package elucent.eidolon.item.curio;

import baubles.api.BaubleType;
import baubles.api.cap.BaubleItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EidolonBaubleItem extends BaubleItem {
    private final String tooltipKey;

    public EidolonBaubleItem(BaubleType baubleType, String tooltipKey) {
        super(baubleType);
        this.tooltipKey = tooltipKey;
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        String slot = translate(slotKey(getBaubleType(stack)));
        tooltip.add(TextFormatting.DARK_PURPLE + String.format(translate("tooltip.eidolon.bauble_slot"), slot));
        addTranslatedLine(tooltip, tooltipKey, TextFormatting.GRAY);
    }

    protected void addTranslatedLine(List<String> tooltip, String key, TextFormatting color) {
        String line = translate(key);
        if (!line.equals(key)) {
            tooltip.add(color + line);
        }
    }

    protected static String translate(String key) {
        return I18n.translateToLocal(key);
    }

    private static String slotKey(BaubleType type) {
        if (type == BaubleType.AMULET) {
            return "tooltip.eidolon.bauble_slot.amulet";
        }
        if (type == BaubleType.RING) {
            return "tooltip.eidolon.bauble_slot.ring";
        }
        if (type == BaubleType.BELT) {
            return "tooltip.eidolon.bauble_slot.belt";
        }
        if (type == BaubleType.HEAD) {
            return "tooltip.eidolon.bauble_slot.head";
        }
        if (type == BaubleType.BODY) {
            return "tooltip.eidolon.bauble_slot.body";
        }
        if (type == BaubleType.CHARM) {
            return "tooltip.eidolon.bauble_slot.charm";
        }
        return "tooltip.eidolon.bauble_slot.trinket";
    }
}
