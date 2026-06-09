package elucent.eidolon.item;

import elucent.eidolon.Reference;
import elucent.eidolon.client.render.armor.BonelordArmorModel;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BonelordArmorItem extends EidolonArmorItem {
    @SideOnly(Side.CLIENT)
    private BonelordArmorModel armorModel;

    public BonelordArmorItem(ArmorMaterial material, EntityEquipmentSlot slot) {
        super(material, slot, "bonelord", "tooltip.eidolon.bonelord_armor");
    }

    public int getPersistentEtherealHealth() {
        return armorType == EntityEquipmentSlot.CHEST ? 20 : 10;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return Reference.MOD_ID + ":textures/entity/bonelord_armor.png";
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
            armorModel = new BonelordArmorModel();
        }
        armorModel.setModelAttributes(defaultModel);
        armorModel.setArmorSlot(armorSlot);
        return armorModel;
    }
}
