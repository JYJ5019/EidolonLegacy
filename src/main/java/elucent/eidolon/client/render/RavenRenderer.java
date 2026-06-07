package elucent.eidolon.client.render;

import elucent.eidolon.Reference;
import elucent.eidolon.entity.RavenEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RavenRenderer extends RenderLiving<RavenEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/raven.png");

    public RavenRenderer(RenderManager manager) {
        super(manager, new RavenModel(), 0.25F);
    }

    @Override
    protected void preRenderCallback(RavenEntity entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.9F, 0.9F, 0.9F);
    }

    @Override
    protected ResourceLocation getEntityTexture(RavenEntity entity) {
        return TEXTURE;
    }
}
