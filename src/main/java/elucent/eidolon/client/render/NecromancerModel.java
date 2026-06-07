package elucent.eidolon.client.render;

import elucent.eidolon.entity.NecromancerEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class NecromancerModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    private final ModelRenderer crossedArms;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;

    public NecromancerModel() {
        textureWidth = 64;
        textureHeight = 64;
        body = new ModelRenderer(this, 16, 20);
        body.addBox(-4.0F, -12.0F, -3.0F, 8, 12, 6);
        body.setRotationPoint(0.0F, 12.0F, 0.0F);
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8);
        head.setRotationPoint(0.0F, -12.0F, 0.0F);
        body.addChild(head);
        crossedArms = new ModelRenderer(this, 40, 38);
        crossedArms.addBox(-4.0F, 0.0F, -2.0F, 8, 4, 4);
        crossedArms.setRotationPoint(0.0F, -8.0F, -2.0F);
        crossedArms.rotateAngleX = -0.78F;
        body.addChild(crossedArms);
        rightArm = new ModelRenderer(this, 40, 46);
        rightArm.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        rightArm.setRotationPoint(-6.0F, -12.0F, 0.0F);
        body.addChild(rightArm);
        leftArm = new ModelRenderer(this, 40, 46);
        leftArm.mirror = true;
        leftArm.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        leftArm.setRotationPoint(6.0F, -12.0F, 0.0F);
        body.addChild(leftArm);
        rightLeg = new ModelRenderer(this, 0, 22);
        rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        rightLeg.setRotationPoint(-2.0F, 0.0F, 0.0F);
        body.addChild(rightLeg);
        leftLeg = new ModelRenderer(this, 0, 22);
        leftLeg.mirror = true;
        leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        leftLeg.setRotationPoint(2.0F, 0.0F, 0.0F);
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
        boolean casting = entityIn instanceof NecromancerEntity && ((NecromancerEntity) entityIn).isCastingSpellLegacy();
        head.rotateAngleY = netHeadYaw * 0.017453292F;
        head.rotateAngleX = headPitch * 0.017453292F;
        rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
        leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * limbSwingAmount;
        crossedArms.isHidden = casting;
        rightArm.isHidden = !casting;
        leftArm.isHidden = !casting;
        if (casting) {
            rightArm.rotateAngleZ = 2.35F;
            leftArm.rotateAngleZ = -2.35F;
            rightArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
            leftArm.rotateAngleX = rightArm.rotateAngleX;
        } else {
            rightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
            leftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
            rightArm.rotateAngleZ = 0.0F;
            leftArm.rotateAngleZ = 0.0F;
        }
    }
}
