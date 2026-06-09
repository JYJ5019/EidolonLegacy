package elucent.eidolon.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.awt.Color;

public class EidolonSlashParticle extends Particle {
    private static final float DEG_TO_RAD = (float) (Math.PI / 180.0D);

    private final SlashData data;
    private final float[] hsvStart = new float[3];
    private final float[] hsvEnd = new float[3];

    public EidolonSlashParticle(World world, TextureAtlasSprite sprite, double x, double y, double z,
                                double vx, double vy, double vz, SlashData data) {
        super(world, x, y, z, vx, vy, vz);
        setParticleTexture(sprite);
        this.data = data;
        this.motionX = vx;
        this.motionY = vy;
        this.motionZ = vz;
        this.particleMaxAge = Math.max(1, data.lifetime);
        this.canCollide = false;
        Color.RGBtoHSB(toColor(data.startRed), toColor(data.startGreen), toColor(data.startBlue), hsvStart);
        Color.RGBtoHSB(toColor(data.endRed), toColor(data.endGreen), toColor(data.endBlue), hsvEnd);
        updateTraits(0.0F);

        double push = 0.025D + world.rand.nextDouble() * 0.025D;
        this.motionX += Math.sin(data.yaw) * Math.cos(data.pitch) * push;
        this.motionY += Math.sin(data.pitch) * push;
        this.motionZ += Math.cos(data.yaw) * Math.cos(data.pitch) * push;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        updateTraits((float) this.particleAge / (float) this.particleMaxAge);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX,
                               float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

        float sy = MathHelper.sin(data.yaw);
        float cy = MathHelper.cos(data.yaw);
        float sp = MathHelper.sin(data.pitch);
        float cp = MathHelper.cos(data.pitch);
        float ox = x - sy * cp * data.radius;
        float oy = y - sp * data.radius;
        float oz = z - cy * cp * data.radius;

        Axis axes = axes();
        float u0 = this.particleTexture.getMinU();
        float u1 = this.particleTexture.getMaxU();
        float v0 = this.particleTexture.getMinV();
        float v1 = this.particleTexture.getMaxV();
        int brightness = getBrightnessForRender(partialTicks);
        int lightX = brightness >> 16 & 65535;
        int lightY = brightness & 65535;
        float alpha = this.particleAlpha;
        float highlightAngle = -data.angle / 2.0F + squareOut(partialTicks) * data.angle;
        float highlightWidth = 4.0F + 4.0F * squareOut(partialTicks);

        for (int i = 0; i < 18; i++) {
            float c1 = i / 18.0F;
            float c2 = (i + 1) / 18.0F;
            Segment a = segment(c1, highlightAngle, highlightWidth, alpha);
            Segment b = segment(c2, highlightAngle, highlightWidth, alpha);
            vertexPair(buffer, ox, oy, oz, axes, a, b, u0, u1, v0, v1, lightX, lightY, false);
            vertexPair(buffer, ox, oy, oz, axes, b, a, u0, u1, v1, v0, lightX, lightY, false);
            vertexPair(buffer, ox, oy, oz, axes, a, b, u0, u1, v0, v1, lightX, lightY, true);
            vertexPair(buffer, ox, oy, oz, axes, b, a, u0, u1, v1, v0, lightX, lightY, true);
        }
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 0xF000F0;
    }

    private void updateTraits(float t) {
        float coeff = clamp(t);
        float hue = lerpAngle(hsvStart[0], hsvEnd[0], coeff);
        float saturation = lerp(hsvStart[1], hsvEnd[1], coeff);
        float brightness = lerp(hsvStart[2], hsvEnd[2], coeff);
        int packed = Color.HSBtoRGB(hue, saturation, brightness);
        this.particleRed = ((packed >> 16) & 255) / 255.0F;
        this.particleGreen = ((packed >> 8) & 255) / 255.0F;
        this.particleBlue = (packed & 255) / 255.0F;
        this.particleAlpha = lerp(data.startAlpha, data.endAlpha, coeff);
        this.particleScale = data.radius;
    }

    private Segment segment(float coeff, float highlightAngle, float highlightWidth, float alpha) {
        float angle = -data.angle / 2.0F + coeff * data.angle;
        float baseAlpha = MathHelper.sin(coeff * (float) Math.PI);
        float delta = clamp((angle - highlightAngle) / data.angle, -1.0F / highlightWidth, 1.0F / highlightWidth);
        float highlight = (MathHelper.cos(highlightWidth * (float) Math.PI * delta) + 1.0F) * 0.5F;
        float mixedAlpha = ((1.0F - data.highlight) * baseAlpha + data.highlight * highlight) * alpha;
        float width = data.width * 0.5F * (0.25F + 0.75F * MathHelper.sin(coeff * (float) Math.PI));
        width += data.highlight * 0.125F * data.width * highlight;
        return new Segment(angle, width, mixedAlpha);
    }

    private void vertexPair(BufferBuilder buffer, float ox, float oy, float oz, Axis axes, Segment a, Segment b,
                            float u0, float u1, float v0, float v1, int lightX, int lightY, boolean vertical) {
        Vec d1 = direction(axes, a.angle);
        Vec d2 = direction(axes, b.angle);
        Vec w1 = vertical ? axes.yAxis.scale(a.width * 0.5F) : d1.scale(a.width);
        Vec w2 = vertical ? axes.yAxis.scale(b.width * 0.5F) : d2.scale(b.width);
        put(buffer, ox + d2.x * data.radius + w2.x, oy + d2.y * data.radius + w2.y,
                oz + d2.z * data.radius + w2.z, u1, v1, b.alpha, lightX, lightY);
        put(buffer, ox + d2.x * data.radius - w2.x, oy + d2.y * data.radius - w2.y,
                oz + d2.z * data.radius - w2.z, u1, v0, b.alpha, lightX, lightY);
        put(buffer, ox + d1.x * data.radius - w1.x, oy + d1.y * data.radius - w1.y,
                oz + d1.z * data.radius - w1.z, u0, v0, a.alpha, lightX, lightY);
        put(buffer, ox + d1.x * data.radius + w1.x, oy + d1.y * data.radius + w1.y,
                oz + d1.z * data.radius + w1.z, u0, v1, a.alpha, lightX, lightY);
    }

