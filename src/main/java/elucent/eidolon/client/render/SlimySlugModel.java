package elucent.eidolon.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class SlimySlugModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer tail;
    private final ModelRenderer rightEye;
    private final ModelRenderer leftEye;

    public SlimySlugModel() {
        textureWidth = 32;
        textureHeight = 16;
        body = new ModelRenderer(this, 0, 0);
        body.addBox(-2.0F, -4.0F, -4.0F, 4, 4, 8);
        body.setRotationPoint(0.0F, 24.0F, 0.0F);
        tail = new ModelRenderer(this, 16, 0);
        tail.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 2);
        tail.setRotationPoint(0.0F, 0.0F, 4.0F);
        body.addChild(tail);
        rightEye = new ModelRenderer(this, 0, 0);
        rightEye.addBox(-1.0F, -3.0F, -0.5F, 1, 4, 1);
        rightEye.setRotationPoint(-0.8F, -3.5F, -3.0F);
        rightEye.rotateAngleZ = -0.25F;
        body.addChild(rightEye);
        leftEye = new ModelRenderer(this, 0, 0);
        leftEye.addBox(0.0F, -3.0F, -0.5F, 1, 4, 1);
        leftEye.setRotationPoint(0.8F, -3.5F, -3.0F);
        leftEye.rotateAngleZ = 0.25F;
        body.addChild(leftEye);
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
        body.rotationPointY = 24.0F + MathHelper.cos(limbSwing * 4.0F) * 0.35F * limbSwingAmount;
    }
}
