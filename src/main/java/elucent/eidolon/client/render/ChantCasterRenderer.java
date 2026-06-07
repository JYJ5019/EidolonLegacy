package elucent.eidolon.client.render;

import elucent.eidolon.Reference;
import elucent.eidolon.client.render.shader.LegacyShaders;
import elucent.eidolon.entity.ChantCasterEntity;
import elucent.eidolon.spell.Sign;
import elucent.eidolon.spell.SignSequence;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ChantCasterRenderer extends Render<ChantCasterEntity> {
    private static final ResourceLocation FALLBACK =
            new ResourceLocation(Reference.MOD_ID, "textures/particle/magic_sign.png");

    public ChantCasterRenderer(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(ChantCasterEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        SignSequence sequence = entity.getSignSequence();
        List<Sign> signs = new ArrayList<>(sequence.getSigns());
        if (signs.isEmpty()) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.25D, z);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();

        int count = signs.size();
        float radius = Math.max(0.28F, MathHelper.sqrt(count) * 0.22F);
        float alpha = entity.getDeathTimer() > 0 ? Math.max(0.0F, entity.getDeathTimer() / 20.0F) : 1.0F;
        if (!entity.hasSucceeded() && entity.getDeathTimer() > 0) {
            alpha *= alpha;
        }

        for (int i = 0; i < count; i++) {
            Sign sign = signs.get(i);
            float angle = (float) (-Math.PI / 2.0D - i * Math.PI * 2.0D / count
                    + (entity.ticksExisted + partialTicks) * 0.04D);
            float px = MathHelper.cos(angle) * radius;
            float py = 0.35F + MathHelper.sin(angle) * radius;
            float size = 0.16F + MathHelper.sin((entity.ticksExisted + partialTicks + i * 5.0F) * 0.18F) * 0.025F;
            renderSign(sign, px, py, size, alpha);
        }

        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void renderSign(Sign sign, float x, float y, float size, float alpha) {
        bindTexture(texture(sign.getSprite()));
        boolean shader = LegacyShaders.beginSprite(1.55F, 1.55F, 1.65F, 1.0F);
        try {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(x - size, y + size, 0.0D).tex(0.0D, 0.0D)
                    .color(sign.getRed(), sign.getGreen(), sign.getBlue(), alpha).endVertex();
            buffer.pos(x - size, y - size, 0.0D).tex(0.0D, 1.0D)
                    .color(sign.getRed(), sign.getGreen(), sign.getBlue(), alpha).endVertex();
            buffer.pos(x + size, y - size, 0.0D).tex(1.0D, 1.0D)
                    .color(sign.getRed(), sign.getGreen(), sign.getBlue(), alpha).endVertex();
            buffer.pos(x + size, y + size, 0.0D).tex(1.0D, 0.0D)
                    .color(sign.getRed(), sign.getGreen(), sign.getBlue(), alpha).endVertex();
            tessellator.draw();
        } finally {
            LegacyShaders.end(shader);
        }
    }

    private ResourceLocation texture(ResourceLocation sprite) {
        return new ResourceLocation(sprite.getNamespace(), "textures/" + sprite.getPath() + ".png");
    }

    @Override
    protected ResourceLocation getEntityTexture(ChantCasterEntity entity) {
        return FALLBACK;
    }
}
