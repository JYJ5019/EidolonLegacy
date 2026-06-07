package elucent.eidolon.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class RavenModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer foldedWings;
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
        head.setRotationPoint(0.0F, -2.5F, -2.2F);
        body.addChild(head);
        foldedWings = new ModelRenderer(this, 0, 14);
        foldedWings.addBox(-2.0F, -2.0F, -0.5F, 4, 4, 7);
        foldedWings.setRotationPoint(0.0F, -1.0F, -1.0F);
        body.addChild(foldedWings);
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
        foldedWings.isHidden = flying;
        rightWing.isHidden = !flying;
        leftWing.isHidden = !flying;
        rightWing.rotateAngleZ = -0.35F - MathHelper.sin(ageInTicks * 0.55F) * 0.45F;
        leftWing.rotateAngleZ = 0.35F + MathHelper.sin(ageInTicks * 0.55F) * 0.45F;
        rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 2.0F + (float) Math.PI) * limbSwingAmount;
        leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 2.0F) * limbSwingAmount;
        head.rotateAngleY = netHeadYaw * 0.017453292F;
        head.rotateAngleX = headPitch * 0.017453292F;
    }
}
