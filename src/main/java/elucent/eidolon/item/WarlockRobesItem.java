package elucent.eidolon.item;

import elucent.eidolon.Reference;
import elucent.eidolon.client.render.armor.WarlockArmorModel;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class WarlockRobesItem extends EidolonArmorItem {
    @SideOnly(Side.CLIENT)
    private WarlockArmorModel armorModel;

    public WarlockRobesItem(ArmorMaterial material, EntityEquipmentSlot slot) {
        super(material, slot, "warlock", "tooltip.eidolon.warlock_robes");
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return Reference.MOD_ID + ":textures/entity/warlock_robes.png";
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, EntityEquipmentSlot armorSlot,
                                    ModelBiped defaultModel) {
        if (armorSlot != armorType) {
            return defaultModel;
        }
        if (armorModel == null) {
            armorModel = new WarlockArmorModel();
        }
        armorModel.setModelAttributes(defaultModel);
        armorModel.setArmorSlot(armorSlot);
        return armorModel;
    }
}
