package elucent.eidolon.client.render.armor;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.MathHelper;

public class BonelordArmorModel extends EidolonArmorModel {
    private final ModelRenderer chest;
    private final ModelRenderer cape;
    private final ModelRenderer leftGreave;
    private final ModelRenderer rightGreave;
    private final ModelRenderer rightShoulder;
    private final ModelRenderer leftShoulder;
    private final ModelRenderer helm;

    public BonelordArmorModel() {
        super(128, 64);

        chest = child(bipedBody, 0, 30, 0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        addBox(chest, 0, 30, false, -5.5F, -24.0F, -4.5F, 11, 6, 8);
        addBox(chest, 38, 30, false, -1.5F, -24.5F, 2.0F, 3, 11, 3);
        addBox(chest, 0, 44, false, -5.0F, -18.0F, -3.25F, 10, 4, 6);
        ModelRenderer hood = childBox(chest, 38, 44, false, 0.0F, -28.0F, 5.0F,
                -0.2618F, 0.0F, 0.0F, -6.5F, 0.0F, 0.0F, 13, 4, 4);
        cape = child(hood, 68, 9, 0.0F, 4.0F, 0.0F, 0.3927F, 0.0F, 0.0F);
        addBox(cape, 68, 9, false, -6.0F, 0.0F, 0.0F, 12, 16, 2);
        addBox(cape, 52, 9, false, 2.0F, 16.0F, 0.0F, 4, 2, 2);
        addBox(cape, 52, 9, true, -6.0F, 16.0F, 0.0F, 4, 2, 2);
        childBox(cape, 50, 29, false, -0.5F, 14.75F, 1.0F, 0.0F, 0.0F, 0.5236F,
                0.0F, -1.0F, -1.5F, 8, 3, 3);
        childBox(cape, 50, 29, true, 0.5F, 14.75F, 1.0F, 0.0F, 0.0F, -0.5236F,
                -8.0F, -1.0F, -1.51F, 8, 3, 3);

        rib(chest, false, 0.5F, -15.0F, 2.0F, 0.0F, 0.2618F, 0.2618F);
        rib(chest, false, 0.5F, -14.25F, 2.0F, 0.0F, 0.2618F, -0.1309F);
        rib(chest, false, 0.5F, -13.5F, 2.0F, 0.0F, 0.2618F, -0.5236F);
        rib(chest, true, -0.5F, -15.0F, 2.0F, 0.0F, -0.2618F, -0.2618F);
        rib(chest, true, -0.5F, -14.25F, 2.0F, 0.0F, -0.2618F, 0.1309F);
        rib(chest, true, -0.5F, -13.5F, 2.0F, 0.0F, -0.2618F, 0.5236F);
        childBox(chest, 30, 30, false, 0.0F, -13.0F, 5.0F, -0.1745F, 0.0F, 0.0F,
                -1.0F, 0.0F, -2.5F, 2, 2, 2);

        leftGreave = childBox(bipedLeftLeg, 72, 27, false, -2.0F, 2.0F, -3.0F,
                0.0F, -0.0873F, 0.0F, 0.0F, -0.5F, 0.0F, 4, 8, 2);
        childBox(leftGreave, 84, 27, false, 4.5F, -2.0F, -0.5F, 0.0F, -0.0873F, 0.0F,
                -4.5F, -0.5F, 0.0F, 5, 4, 3);
        rightGreave = childBox(bipedRightLeg, 72, 27, true, 2.0F, 2.0F, -3.0F,
                0.0F, 0.0873F, 0.0F, -4.0F, -0.5F, 0.0F, 4, 8, 2);
        childBox(rightGreave, 84, 27, true, -4.5F, -2.0F, -0.5F, 0.0F, 0.0873F, 0.0F,
                -0.5F, -0.5F, 0.0F, 5, 4, 3);

        rightShoulder = childBox(bipedRightArm, 44, 17, true, -4.5F, -2.0F, 0.0F,
                0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.0F, 6, 6, 6);
        leftShoulder = childBox(bipedLeftArm, 44, 17, false, 4.5F, -2.0F, 0.0F,
                0.0F, 0.0F, 0.0F, -6.0F, 0.0F, -3.0F, 6, 6, 6);

        helm = child(bipedHead, 0, 16, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        addBox(helm, 0, 16, false, -5.5F, -3.0F, -5.5F, 11, 3, 11);
        addBox(helm, 0, 0, false, -4.5F, -9.0F, -4.5F, 9, 7, 9, 0.01F);
        childBox(helm, 33, 16, false, -0.5F, -3.5F, -4.5F, -0.2618F, 0.0F, 0.0F,
                -2.0F, 0.0F, -1.5F, 5, 4, 3);
        childBox(helm, 27, 0, true, -4.5F, -3.0F, -4.0F, 0.0F, 0.5236F, 0.5236F,
                -5.0F, -2.0F, 0.0F, 5, 2, 2);
        childBox(helm, 27, 4, true, -4.5F, -5.0F, 1.0F, 0.0F, 1.0472F, 0.7854F,
                -7.0F, -2.0F, 0.0F, 7, 2, 2);
        childBox(helm, 27, 0, false, 4.5F, -3.0F, -4.0F, 0.0F, -0.5236F, -0.5236F,
                0.0F, -2.0F, 0.0F, 5, 2, 2);
        childBox(helm, 27, 4, false, 4.5F, -5.0F, 1.0F, 0.0F, -1.0472F, -0.7854F,
                0.0F, -2.0F, 0.0F, 7, 2, 2);
    }

    private void rib(ModelRenderer chest, boolean mirror, float x, float y, float z,
                     float rotX, float rotY, float rotZ) {
        ModelRenderer rib = child(chest, 36, 8, x, y, z, rotX, rotY, rotZ);
        if (mirror) {
            addBox(rib, 36, 8, true, 0.0F, -2.0F, -1.0F, 5, 2, 2);
            addBox(rib, 45, 0, true, 5.0F, -2.0F, -4.0F, 2, 2, 5);
        } else {
            addBox(rib, 36, 8, false, -5.0F, -2.0F, -1.0F, 5, 2, 2);
            addBox(rib, 45, 0, false, -7.0F, -2.0F, -4.0F, 2, 2, 5);
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
        bipedHead.showModel = isArmorSlot(EntityEquipmentSlot.HEAD);
        bipedHeadwear.showModel = false;
        bipedBody.showModel = isArmorSlot(EntityEquipmentSlot.CHEST);
        bipedRightArm.showModel = isArmorSlot(EntityEquipmentSlot.CHEST);
        bipedLeftArm.showModel = isArmorSlot(EntityEquipmentSlot.CHEST);
        bipedRightLeg.showModel = isArmorSlot(EntityEquipmentSlot.LEGS);
        bipedLeftLeg.showModel = isArmorSlot(EntityEquipmentSlot.LEGS);

        boolean headSlot = isArmorSlot(EntityEquipmentSlot.HEAD);
        boolean chestSlot = isArmorSlot(EntityEquipmentSlot.CHEST);
        boolean legsSlot = isArmorSlot(EntityEquipmentSlot.LEGS);
        helm.showModel = headSlot;
        chest.showModel = chestSlot;
        rightShoulder.showModel = chestSlot;
        leftShoulder.showModel = chestSlot;
        rightGreave.showModel = legsSlot;
        leftGreave.showModel = legsSlot;

        if (chestSlot) {
            cape.rotateAngleX = 0.3927F + MathHelper.abs(MathHelper.cos(limbSwing * 0.6662F) * 0.4F * limbSwingAmount);
        }
    }
}
