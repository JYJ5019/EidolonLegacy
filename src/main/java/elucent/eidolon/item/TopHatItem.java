package elucent.eidolon.item;

import elucent.eidolon.Reference;
import elucent.eidolon.client.render.armor.TopHatModel;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class TopHatItem extends EidolonArmorItem {
    @SideOnly(Side.CLIENT)
    private TopHatModel armorModel;

    public TopHatItem(ArmorMaterial material) {
        super(material, EntityEquipmentSlot.HEAD, "top_hat", "lore.eidolon.top_hat");
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return Reference.MOD_ID + ":textures/entity/hat.png";
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
            armorModel = new TopHatModel();
        }
        armorModel.setModelAttributes(defaultModel);
        armorModel.setArmorSlot(armorSlot);
        return armorModel;
    }
}
