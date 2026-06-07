package elucent.eidolon.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ZombieBruteModel extends ModelBase {
    private final ModelRenderer chest;
    private final ModelRenderer head;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;

    public ZombieBruteModel() {
        textureWidth = 96;
        textureHeight = 64;
        chest = new ModelRenderer(this, 20, 16);
        chest.addBox(-6.0F, -15.0F, -3.0F, 12, 15, 6);
        chest.setRotationPoint(0.0F, 9.0F, 0.0F);
        head = new ModelRenderer(this, 0, 37);
        head.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10);
        head.setRotationPoint(0.0F, -15.0F, 0.0F);
        chest.addChild(head);
        rightArm = new ModelRenderer(this, 56, 16);
        rightArm.addBox(-2.5F, 0.0F, -2.5F, 5, 13, 5);
        rightArm.setRotationPoint(-9.0F, -12.0F, 0.0F);
        chest.addChild(rightArm);
        leftArm = new ModelRenderer(this, 56, 16);
        leftArm.mirror = true;
        leftArm.addBox(-2.5F, 0.0F, -2.5F, 5, 13, 5);
        leftArm.setRotationPoint(9.0F, -12.0F, 0.0F);
        chest.addChild(leftArm);
        rightLeg = new ModelRenderer(this, 0, 16);
        rightLeg.addBox(-2.5F, 0.0F, -2.5F, 5, 15, 5);
        rightLeg.setRotationPoint(-3.0F, 0.0F, 0.0F);
        chest.addChild(rightLeg);
        leftLeg = new ModelRenderer(this, 0, 16);
        leftLeg.mirror = true;
        leftLeg.addBox(-2.5F, 0.0F, -2.5F, 5, 15, 5);
        leftLeg.setRotationPoint(3.0F, 0.0F, 0.0F);
        chest.addChild(leftLeg);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        chest.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.2F * limbSwingAmount;
        leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
        rightArm.rotateAngleX = -1.35F;
        leftArm.rotateAngleX = -1.35F;
        rightArm.rotateAngleZ = 0.1F;
        leftArm.rotateAngleZ = -0.1F;
        head.rotateAngleY = netHeadYaw * 0.017453292F;
        head.rotateAngleX = headPitch * 0.017453292F;
    }
}
