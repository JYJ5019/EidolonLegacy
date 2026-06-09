package elucent.eidolon.client.render;

import elucent.eidolon.Reference;
import elucent.eidolon.entity.SlimySlugEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SlimySlugRenderer extends RenderLiving<SlimySlugEntity> {
    private static final ResourceLocation GREEN = new ResourceLocation(Reference.MOD_ID, "textures/entity/slimy_slug.png");
    private static final ResourceLocation BROWN = new ResourceLocation(Reference.MOD_ID, "textures/entity/brown_slug.png");
    private static final ResourceLocation BANANA = new ResourceLocation(Reference.MOD_ID, "textures/entity/banana_slug.png");

    public SlimySlugRenderer(RenderManager manager) {
        super(manager, new SlimySlugModel(), 0.18F);
    }

    @Override
    protected void preRenderCallback(SlimySlugEntity entity, float partialTickTime) {
        float limbSwing = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTickTime);
        float squish = 1.0F + MathHelper.cos(limbSwing * 4.0F + (float) Math.PI) * 0.1F;
        float horizontalScale = 2.0F / (1.0F + squish);
        GlStateManager.scale(horizontalScale, horizontalScale, squish);
    }

    @Override
    protected ResourceLocation getEntityTexture(SlimySlugEntity entity) {
        if (entity.getVariant() == 1) {
            return BANANA;
        }
        if (entity.getVariant() == 2) {
            return BROWN;
        }
        return GREEN;
    }
}
