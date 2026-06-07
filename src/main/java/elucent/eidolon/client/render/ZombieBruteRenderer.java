package elucent.eidolon.client.render;

import elucent.eidolon.Reference;
import elucent.eidolon.entity.ZombieBruteEntity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class ZombieBruteRenderer extends RenderLiving<ZombieBruteEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/zombie_brute.png");

    public ZombieBruteRenderer(RenderManager manager) {
        super(manager, new ZombieBruteModel(), 0.7F);
    }

    @Override
    protected ResourceLocation getEntityTexture(ZombieBruteEntity entity) {
        return TEXTURE;
    }
}
