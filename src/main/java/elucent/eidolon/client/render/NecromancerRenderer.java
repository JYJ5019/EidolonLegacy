package elucent.eidolon.client.render;

import elucent.eidolon.Reference;
import elucent.eidolon.entity.NecromancerEntity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class NecromancerRenderer extends RenderLiving<NecromancerEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/necromancer.png");

    public NecromancerRenderer(RenderManager manager) {
        super(manager, new NecromancerModel(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(NecromancerEntity entity) {
        return TEXTURE;
    }
}
