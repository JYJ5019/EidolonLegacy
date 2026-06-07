package elucent.eidolon.item;

import elucent.eidolon.Reference;
import elucent.eidolon.registries.ModCreativeTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EidolonArmorItem extends ItemArmor {
    private final String textureName;
    private final String tooltipKey;

    public EidolonArmorItem(ArmorMaterial material, EntityEquipmentSlot slot, String textureName, String tooltipKey) {
        super(material, 0, slot);
        this.textureName = textureName;
        this.tooltipKey = tooltipKey;
        setCreativeTab(ModCreativeTabs.EIDOLON);
    }

    @Override
    public String getArmorTexture(ItemStack stack, net.minecraft.entity.Entity entity, EntityEquipmentSlot slot, String type) {
        int layer = slot == EntityEquipmentSlot.LEGS ? 2 : 1;
        return Reference.MOD_ID + ":textures/models/armor/" + textureName + "_layer_" + layer + ".png";
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (tooltipKey != null && !tooltipKey.isEmpty()) {
            String line = I18n.translateToLocal(tooltipKey);
            if (!line.equals(tooltipKey)) {
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_PURPLE.toString() + TextFormatting.ITALIC + line);
            }
        }
    }
}
