package elucent.eidolon.item;

import elucent.eidolon.Reference;
import elucent.eidolon.client.render.armor.SilverArmorModel;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class SilverArmorItem extends EidolonArmorItem {
    @SideOnly(Side.CLIENT)
    private SilverArmorModel armorModel;

    public SilverArmorItem(ArmorMaterial material, EntityEquipmentSlot slot) {
        super(material, slot, "silver", null);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return Reference.MOD_ID + ":textures/entity/silver_armor.png";
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
            armorModel = new SilverArmorModel();
        }
        armorModel.setModelAttributes(defaultModel);
        armorModel.setArmorSlot(armorSlot);
        return armorModel;
    }
}
