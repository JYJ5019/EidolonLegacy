package elucent.eidolon.client.render.armor;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class TopHatModel extends EidolonArmorModel {
    public TopHatModel() {
        super(64, 32);
        ModelRenderer hat = child(bipedHead, 0, 0, 0.0F, -7.0F, 0.0F, -0.0873F, 0.0F, 0.0F);
        addBox(hat, 0, 0, false, -5.0F, -2.0F, -5.0F, 10, 2, 10);
        addBox(hat, 0, 12, false, -4.0F, -12.0F, -4.0F, 8, 10, 8);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
        bipedHead.showModel = isArmorSlot(EntityEquipmentSlot.HEAD);
        bipedHeadwear.showModel = false;
        bipedBody.showModel = false;
        bipedRightArm.showModel = false;
        bipedLeftArm.showModel = false;
        bipedRightLeg.showModel = false;
        bipedLeftLeg.showModel = false;
    }
}