    private void put(BufferBuilder buffer, float x, float y, float z, float u, float v, float alpha,
                     int lightX, int lightY) {
        buffer.pos(x, y, z)
                .tex(u, v)
                .color(this.particleRed, this.particleGreen, this.particleBlue, alpha)
                .lightmap(lightX, lightY)
                .endVertex();
    }

    private Axis axes() {
        float sy = MathHelper.sin(data.yaw);
        float cy = MathHelper.cos(data.yaw);
        float sp = MathHelper.sin(data.pitch);
        float cp = MathHelper.cos(data.pitch);
        float right = data.yaw + (float) Math.PI / 2.0F;
        float up = data.pitch + (float) Math.PI / 2.0F;
        float sr = MathHelper.sin(right);
        float cr = MathHelper.cos(right);
        float su = MathHelper.sin(up);
        float cu = MathHelper.cos(up);
        Vec xAxis = new Vec(sr * cp, 0.0F, cr * cp);
        Vec yAxis = new Vec(sy * cu, su, cy * cu);
        Vec zAxis = new Vec(sy * cp, sp, cy * cp);
        float cro = MathHelper.cos(data.roll);
        float sro = MathHelper.sin(data.roll);
        Vec rotatedX = xAxis.scale(cro).subtract(yAxis.scale(sro));
        yAxis = xAxis.scale(sro).add(yAxis.scale(cro));
        return new Axis(rotatedX, yAxis, zAxis);
    }

    private Vec direction(Axis axes, float angle) {
        float sin = MathHelper.sin(angle);
        float cos = MathHelper.cos(angle);
        return axes.xAxis.scale(sin).add(axes.zAxis.scale(cos));
    }

    private float squareOut(float partialTicks) {
        float inv = (this.particleMaxAge - (float) this.particleAge + partialTicks) / this.particleMaxAge;
        return 1.0F - inv * inv * inv * inv;
    }

    private static int toColor(float value) {
        return Math.max(0, Math.min(255, (int) (255.0F * value)));
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static float lerpAngle(float a, float b, float t) {
        float delta = b - a;
        if (delta > 0.5F) {
            delta -= 1.0F;
        } else if (delta < -0.5F) {
            delta += 1.0F;
        }
        float result = a + delta * t;
        return result < 0.0F ? result + 1.0F : result > 1.0F ? result - 1.0F : result;
    }

    private static float clamp(float value) {
        return clamp(value, 0.0F, 1.0F);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static SlashData data() {
        return new SlashData();
    }

    public static class SlashData {
        private float startRed = 1.0F;
        private float startGreen = 1.0F;
        private float startBlue = 1.0F;
        private float startAlpha = 1.0F;
        private float endRed = 1.0F;
        private float endGreen = 1.0F;
        private float endBlue = 1.0F;
        private float endAlpha = 0.0F;
        private float width = 0.625F;
        private float radius = 1.0F;
        private float pitch;
        private float yaw;
        private float roll;
        private float angle;
        private int lifetime = 10;
        private float highlight;

        public SlashData color(float r1, float g1, float b1, float r2, float g2, float b2) {
            this.startRed = r1;
            this.startGreen = g1;
            this.startBlue = b1;
            this.endRed = r2;
            this.endGreen = g2;
            this.endBlue = b2;
            return this;
        }

        public SlashData alpha(float a1, float a2) {
            this.startAlpha = a1;
            this.endAlpha = a2;
            return this;
        }

        public SlashData width(float width) {
            this.width = width;
            return this;
        }

        public SlashData radius(float radius) {
            this.radius = radius;
            return this;
        }

        public SlashData lookAt(double x1, double y1, double z1, double x2, double y2, double z2) {
            double horiz = Math.sqrt((x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1));
            this.yaw = (float) Math.atan2(x2 - x1, z2 - z1);
            this.pitch = (float) Math.atan2(y2 - y1, horiz);
            return this;
        }

        public SlashData roll(float roll) {
            this.roll = roll;
            return this;
        }

        public SlashData angle(float degrees) {
            this.angle = degrees * DEG_TO_RAD;
            return this;
        }

        public SlashData lifetime(int ticks) {
            this.lifetime = ticks;
            return this;
        }

        public SlashData highlight(float highlight) {
            this.highlight = highlight;
            return this;
        }
    }

    private static class Axis {
        private final Vec xAxis;
        private final Vec yAxis;
        private final Vec zAxis;

        private Axis(Vec xAxis, Vec yAxis, Vec zAxis) {
            this.xAxis = xAxis;
            this.yAxis = yAxis;
            this.zAxis = zAxis;
        }
    }

    private static class Segment {
        private final float angle;
        private final float width;
        private final float alpha;

        private Segment(float angle, float width, float alpha) {
            this.angle = angle;
            this.width = width;
            this.alpha = alpha;
        }
    }

    private static class Vec {
        private final float x;
        private final float y;
        private final float z;

        private Vec(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private Vec scale(float scalar) {
            return new Vec(x * scalar, y * scalar, z * scalar);
        }

        private Vec add(Vec other) {
            return new Vec(x + other.x, y + other.y, z + other.z);
        }

        private Vec subtract(Vec other) {
            return new Vec(x - other.x, y - other.y, z - other.z);
        }
    }
}
