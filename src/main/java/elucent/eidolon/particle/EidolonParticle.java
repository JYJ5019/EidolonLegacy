package elucent.eidolon.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.awt.Color;

public class EidolonParticle extends Particle {
    private final float startRed;
    private final float startGreen;
    private final float startBlue;
    private final float endRed;
    private final float endGreen;
    private final float endBlue;
    private final float startAlpha;
    private final float endAlpha;
    private final float startScale;
    private final float endScale;
    private final float spin;
    private final boolean fullbright;
    private final boolean sourceSmokeCurve;
    private final float yDamping;
    private final TextureAtlasSprite endSprite;
    private final float[] hsvStart = new float[3];
    private final float[] hsvEnd = new float[3];

    public EidolonParticle(World world, TextureAtlasSprite sprite, double x, double y, double z,
                           double vx, double vy, double vz, EidolonParticleData data) {
        super(world, x, y, z, vx, vy, vz);
        setParticleTexture(sprite);
        this.motionX = vx;
        this.motionY = vy;
        this.motionZ = vz;
        this.startRed = data.startRed;
        this.startGreen = data.startGreen;
        this.startBlue = data.startBlue;
        this.endRed = data.endRed;
        this.endGreen = data.endGreen;
        this.endBlue = data.endBlue;
        this.startAlpha = data.startAlpha;
        this.endAlpha = data.endAlpha;
        this.startScale = data.startScale;
        this.endScale = data.endScale;
        this.spin = data.spin;
        this.fullbright = data.fullbright;
        this.sourceSmokeCurve = data.sourceSmokeCurve;
        this.yDamping = data.yDamping;
        this.endSprite = data.endSprite;
        this.particleMaxAge = Math.max(1, data.lifetime);
        this.particleGravity = data.gravity;
        this.canCollide = data.collides;
        Color.RGBtoHSB(toColor(this.startRed), toColor(this.startGreen), toColor(this.startBlue), hsvStart);
        Color.RGBtoHSB(toColor(this.endRed), toColor(this.endGreen), toColor(this.endBlue), hsvEnd);
        updateTraits(traitCoeff(0.0F));
    }

    @Override
    public void onUpdate() {
        updateTraits(traitCoeff((float) this.particleAge / (float) this.particleMaxAge));
        this.particleAngle += this.spin;
        super.onUpdate();
        if (this.yDamping > 0.0F) {
            this.motionY *= this.yDamping;
        }
        if (this.endSprite != null && this.particleAge >= this.particleMaxAge * 4 / 5) {
            setParticleTexture(this.endSprite);
        }
    }

    private float traitCoeff(float ageCoeff) {
        return this.sourceSmokeCurve ? 1.0F - (float) Math.sin(Math.PI * ageCoeff) : ageCoeff;
    }

    private void updateTraits(float t) {
        float clamped = Math.max(0.0F, Math.min(1.0F, t));
        float hue = lerpAngle(hsvStart[0], hsvEnd[0], clamped);
        float saturation = lerp(hsvStart[1], hsvEnd[1], clamped);
        float brightness = lerp(hsvStart[2], hsvEnd[2], clamped);
        int packed = Color.HSBtoRGB(hue, saturation, brightness);
        this.particleRed = ((packed >> 16) & 255) / 255.0F;
        this.particleGreen = ((packed >> 8) & 255) / 255.0F;
        this.particleBlue = (packed & 255) / 255.0F;
        this.particleAlpha = lerp(this.startAlpha, this.endAlpha, clamped);
        this.particleScale = lerp(this.startScale, this.endScale, clamped);
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private float lerpAngle(float a, float b, float t) {
        float delta = b - a;
        if (delta > 0.5F) {
            delta -= 1.0F;
        } else if (delta < -0.5F) {
            delta += 1.0F;
        }
        float result = a + delta * t;
        return result < 0.0F ? result + 1.0F : result > 1.0F ? result - 1.0F : result;
    }

    private int toColor(float value) {
        return Math.max(0, Math.min(255, (int) (255.0F * Math.min(1.0F, value))));
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX,
                               float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        this.prevParticleAngle = this.particleAngle - this.spin;
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return this.fullbright ? 0xF000F0 : super.getBrightnessForRender(partialTick);
    }

    public static class EidolonParticleData {
        private int lifetime = 20;
        private float startRed = 1.0F;
        private float startGreen = 1.0F;
        private float startBlue = 1.0F;
        private float endRed = 1.0F;
        private float endGreen = 1.0F;
        private float endBlue = 1.0F;
        private float startAlpha = 0.85F;
        private float endAlpha = 0.0F;
        private float startScale = 0.15F;
        private float endScale = 0.05F;
        private float spin = 0.0F;
        private float gravity = 0.0F;
        private boolean collides;
        private boolean fullbright = true;
        private boolean sourceSmokeCurve;
        private float yDamping;
        private TextureAtlasSprite endSprite;

        public EidolonParticleData lifetime(int lifetime) {
            this.lifetime = lifetime;
            return this;
        }

        public EidolonParticleData color(float red, float green, float blue) {
            return color(red, green, blue, red, green, blue);
        }

        public EidolonParticleData color(float startRed, float startGreen, float startBlue,
                                         float endRed, float endGreen, float endBlue) {
            this.startRed = startRed;
            this.startGreen = startGreen;
            this.startBlue = startBlue;
            this.endRed = endRed;
            this.endGreen = endGreen;
            this.endBlue = endBlue;
            return this;
        }

        public EidolonParticleData alpha(float startAlpha, float endAlpha) {
            this.startAlpha = startAlpha;
            this.endAlpha = endAlpha;
            return this;
        }

        public EidolonParticleData scale(float startScale, float endScale) {
            this.startScale = startScale;
            this.endScale = endScale;
            return this;
        }

        public EidolonParticleData spin(float spin) {
            this.spin = spin;
            return this;
        }

        public EidolonParticleData gravity(float gravity) {
            this.gravity = gravity;
            return this;
        }

        public EidolonParticleData collides(boolean collides) {
            this.collides = collides;
            return this;
        }

        public EidolonParticleData fullbright(boolean fullbright) {
            this.fullbright = fullbright;
            return this;
        }

        public EidolonParticleData sourceSmokeCurve(float yDamping) {
            this.sourceSmokeCurve = true;
            this.yDamping = yDamping;
            return this;
        }

        public EidolonParticleData yDamping(float yDamping) {
            this.yDamping = yDamping;
            return this;
        }

        public EidolonParticleData endSprite(TextureAtlasSprite endSprite) {
            this.endSprite = endSprite;
            return this;
        }
    }
}
