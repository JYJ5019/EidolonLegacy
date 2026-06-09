package elucent.eidolon.client.render;

import elucent.eidolon.Reference;
import elucent.eidolon.entity.NecromancerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class NecromancerRenderer extends RenderLiving<NecromancerEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/necromancer.png");
    private static final ResourceLocation EYES_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/necromancer_eyes.png");

    public NecromancerRenderer(RenderManager manager) {
        super(manager, new NecromancerModel(), 0.5F);
        addLayer(new NecromancerEyesLayer(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(NecromancerEntity entity) {
        return TEXTURE;
    }

    private static class NecromancerEyesLayer implements LayerRenderer<NecromancerEntity> {
        private static final int FULL_BRIGHT = 61680;

        private final NecromancerRenderer renderer;

        private NecromancerEyesLayer(NecromancerRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public void doRenderLayer(NecromancerEntity entity, float limbSwing, float limbSwingAmount,
                                  float partialTicks, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scale) {
            int packedBrightness = entity.getBrightnessForRender();

            Minecraft.getMinecraft().getTextureManager().bindTexture(EYES_TEXTURE);
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GlStateManager.depthMask(false);
            setLightmap(FULL_BRIGHT);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            try {
                renderer.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks,
                        netHeadYaw, headPitch, scale);
            } finally {
                setLightmap(packedBrightness);
                GlStateManager.depthMask(true);
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GlStateManager.enableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        private void setLightmap(int packedBrightness) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                    packedBrightness % 65536, packedBrightness / 65536);
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
