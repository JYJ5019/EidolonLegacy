package elucent.eidolon.client.render;

import elucent.eidolon.entity.RavenEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class RavenModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer foldedWings;
    private final ModelRenderer tailMid;
    private final ModelRenderer rightTail;
    private final ModelRenderer leftTail;
    private final ModelRenderer rightWing;
    private final ModelRenderer leftWing;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;

    public RavenModel() {
        textureWidth = 32;
        textureHeight = 32;
        body = new ModelRenderer(this, 1, 6);
        body.addBox(-1.5F, -3.0F, -2.5F, 3, 3, 5);
        body.setRotationPoint(0.0F, 21.0F, 0.0F);
        body.rotateAngleX = -0.45F;
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-1.5F, -3.0F, -3.0F, 3, 3, 3);
        head.addBox(-0.5F, -2.0F, -5.0F, 1, 2, 2);
        head.setRotationPoint(0.0F, -2.5F, -2.2F);
        body.addChild(head);
        foldedWings = new ModelRenderer(this, 0, 14);
        foldedWings.addBox(-2.0F, -2.0F, -0.5F, 4, 4, 7);
        foldedWings.setRotationPoint(0.0F, -1.0F, -1.0F);
        body.addChild(foldedWings);
        tailMid = new ModelRenderer(this, 13, 0);
        tailMid.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 7);
        tailMid.setRotationPoint(0.0F, -1.5F, 2.5F);
        tailMid.rotateAngleX = -0.2618F;
        body.addChild(tailMid);
        rightTail = new ModelRenderer(this, 16, 14);
        rightTail.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 6);
        rightTail.setRotationPoint(-0.75F, -1.0F, 2.0F);
        rightTail.rotateAngleX = -0.2618F;
        rightTail.rotateAngleY = -0.2618F;
        body.addChild(rightTail);
        leftTail = new ModelRenderer(this, 16, 14);
        leftTail.mirror = true;
        leftTail.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 6);
        leftTail.setRotationPoint(0.75F, -1.0F, 2.0F);
        leftTail.rotateAngleX = -0.2618F;
        leftTail.rotateAngleY = 0.2618F;
        body.addChild(leftTail);
        rightWing = new ModelRenderer(this, 0, 25);
        rightWing.addBox(-10.0F, 0.0F, 0.0F, 10, 1, 6);
        rightWing.setRotationPoint(-1.0F, -2.5F, -1.0F);
        body.addChild(rightWing);
        leftWing = new ModelRenderer(this, 0, 25);
        leftWing.mirror = true;
        leftWing.addBox(0.0F, 0.0F, 0.0F, 10, 1, 6);
        leftWing.setRotationPoint(1.0F, -2.5F, -1.0F);
        body.addChild(leftWing);
        rightLeg = new ModelRenderer(this, 12, 7);
        rightLeg.addBox(-0.5F, 0.0F, -1.0F, 1, 3, 1);
        rightLeg.setRotationPoint(-1.0F, 0.0F, 0.5F);
        body.addChild(rightLeg);
        leftLeg = new ModelRenderer(this, 12, 7);
        leftLeg.addBox(-0.5F, 0.0F, -1.0F, 1, 3, 1);
        leftLeg.setRotationPoint(1.0F, 0.0F, 0.5F);
        body.addChild(leftLeg);
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
        boolean flying = !entityIn.onGround;
        body.rotationPointY = 21.0F;
        foldedWings.isHidden = flying;
        rightWing.isHidden = !flying;
        leftWing.isHidden = !flying;
        if (flying) {
            rightLeg.rotateAngleX = 0.0F;
            leftLeg.rotateAngleX = 0.0F;
            float flapScale = entityIn.motionY < 0.0D ? 0.1F : 0.4F;
            rightWing.rotateAngleZ = MathHelper.sin(ageInTicks) * flapScale;
            leftWing.rotateAngleZ = -MathHelper.sin(ageInTicks + 0.97F) * flapScale;
        } else if (entityIn instanceof RavenEntity && ((RavenEntity) entityIn).isSitting()) {
            rightLeg.rotateAngleX = -(float) Math.PI / 3.0F;
            leftLeg.rotateAngleX = -(float) Math.PI / 3.0F;
            body.rotationPointY = 22.5F;
        } else {
            rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 2.0F + (float) Math.PI) * 2.0F * limbSwingAmount;
            leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 2.0F) * 2.0F * limbSwingAmount;
        }
        head.rotateAngleY = netHeadYaw * 0.017453292F;
        head.rotateAngleX = headPitch * 0.017453292F;
    }
}
