package elucent.eidolon.client.render;

import elucent.eidolon.Reference;
import elucent.eidolon.entity.WraithEntity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class WraithRenderer extends RenderLiving<WraithEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/wraith.png");

    public WraithRenderer(RenderManager manager) {
        super(manager, new WraithModel(), 0.45F);
    }

    @Override
    protected ResourceLocation getEntityTexture(WraithEntity entity) {
        return TEXTURE;
    }
}
