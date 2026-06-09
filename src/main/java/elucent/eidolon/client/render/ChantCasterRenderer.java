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
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ChantCasterRenderer extends Render<ChantCasterEntity> {
    private static final ResourceLocation FALLBACK =
            new ResourceLocation(Reference.MOD_ID, "textures/particle/magic_sign.png");
    private static final ResourceLocation BEAM =
            new ResourceLocation(Reference.MOD_ID, "textures/particle/beam.png");
    private static final ResourceLocation RING =
            new ResourceLocation(Reference.MOD_ID, "textures/particle/ring.png");
    private static final float HALF_PI = (float) (Math.PI / 2.0D);
    private static final float TWO_PI = (float) (Math.PI * 2.0D);

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
        try {
            GlStateManager.translate(x, y, z);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.depthMask(false);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int count = signs.size();
            int ringSigns = Math.max(0, count - 1);
            float radius = MathHelper.sqrt(ringSigns) * 0.25F;
            if (ringSigns > 0) {
                radius = Math.max(0.3F, radius);
            }

            Vec3d look = entity.getLookDirection();
            Vec3d left = new Vec3d(0.0D, 1.0D, 0.0D).crossProduct(look);
            if (left.length() <= 0.001D) {
                left = new Vec3d(1.0D, 0.0D, 0.0D);
            } else {
                left = left.normalize();
            }
            Vec3d up = look.crossProduct(left);
            if (up.length() <= 0.001D) {
                up = new Vec3d(0.0D, 1.0D, 0.0D);
            } else {
                up = up.normalize();
            }

            Vec3d center = look.add(0.0D, 0.5D, 0.0D);
            int deathTimer = entity.getDeathTimer();
            boolean succeeded = entity.hasSucceeded();
            int repetitions = deathTimer > 0 && succeeded ? 3 : 1;
            float alpha = 1.0F;
            if (deathTimer > 0) {
                alpha = deathTimer / 20.0F;
                alpha *= alpha;
                if (!succeeded) {
                    alpha *= alpha;
                    center = center.add(look.scale(1.0F - alpha));
                    radius += 0.25F - 0.25F * alpha;
                }
            }

            float ticks = entity.ticksExisted + partialTicks;
            float spin = ticks * 0.04F;
            for (int i = 0; i < repetitions; i++) {
                renderBeams(signs, center, left, up, radius, alpha, spin);
                renderSignRings(signs, center, left, up, radius, alpha, spin, ticks);
                renderSigns(signs, center, left, up, radius, alpha, spin, ticks);
            }
        } finally {
            GlStateManager.depthMask(true);
            GlStateManager.enableCull();
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void renderSigns(List<Sign> signs, Vec3d center, Vec3d left, Vec3d up,
                             float radius, float alpha, float spin, float ticks) {
        int count = signs.size();
        for (int i = 0; i < count; i++) {
            Sign sign = signs.get(i);
            float angle = -HALF_PI - i * TWO_PI / count + spin;
            Vec3d signCenter = pointOnCircle(center, left, up, radius, angle);
            float size = signSize(ticks, i);
            float signAlpha = alpha * brightness(angle, ticks);
            renderSign(sign, signCenter, left.scale(size), up.scale(size), signAlpha);
        }
    }

    private void renderSign(Sign sign, Vec3d center, Vec3d dx, Vec3d dy, float alpha) {
        bindTexture(texture(sign.getSprite()));
        boolean shader = LegacyShaders.beginSprite(1.55F, 1.55F, 1.65F, 1.0F);
        try {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            putQuad(buffer, center, dx, dy, sign.getRed(), sign.getGreen(), sign.getBlue(), alpha,
                    0.0D, 0.0D, 1.0D, 1.0D);
            tessellator.draw();
        } finally {
            LegacyShaders.end(shader);
        }
    }

    private void renderSignRings(List<Sign> signs, Vec3d center, Vec3d left, Vec3d up,
                                 float radius, float alpha, float spin, float ticks) {
        bindTexture(RING);
        boolean shader = LegacyShaders.beginSprite(1.55F, 1.55F, 1.65F, 1.0F);
        try {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            int count = signs.size();
            for (int i = 0; i < count; i++) {
                Sign sign = signs.get(i);
                float angle = -HALF_PI - i * TWO_PI / count + spin;
                Vec3d signCenter = pointOnCircle(center, left, up, radius, angle);
                float size = signSize(ticks, i) * 1.75F;
                putQuad(buffer, signCenter, left.scale(size), up.scale(size),
                        sign.getRed(), sign.getGreen(), sign.getBlue(), alpha * 0.5F,
                        0.0D, 0.0D, 1.0D, 1.0D);
            }
            tessellator.draw();
        } finally {
            LegacyShaders.end(shader);
        }
    }

    private void renderBeams(List<Sign> signs, Vec3d center, Vec3d left, Vec3d up,
                             float radius, float alpha, float spin) {
        if (signs.isEmpty()) {
            return;
        }
        bindTexture(BEAM);
        boolean shader = LegacyShaders.beginSprite(1.55F, 1.55F, 1.65F, 1.0F);
        try {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

            int count = signs.size();
            int steps = Math.max(24, count * 4);
            if (steps % count != 0) {
                steps += count - steps % count;
            }
            int periodicity = Math.max(4, steps / count);
            renderBeamBand(buffer, signs, center, left, up, radius + 0.1375F, radius + 0.3375F,
                    alpha * 0.5F, spin, steps, periodicity);
            renderBeamBand(buffer, signs, center, left, up, radius - 0.3F, radius - 0.1F,
                    alpha * 0.5F, spin, steps, periodicity);

            tessellator.draw();
        } finally {
            LegacyShaders.end(shader);
        }
    }

    private void renderBeamBand(BufferBuilder buffer, List<Sign> signs, Vec3d center, Vec3d left, Vec3d up,
                                float innerRadius, float outerRadius, float alpha, float spin,
                                int steps, int periodicity) {
        int count = signs.size();
        for (int i = 0; i < steps; i++) {
            int curIndex = (i / periodicity - 1 + count) % count;
            Sign current = signs.get(curIndex);
            Sign next = signs.get((curIndex + 1) % count);
            float t1 = (i % periodicity) / (float) periodicity;
            float t2 = (i % periodicity + 1) / (float) periodicity;

            float red1 = lerp(t1, current.getRed(), next.getRed());
            float red2 = lerp(t2, current.getRed(), next.getRed());
            float green1 = lerp(t1, current.getGreen(), next.getGreen());
            float green2 = lerp(t2, current.getGreen(), next.getGreen());
            float blue1 = lerp(t1, current.getBlue(), next.getBlue());
            float blue2 = lerp(t2, current.getBlue(), next.getBlue());

            float angle1 = -HALF_PI - (i - periodicity) * TWO_PI / steps + spin;
            float angle2 = -HALF_PI - (i - periodicity + 1) * TWO_PI / steps + spin;
            Vec3d inner1 = pointOnCircle(center, left, up, innerRadius, angle1);
            Vec3d inner2 = pointOnCircle(center, left, up, innerRadius, angle2);
            Vec3d outer1 = pointOnCircle(center, left, up, outerRadius, angle1);
            Vec3d outer2 = pointOnCircle(center, left, up, outerRadius, angle2);

            putVertex(buffer, outer1, 1.0D, 1.0D, red1, green1, blue1, alpha);
            putVertex(buffer, outer2, 0.0D, 1.0D, red2, green2, blue2, alpha);
            putVertex(buffer, inner2, 0.0D, 0.0D, red2, green2, blue2, alpha);
            putVertex(buffer, inner1, 1.0D, 0.0D, red1, green1, blue1, alpha);
        }
    }

    private void putQuad(BufferBuilder buffer, Vec3d center, Vec3d dx, Vec3d dy,
                         float red, float green, float blue, float alpha,
                         double u0, double v0, double u1, double v1) {
        putVertex(buffer, center.x - dx.x + dy.x, center.y - dx.y + dy.y, center.z - dx.z + dy.z,
                u0, v0, red, green, blue, alpha);
        putVertex(buffer, center.x - dx.x - dy.x, center.y - dx.y - dy.y, center.z - dx.z - dy.z,
                u0, v1, red, green, blue, alpha);
        putVertex(buffer, center.x + dx.x - dy.x, center.y + dx.y - dy.y, center.z + dx.z - dy.z,
                u1, v1, red, green, blue, alpha);
        putVertex(buffer, center.x + dx.x + dy.x, center.y + dx.y + dy.y, center.z + dx.z + dy.z,
                u1, v0, red, green, blue, alpha);
    }

    private void putVertex(BufferBuilder buffer, Vec3d pos, double u, double v,
                           float red, float green, float blue, float alpha) {
        putVertex(buffer, pos.x, pos.y, pos.z, u, v, red, green, blue, alpha);
    }

    private void putVertex(BufferBuilder buffer, double x, double y, double z, double u, double v,
                           float red, float green, float blue, float alpha) {
        buffer.pos(x, y, z).tex(u, v).color(red, green, blue, alpha).endVertex();
    }

    private Vec3d pointOnCircle(Vec3d center, Vec3d left, Vec3d up, float radius, float angle) {
        return center.add(left.scale(MathHelper.cos(angle) * radius)).add(up.scale(MathHelper.sin(angle) * radius));
    }

    private float signSize(float ticks, int index) {
        return 0.175F + MathHelper.sin((ticks + index * 5.0F) * 0.18F) * 0.025F;
    }

    private float brightness(float angle, float ticks) {
        float bright = MathHelper.sin(angle + TWO_PI * ticks / 20.0F);
        bright = Math.max(0.0F, Math.min(1.0F, bright));
        bright *= bright;
        return 0.6F + 0.4F * bright;
    }

    private float lerp(float amount, float start, float end) {
        return start + amount * (end - start);
    }

    private ResourceLocation texture(ResourceLocation sprite) {
        return new ResourceLocation(sprite.getNamespace(), "textures/" + sprite.getPath() + ".png");
    }

    @Override
    protected ResourceLocation getEntityTexture(ChantCasterEntity entity) {
        return FALLBACK;
    }
}
