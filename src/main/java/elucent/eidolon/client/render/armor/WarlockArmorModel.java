package elucent.eidolon.client.render.armor;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.MathHelper;

public class WarlockArmorModel extends EidolonArmorModel {
    private final ModelRenderer leftBoot;
    private final ModelRenderer rightBoot;
    private final ModelRenderer rightSleeve;
    private final ModelRenderer leftSleeve;
    private final ModelRenderer hat;
    private final ModelRenderer cloak;
    private final ModelRenderer backSide;
    private final ModelRenderer leftSide;
    private final ModelRenderer rightSide;

    public WarlockArmorModel() {
        super(64, 128);

        leftBoot = child(bipedLeftLeg, 0, 84, 0.25F, 0.0F, 0.0F, 0.0F, -0.0873F, 0.0F);
        addBox(leftBoot, 0, 84, false, -2.5F, 5.5F, -2.5F, 5, 7, 5);
        addBox(leftBoot, 0, 80, false, -2.5F, 9.5F, -3.5F, 5, 3, 1);
        childBox(leftBoot, 20, 84, false, 0.0F, 5.0F, 0.0F, 0.0F, 0.0F, 0.0873F,
                -3.0F, -1.0F, -3.0F, 6, 2, 6);

        rightBoot = child(bipedRightLeg, 0, 84, -0.25F, 0.0F, 0.0F, 0.0F, 0.0873F, 0.0F);
        addBox(rightBoot, 0, 84, false, -2.5F, 5.5F, -2.5F, 5, 7, 5);
        addBox(rightBoot, 0, 80, false, -2.5F, 9.5F, -3.5F, 5, 3, 1);
        childBox(rightBoot, 20, 84, false, 0.0F, 5.0F, 0.0F, 0.0F, 0.0F, -0.0873F,
                -3.0F, -1.0F, -3.0F, 6, 2, 6);

        rightSleeve = childBox(bipedRightArm, 28, 38, false, 1.0F, -1.0F, 0.0F,
                0.0F, 0.0F, 0.1745F, -4.5F, -2.0F, -2.5F, 5, 5, 5);
        childBox(rightSleeve, 28, 48, false, -1.0F, 3.0F, 0.0F, 0.0F, 0.0873F, 0.0873F,
                -4.5F, -0.5F, -3.0F, 6, 2, 6);

        leftSleeve = childBox(bipedLeftArm, 28, 38, true, -1.0F, -1.0F, 0.0F,
                0.0F, 0.0F, -0.1745F, -0.5F, -2.0F, -2.5F, 5, 5, 5);
        childBox(leftSleeve, 28, 48, true, 1.0F, 3.0F, 0.0F, 0.0F, -0.0873F, -0.0873F,
                -1.5F, -0.5F, -3.0F, 6, 2, 6);

        hat = childBox(bipedHead, 0, 0, false, 0.0F, -6.5F, 0.0F,
                -0.1745F, 0.0F, -0.0873F, -7.0F, -1.0F, -7.0F, 14, 2, 14);
        ModelRenderer hatMid = childBox(hat, 0, 16, false, 0.0F, 0.0F, 0.0F,
                -0.0873F, 0.0F, 0.0873F, -4.5F, -5.75F, -4.5F, 9, 6, 9);
        ModelRenderer hatUpper = childBox(hatMid, 0, 31, false, 0.0F, -5.0F, 0.0F,
                -0.1745F, 0.0F, 0.0436F, -3.0F, -4.0F, -3.0F, 6, 4, 6);
        childBox(hatUpper, 24, 31, false, 0.0F, -3.0F, 0.0F,
                -0.2618F, 0.0F, 0.0873F, -1.5F, -4.5F, -1.5F, 3, 4, 3);

        cloak = child(bipedBody, 0, 41, 2.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        addBox(cloak, 0, 41, false, -6.5F, -12.4F, -2.5F, 9, 15, 5);
        addBox(cloak, 28, 56, false, -7.0F, -12.5F, -3.0F, 10, 8, 6);
        leftSide = childBox(cloak, 0, 64, false, -5.0F, -4.5F, -3.0F, 0.0F, 0.0F, 0.1745F,
                -2.0F, 0.0F, 0.01F, 2, 10, 6);
        rightSide = childBox(cloak, 0, 64, false, 1.0F, -4.5F, -3.0F, 0.0F, 0.0F, -0.1745F,
                0.0F, 0.0F, 0.01F, 2, 10, 6);
        backSide = childBox(cloak, 17, 70, false, -2.0F, -4.5F, 1.0F, 0.1745F, 0.0F, 0.0F,
                -5.01F, 0.0F, 0.0F, 10, 12, 2);
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
        bipedRightLeg.showModel = isArmorSlot(EntityEquipmentSlot.FEET);
        bipedLeftLeg.showModel = isArmorSlot(EntityEquipmentSlot.FEET);

        boolean headSlot = isArmorSlot(EntityEquipmentSlot.HEAD);
        boolean chestSlot = isArmorSlot(EntityEquipmentSlot.CHEST);
        boolean feetSlot = isArmorSlot(EntityEquipmentSlot.FEET);
        hat.showModel = headSlot;
        cloak.showModel = chestSlot;
        rightSleeve.showModel = chestSlot;
        leftSleeve.showModel = chestSlot;
        rightBoot.showModel = feetSlot;
        leftBoot.showModel = feetSlot;

        if (chestSlot) {
            float walk = MathHelper.cos(limbSwing * 0.6662F);
            backSide.rotateAngleX = 0.1745F + MathHelper.abs(walk) * 0.7F * limbSwingAmount;
            leftSide.rotateAngleZ = 0.1745F + MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 0.2F * limbSwingAmount
                    + 0.1F * limbSwingAmount;
            rightSide.rotateAngleZ = -0.1745F - walk * 0.2F * limbSwingAmount - 0.1F * limbSwingAmount;
        }
    }
}
