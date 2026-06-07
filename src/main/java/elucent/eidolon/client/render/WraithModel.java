package elucent.eidolon.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class WraithModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer tail;
    private final ModelRenderer rightArm;
    private final ModelRenderer lowerRightArm;
    private final ModelRenderer rightHand;
    private final ModelRenderer leftArm;
    private final ModelRenderer lowerLeftArm;
    private final ModelRenderer leftHand;

    public WraithModel() {
        textureWidth = 64;
        textureHeight = 64;

        body = new ModelRenderer(this, 0, 0);
        body.addBox(-4.0F, -16.0F, -4.0F, 8, 16, 8);
        body.setTextureOffset(32, 2).addBox(-3.0F, -15.0F, -3.0F, 6, 15, 6);
        body.setRotationPoint(0.0F, 8.0F, 0.0F);

        tail = new ModelRenderer(this, 0, 24);
        tail.addBox(-4.0F, 0.0F, 0.0F, 8, 16, 8);
        tail.setTextureOffset(32, 26).addBox(-3.0F, 0.25F, 1.0F, 6, 15, 6);
        tail.setRotationPoint(0.0F, 0.0F, -4.0F);
        tail.rotateAngleX = 0.2618F;
        body.addChild(tail);

        rightArm = new ModelRenderer(this, 0, 48);
        rightArm.addBox(-1.5F, -1.5F, -1.0F, 3, 6, 3);
        rightArm.setRotationPoint(-5.5F, -6.5F, 0.0F);
        rightArm.rotateAngleX = -1.0472F;
        rightArm.rotateAngleZ = -0.1745F;
        body.addChild(rightArm);

        lowerRightArm = new ModelRenderer(this, 12, 48);
        lowerRightArm.addBox(-1.5F, -2.5F, -2.65F, 3, 6, 3);
        lowerRightArm.setRotationPoint(0.0F, 6.0F, 0.0F);
        lowerRightArm.rotateAngleX = -0.7854F;
        rightArm.addChild(lowerRightArm);

        rightHand = new ModelRenderer(this, 25, 48);
        rightHand.addBox(-1.5F, -1.0F, -1.5F, 3, 3, 3);
        rightHand.setRotationPoint(0.0F, 2.5F, 0.0F);
        rightHand.rotateAngleX = 0.3927F;
        lowerRightArm.addChild(rightHand);

        leftArm = new ModelRenderer(this, 0, 48);
        leftArm.mirror = true;
        leftArm.addBox(-1.5F, -1.5F, -1.0F, 3, 6, 3);
        leftArm.setRotationPoint(5.5F, -6.5F, 0.0F);
        leftArm.rotateAngleX = -1.0472F;
        leftArm.rotateAngleZ = 0.1745F;
        body.addChild(leftArm);

        lowerLeftArm = new ModelRenderer(this, 12, 48);
        lowerLeftArm.mirror = true;
        lowerLeftArm.addBox(-1.5F, -2.5F, -2.65F, 3, 6, 3);
        lowerLeftArm.setRotationPoint(0.0F, 6.0F, 0.0F);
        lowerLeftArm.rotateAngleX = -0.7854F;
        leftArm.addChild(lowerLeftArm);

        leftHand = new ModelRenderer(this, 25, 48);
        leftHand.mirror = true;
        leftHand.addBox(-1.5F, -1.0F, -1.5F, 3, 3, 3);
        leftHand.setRotationPoint(0.0F, 2.5F, 0.0F);
        leftHand.rotateAngleX = 0.3927F;
        lowerLeftArm.addChild(leftHand);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        float horizontalSpeed = MathHelper.sqrt(entityIn.motionX * entityIn.motionX + entityIn.motionZ * entityIn.motionZ);
        float lean = MathHelper.clamp(15.0F * horizontalSpeed / 0.3F, -15.0F, 15.0F);
        body.rotationPointY = 8.0F + 1.5F * MathHelper.sin(ageInTicks / 20.0F * (float) Math.PI);
        body.rotateAngleX = lean * 0.017453292F;
        rightArm.rotateAngleX = -1.05F + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.25F * limbSwingAmount;
        leftArm.rotateAngleX = -1.05F + MathHelper.cos(limbSwing * 0.6662F) * 0.25F * limbSwingAmount;
    }
}
