package elucent.eidolon.client.render;

import elucent.eidolon.entity.AngelArrowEntity;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class AngelArrowRenderer extends RenderArrow<AngelArrowEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/projectiles/arrow.png");

    public AngelArrowRenderer(RenderManager manager) {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(AngelArrowEntity entity) {
        return TEXTURE;
    }
}
