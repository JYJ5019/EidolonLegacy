package elucent.eidolon.client.render;

import elucent.eidolon.Reference;
import elucent.eidolon.entity.RavenEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RavenRenderer extends RenderLiving<RavenEntity> {
    static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/raven.png");

    public RavenRenderer(RenderManager manager) {
        super(manager, new RavenModel(), 0.25F);
    }

    @Override
    protected void preRenderCallback(RavenEntity entity, float partialTickTime) {
        float scale = entity.isChild() ? 0.5F : 1.0F;
        shadowSize = entity.isChild() ? 0.125F : 0.25F;
        GlStateManager.scale(scale, scale, scale);
    }

    @Override
    protected ResourceLocation getEntityTexture(RavenEntity entity) {
        return TEXTURE;
    }
}
