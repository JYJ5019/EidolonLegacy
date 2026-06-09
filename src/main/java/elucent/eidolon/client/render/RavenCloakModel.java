package elucent.eidolon.client.render;

import elucent.eidolon.item.RavenCloakItem;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class RavenCloakModel extends ModelBiped {
    private final ModelRenderer hood;
    private final ModelRenderer cloak;
    private final ModelRenderer wings;
    private final ModelRenderer leftCloak;
    private final ModelRenderer rightCloak;
    private final ModelRenderer upperLeftCloak;
    private final ModelRenderer upperRightCloak;
    private final ModelRenderer leftWing;
    private final ModelRenderer leftWingMid;
    private final ModelRenderer leftWingTip;
    private final ModelRenderer rightWing;
    private final ModelRenderer rightWingMid;
    private final ModelRenderer rightWingTip;

    private boolean flying;
    private int dashTicks;
    private int flapCharges;
    private int flapAge = -1;

    public RavenCloakModel() {
        super(0.0F, 0.0F, 128, 64);
        textureWidth = 128;
        textureHeight = 64;

        bipedHead = new ModelRenderer(this);
        bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHeadwear = new ModelRenderer(this);
        bipedBody = new ModelRenderer(this);
        bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedRightArm = new ModelRenderer(this);
        bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        bipedLeftArm = new ModelRenderer(this);
        bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        bipedRightLeg = new ModelRenderer(this);
        bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        bipedLeftLeg = new ModelRenderer(this);
        bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);

        hood = child(bipedHead, 0, 0, 0.0F, 0.5F, 0.5F, 0.2618F, 0.0F, 0.0F);
        hood.addBox(-5.5F, -10.5F, -4.5F, 11, 11, 11, 0.5F);
        childBox(hood, 0, 16, true, 6.0F, -3.0F, -2.0F, 2.0944F, 0.0F, 0.5236F,
                0.0F, -1.5F, 0.0F, 0, 3, 6);
        childBox(hood, 0, 17, true, 6.0F, -3.0F, 1.0F, 1.5708F, 0.0F, 0.7854F,
                0.0F, -1.5F, 0.0F, 0, 3, 8);
        childBox(hood, 0, 16, true, 6.0F, -2.0F, 4.0F, 1.0472F, 0.0F, 0.5236F,
                0.0F, -1.5F, 0.0F, 0, 3, 6);
        childBox(hood, 0, 16, false, -6.0F, -3.0F, -2.0F, 2.0944F, 0.0F, -0.5236F,
                0.0F, -1.5F, 0.0F, 0, 3, 6);
        childBox(hood, 0, 17, false, -6.0F, -3.0F, 1.0F, 1.5708F, 0.0F, -0.7854F,
                0.0F, -1.5F, 0.0F, 0, 3, 8);
        childBox(hood, 0, 16, false, -6.0F, -2.0F, 4.0F, 1.0472F, 0.0F, -0.5236F,
                0.0F, -1.5F, 0.0F, 0, 3, 6);
        childBox(hood, 33, 0, false, 0.0F, -11.0F, -5.0F, 0.7854F, 0.0F, 0.0F,
                -2.5F, 0.0F, -4.0F, 5, 4, 4);

        cloak = child(bipedBody, 0, 0, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        leftCloak = childBox(cloak, 0, 28, true, -0.5F, 1.0F, -3.0F, 0.1309F, -0.1309F, -0.1309F,
                -0.5F, -4.0F, -4.0F, 12, 20, 12);
        rightCloak = childBox(cloak, 0, 28, false, -0.5F, 1.0F, -3.0F, 0.1309F, 0.1309F, 0.1309F,
                -11.5F, -4.0F, -4.0F, 12, 20, 12);

        wings = child(bipedBody, 0, 0, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        upperLeftCloak = childBox(wings, 0, 28, true, -0.5F, 4.0F, -3.0F, 0.1309F, -0.1309F, -0.5236F,
                0.5F, -4.0F, -4.0F, 12, 7, 12);
        upperRightCloak = childBox(wings, 0, 28, false, -0.5F, 4.0F, -3.0F, 0.1309F, 0.1309F, 0.5236F,
                -12.5F, -4.0F, -4.0F, 12, 7, 12);

        leftWing = child(wings, 51, 0, 0.0F, -2.0F, 5.0F, 0.0F, -0.5236F, -0.3927F);
        leftWing.addBox(0.0F, 0.0F, -2.0F, 12, 8, 2);
        leftWing.setTextureOffset(48, 41).addBox(-4.0F, 8.0F, -1.0F, 14, 12, 0);
        leftWingMid = child(leftWing, 72, 10, 12.5F, 0.0F, -0.5F, 0.0F, 0.2618F, 0.1309F);
        leftWingMid.addBox(0.0F, 0.0F, -1.0F, 12, 8, 1);
        leftWingMid.setTextureOffset(44, 27).addBox(-1.0F, 8.0F, -0.5F, 14, 12, 0);
        leftWingTip = childBox(leftWingMid, 44, 11, false, 11.5F, 0.0F, 0.5F, 0.0F, 0.5236F, -0.1309F,
                0.0F, 0.0F, -1.0F, 14, 16, 0);

        rightWing = child(wings, 51, 0, 0.0F, -2.0F, 5.0F, 0.0F, 0.5236F, 0.3927F);
        rightWing.mirror = true;
        rightWing.addBox(-12.0F, 0.0F, -2.0F, 12, 8, 2);
        rightWing.setTextureOffset(48, 41).addBox(-10.0F, 8.0F, -1.0F, 14, 12, 0);
        rightWing.mirror = false;
        rightWingMid = child(rightWing, 72, 10, -12.5F, 0.0F, -0.5F, 0.0F, -0.2618F, -0.1309F);
        rightWingMid.mirror = true;
        rightWingMid.addBox(-12.0F, 0.0F, -1.0F, 12, 8, 1);
        rightWingMid.setTextureOffset(44, 27).addBox(-13.0F, 8.0F, -0.5F, 14, 12, 0);
        rightWingMid.mirror = false;
        rightWingTip = childBox(rightWingMid, 44, 11, true, -11.5F, 0.0F, 0.5F, 0.0F, -0.5236F, 0.1309F,
                -14.0F, 0.0F, -1.0F, 14, 16, 0);
    }

    public void setState(boolean flying, int dashTicks, int flapCharges) {
        setState(flying, dashTicks, flapCharges, -1);
    }

    public void setState(boolean flying, int dashTicks, int flapCharges, int flapAge) {
        this.flying = flying;
        this.dashTicks = Math.max(0, Math.min(RavenCloakItem.MAX_DASH_TICKS, dashTicks));
        this.flapCharges = Math.max(0, Math.min(RavenCloakItem.MAX_CHARGES, flapCharges));
        this.flapAge = flapAge;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            bipedHead.showModel = true;
            bipedHeadwear.showModel = false;
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);

        boolean active = flying || dashTicks > 0 || (!entity.onGround && entity.motionY < -0.12D);
        cloak.showModel = !active;
        wings.showModel = active;

        float speed = MathHelper.clamp((float)Math.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ),
                0.0F, 0.45F);
        float sink = MathHelper.clamp((float)(-entity.motionY), -0.4F, 0.5F);
        float chargeEase = 1.0F - flapCharges / (float)RavenCloakItem.MAX_CHARGES;
        float cloakSway = speed * 0.18F + sink * 0.08F + chargeEase * 0.03F;

        setRotation(leftCloak, 0.1309F + cloakSway, -0.1309F, -0.1309F - speed * 0.08F);
        setRotation(rightCloak, 0.1309F + cloakSway, 0.1309F, 0.1309F + speed * 0.08F);

        float flap = active ? MathHelper.sin(ageInTicks * 0.35F) * 0.12F : 0.0F;
        if (flapAge >= 0 && flapAge < 20) {
            float flapTime = Math.max(0.05F, flapAge / 20.0F);
            flap += MathHelper.sin((float)Math.PI * 2.4F * flapTime) * (1.0F - flapTime) * 0.55F;
        }
        float spread = active ? 1.0F : 0.0F;
        float dashStart = dashTicks > 0
                ? MathHelper.clamp((RavenCloakItem.MAX_DASH_TICKS - dashTicks) / 10.0F, 0.0F, 1.0F)
                : 0.0F;

        float leftWingX = lerp(spread, 0.0F, 0.7854F);
        float leftWingY = lerp(spread, 0.2618F, -0.5236F);
        float leftWingZ = lerp(spread, -0.3927F, -0.7854F);
        float leftMidY = lerp(spread, 1.4399F, 0.2618F);
        float leftTipY = lerp(spread, 1.5708F, 0.5236F);

        float rightWingX = lerp(spread, 0.0F, 0.7854F);
        float rightWingY = lerp(spread, -0.2618F, 0.5236F);
        float rightWingZ = lerp(spread, 0.3927F, 0.7854F);
        float rightMidY = lerp(spread, -1.4399F, -0.2618F);
        float rightTipY = lerp(spread, -1.5708F, -0.5236F);

        if (dashTicks > 0) {
            leftWingX = lerp(dashStart, leftWingX, 0.2618F);
            leftWingY = lerp(dashStart, leftWingY, 0.0F);
            leftWingZ = lerp(dashStart, leftWingZ, 0.0F);
            rightWingX = lerp(dashStart, rightWingX, 0.2618F);
            rightWingY = lerp(dashStart, rightWingY, 0.0F);
            rightWingZ = lerp(dashStart, rightWingZ, 0.0F);
        } else {
            leftWingZ += flap + sink * 0.5236F;
            rightWingZ -= flap + sink * 0.5236F;
            leftMidY += flap * 0.55F;
            rightMidY -= flap * 0.55F;
        }

        if (isSneak) {
            leftWingY += 0.3927F;
            leftWingZ += 0.3927F;
            rightWingY -= 0.3927F;
            rightWingZ -= 0.3927F;
        }

        setRotation(upperLeftCloak, 0.1309F, -0.1309F, -0.5236F);
        setRotation(upperRightCloak, 0.1309F, 0.1309F, 0.5236F);
        setRotation(leftWing, leftWingX, leftWingY, leftWingZ);
        setRotation(leftWingMid, 0.0F, leftMidY, 0.1309F);
        setRotation(leftWingTip, 0.0F, leftTipY, -0.1309F);
        setRotation(rightWing, rightWingX, rightWingY, rightWingZ);
        setRotation(rightWingMid, 0.0F, rightMidY, -0.1309F);
        setRotation(rightWingTip, 0.0F, rightTipY, 0.1309F);
    }

    private ModelRenderer child(ModelRenderer parent, int texU, int texV,
                                float x, float y, float z,
                                float rotX, float rotY, float rotZ) {
        ModelRenderer renderer = new ModelRenderer(this, texU, texV);
        renderer.setRotationPoint(x, y, z);
        setRotation(renderer, rotX, rotY, rotZ);
        parent.addChild(renderer);
        return renderer;
    }

    private ModelRenderer childBox(ModelRenderer parent, int texU, int texV, boolean mirror,
                                   float x, float y, float z,
                                   float rotX, float rotY, float rotZ,
                                   float boxX, float boxY, float boxZ,
                                   int width, int height, int depth) {
        ModelRenderer renderer = child(parent, texU, texV, x, y, z, rotX, rotY, rotZ);
        renderer.mirror = mirror;
        renderer.addBox(boxX, boxY, boxZ, width, height, depth);
        renderer.mirror = false;
        return renderer;
    }

    private static void setRotation(ModelRenderer renderer, float x, float y, float z) {
        renderer.rotateAngleX = x;
        renderer.rotateAngleY = y;
        renderer.rotateAngleZ = z;
    }

    private static float lerp(float progress, float from, float to) {
        return from + (to - from) * progress;
    }
}
