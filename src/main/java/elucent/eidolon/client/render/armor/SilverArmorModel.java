package elucent.eidolon.client.render.armor;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class SilverArmorModel extends EidolonArmorModel {
    private final ModelRenderer chest;
    private final ModelRenderer rightShoulder;
    private final ModelRenderer leftShoulder;
    private final ModelRenderer codpiece;
    private final ModelRenderer leftBoot;
    private final ModelRenderer rightBoot;
    private final ModelRenderer leftLegging;
    private final ModelRenderer rightLegging;

    public SilverArmorModel() {
        super(64, 64);

        chest = childBox(bipedBody, 16, 16, false, 0.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                -4.0F, -12.0F, -2.0F, 8, 12, 4, 1.0F);
        rightShoulder = childBox(bipedRightArm, 40, 16, true, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                -3.0F, -2.0F, -2.0F, 4, 5, 4, 1.0F);
        leftShoulder = childBox(bipedLeftArm, 40, 16, false, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                -1.0F, -2.0F, -2.0F, 4, 5, 4, 1.0F);
        ModelRenderer helm = childBox(bipedHead, 0, 0, false, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                -4.0F, -8.0F, -4.0F, 8, 8, 8, 1.0F);
        childBox(helm, 6, 41, false, 0.0F, -3.0F, 0.0F, 0.0F, 0.7854F, 0.0F,
                -5.0F, -6.0F, -5.5F, 10, 10, 10, 1.0F);
        leftBoot = childBox(bipedLeftLeg, 0, 22, true, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                -2.0F, 6.0F, -2.0F, 4, 6, 4, 1.0F);
        rightBoot = childBox(bipedRightLeg, 0, 22, false, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                -2.0F, 6.0F, -2.0F, 4, 6, 4, 1.0F);
        leftLegging = childBox(bipedLeftLeg, 0, 32, false, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                -2.0F, 0.0F, -2.0F, 4, 9, 4, 0.5F);
        rightLegging = childBox(bipedRightLeg, 0, 32, true, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                -2.0F, 0.0F, -2.0F, 4, 9, 4, 0.5F);
        codpiece = childBox(bipedBody, 16, 32, false, 0.0F, 19.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                -4.0F, 0.0F, -2.0F, 8, 5, 4, 0.5F);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
        bipedHead.showModel = isArmorSlot(EntityEquipmentSlot.HEAD);
        bipedHeadwear.showModel = false;
        bipedBody.showModel = isArmorSlot(EntityEquipmentSlot.CHEST) || isArmorSlot(EntityEquipmentSlot.LEGS);
        bipedRightArm.showModel = isArmorSlot(EntityEquipmentSlot.CHEST);
        bipedLeftArm.showModel = isArmorSlot(EntityEquipmentSlot.CHEST);
        bipedRightLeg.showModel = isArmorSlot(EntityEquipmentSlot.LEGS) || isArmorSlot(EntityEquipmentSlot.FEET);
        bipedLeftLeg.showModel = isArmorSlot(EntityEquipmentSlot.LEGS) || isArmorSlot(EntityEquipmentSlot.FEET);

        boolean chestSlot = isArmorSlot(EntityEquipmentSlot.CHEST);
        boolean legsSlot = isArmorSlot(EntityEquipmentSlot.LEGS);
        boolean feetSlot = isArmorSlot(EntityEquipmentSlot.FEET);
        chest.showModel = chestSlot;
        rightShoulder.showModel = chestSlot;
        leftShoulder.showModel = chestSlot;
        codpiece.showModel = legsSlot;
        rightLegging.showModel = legsSlot;
        leftLegging.showModel = legsSlot;
        rightBoot.showModel = feetSlot;
        leftBoot.showModel = feetSlot;
    }
}
