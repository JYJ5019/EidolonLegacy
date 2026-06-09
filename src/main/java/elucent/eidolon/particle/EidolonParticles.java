package elucent.eidolon.particle;

import elucent.eidolon.Reference;
import elucent.eidolon.client.ClientConfig;
import elucent.eidolon.particle.EidolonParticle.EidolonParticleData;
import elucent.eidolon.spell.Rune;
import elucent.eidolon.spell.Runes;
import elucent.eidolon.spell.Sign;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public final class EidolonParticles {
    private static final Map<ResourceLocation, ResourceLocation> TEXTURES = new LinkedHashMap<>();
    private static final Map<ResourceLocation, TextureAtlasSprite> SPRITES = new LinkedHashMap<>();

    public static final ResourceLocation WISP = texture("wisp");
    public static final ResourceLocation BEAM = texture("beam");
    public static final ResourceLocation FLAME = texture("flame");
    public static final ResourceLocation SMOKE = texture("smoke");
    public static final ResourceLocation SPARKLE = texture("sparkle");
    public static final ResourceLocation BUBBLE = texture("bubble");
    public static final ResourceLocation BURST = texture("burst");
    public static final ResourceLocation AURA = texture("aura");
    public static final ResourceLocation EYE = texture("eye");
    public static final ResourceLocation RING = texture("ring");

    private EidolonParticles() {
    }

    public static ParticleBuilder create(ResourceLocation texture) {
        ParticleBuilder builder = new ParticleBuilder(texture);
        if (SMOKE.equals(texture)) {
            builder.sourceSmokeCurve(0.98F);
        } else if (BUBBLE.equals(texture)) {
            builder.yDamping(0.8F).endSprite(BURST);
        }
        return builder;
    }

    public static void registerDefaults() {
        texture("blood_sign");
        texture("death_sign");
        texture("flame_sign");
        texture("harmony_sign");
        texture("magic_sign");
        texture("mind_sign");
        texture("sacred_sign");
        texture("soul_sign");
        texture("warding_sign");
        texture("wicked_sign");
        texture("winter_sign");
        texture("absorption_ritual");
        texture("allure_ritual");
        texture("crystal_ritual");
        texture("daylight_ritual");
        texture("deceit_ritual");
        texture("moonlight_ritual");
        texture("purify_ritual");
        texture("recharge_ritual");
        texture("repelling_ritual");
        texture("sanguine_ritual");
        texture("summon_ritual");
        texture("warp_ritual");
        texture("energy_sign");
        texture("feather");
        for (Rune rune : Runes.getRunes()) {
            register(rune.getSprite());
        }
    }

    private static ResourceLocation texture(String name) {
        ResourceLocation location = new ResourceLocation(Reference.MOD_ID, "particle/" + name);
        register(location);
        return location;
    }

    private static ResourceLocation register(ResourceLocation location) {
        TEXTURES.put(location, location);
        return location;
    }

    private static void onTextureStitch(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        for (ResourceLocation texture : TEXTURES.values()) {
            SPRITES.put(texture, map.registerSprite(texture));
        }
    }

    public static final class TextureEvents {
        @net.minecraftforge.fml.common.eventhandler.SubscribeEvent
        public void onTextureStitch(TextureStitchEvent.Pre event) {
            EidolonParticles.onTextureStitch(event);
        }
    }

    public static void spawnWisp(World world, double x, double y, double z, double vx, double vy, double vz,
                                 float red, float green, float blue) {
        spawn(world, WISP, x, y, z, vx, vy, vz, new EidolonParticleData()
                .lifetime(22)
                .color(red, green, blue, Math.min(1.0F, red * 1.35F), Math.min(1.0F, green * 1.35F),
                        Math.min(1.0F, blue * 1.35F))
                .alpha(0.85F, 0.0F)
                .scale(0.16F, 0.04F)
                .spin(randomSpin(world, 0.05F)));
    }

    public static void spawnSparkle(World world, double x, double y, double z, double vx, double vy, double vz,
                                    float red, float green, float blue) {
        spawn(world, SPARKLE, x, y, z, vx, vy, vz, new EidolonParticleData()
                .lifetime(16)
                .color(red, green, blue, 1.0F, 1.0F, 1.0F)
                .alpha(0.9F, 0.0F)
                .scale(0.11F, 0.02F)
                .spin(randomSpin(world, 0.12F)));
    }

    public static void spawnFlame(World world, double x, double y, double z, double vx, double vy, double vz,
                                  float red, float green, float blue) {
        spawn(world, FLAME, x, y, z, vx, vy, vz, new EidolonParticleData()
                .lifetime(18)
                .color(red, green, blue, 1.0F, 0.75F, 0.28F)
                .alpha(0.9F, 0.0F)
                .scale(0.18F, 0.04F)
                .spin(randomSpin(world, 0.04F)));
    }

    public static void spawnSmoke(World world, double x, double y, double z, double vx, double vy, double vz,
                                  float red, float green, float blue) {
        spawn(world, SMOKE, x, y, z, vx, vy, vz, new EidolonParticleData()
                .lifetime(28)
                .color(red, green, blue, 0.18F, 0.18F, 0.2F)
                .alpha(0.55F, 0.0F)
                .scale(0.18F, 0.45F)
                .spin(randomSpin(world, 0.025F))
                .fullbright(false)
                .sourceSmokeCurve(0.98F));
    }

    public static void spawnSteam(World world, double x, double y, double z, double vx, double vy, double vz,
                                  float red, float green, float blue) {
        spawn(world, SMOKE, x, y, z, vx, vy, vz, new EidolonParticleData()
                .lifetime(70)
                .color(red, green, blue, 0.86F, 0.88F, 0.92F)
                .alpha(0.16F, 0.0F)
                .scale(0.28F, 0.1F)
                .spin(randomSpin(world, 0.018F))
                .fullbright(false)
                .sourceSmokeCurve(0.99F));
    }

    public static void spawnBubble(World world, double x, double y, double z, double vx, double vy, double vz) {
        spawnBubble(world, x, y, z, vx, vy, vz, 0.72F, 0.88F, 1.0F);
    }

    public static void spawnBubble(World world, double x, double y, double z, double vx, double vy, double vz,
                                   float red, float green, float blue) {
        spawn(world, BUBBLE, x, y, z, vx, vy, vz, new EidolonParticleData()
                .lifetime(18)
                .color(red, green, blue)
                .alpha(0.6F, 0.0F)
                .scale(0.1F, 0.16F)
                .yDamping(0.8F)
                .endSprite(sprite(BURST)));
    }

    public static void spawnSign(World world, Sign sign, double x, double y, double z, double vx, double vy, double vz) {
        spawn(world, sign.getSprite(), x, y, z, vx, vy, vz, new EidolonParticleData()
                .lifetime(20)
                .color(sign.getRed(), sign.getGreen(), sign.getBlue())
                .alpha(0.8F, 0.0F)
                .scale(0.25F, 0.12F)
                .gravity(-0.004F)
                .spin(randomSpin(world, 0.03F)));
    }

    public static void spawnRune(World world, Rune rune, double x, double y, double z, double vx, double vy, double vz) {
        spawnRune(world, rune, x, y, z, vx, vy, vz, 0.75F, 0.35F, 1.0F, 1.0F, 0.8F, 1.0F);
    }

    public static void spawnRune(World world, Rune rune, double x, double y, double z, double vx, double vy, double vz,
                                 float red, float green, float blue) {
        spawnRune(world, rune, x, y, z, vx, vy, vz,
                red, green, blue,
                Math.min(1.0F, red * 1.25F), Math.min(1.0F, green * 1.25F), Math.min(1.0F, blue * 1.25F));
    }

    public static void spawnRune(World world, Rune rune, double x, double y, double z, double vx, double vy, double vz,
                                 float startRed, float startGreen, float startBlue,
                                 float endRed, float endGreen, float endBlue) {
        spawn(world, rune.getSprite(), x, y, z, vx, vy, vz, new EidolonParticleData()
                .lifetime(28)
                .color(startRed, startGreen, startBlue, endRed, endGreen, endBlue)
                .alpha(0.9F, 0.0F)
                .scale(0.22F, 0.07F)
                .spin(randomSpin(world, 0.04F)));
    }

    public static void spawnSlash(World world, double x1, double y1, double z1, double x2, double y2, double z2,
                                  float red, float green, float blue) {
        int count = particleCount(24);
        if (count <= 0) {
            return;
        }
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        for (int i = 0; i < count; i++) {
            double t = count <= 1 ? 1.0D : i / (double) (count - 1);
            double arc = Math.sin(t * Math.PI);
            double x = x1 + dx * t;
            double y = y1 + dy * t + arc * 0.35D;
            double z = z1 + dz * t;
            spawn(world, BEAM, x, y, z, -dx * 0.003D, 0.01D, -dz * 0.003D, new EidolonParticleData()
                    .lifetime(10 + world.rand.nextInt(8))
                    .color(red, green, blue, 1.0F, 1.0F, 0.8F)
                    .alpha(0.9F, 0.0F)
                    .scale(0.18F + (float) arc * 0.18F, 0.02F)
                    .spin(randomSpin(world, 0.18F)));
        }
    }

    public static void spawnGlowingSlash(World world, double x, double y, double z, double vx, double vy, double vz,
                                         EidolonSlashParticle.SlashData data) {
        if (world == null || !world.isRemote || !ClientConfig.visualEffectsEnabled() || particleCount(1) <= 0) {
            return;
        }
        Minecraft.getMinecraft().effectRenderer.addEffect(
                new EidolonSlashParticle(world, sprite(BEAM), x, y, z, vx, vy, vz, data));
    }

    public static void spawnLine(World world, double x1, double y1, double z1, double x2, double y2, double z2,
                                 float red, float green, float blue, boolean arc) {
        int count = particleCount(26);
        if (count <= 0) {
            return;
        }
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        for (int i = 0; i < count; i++) {
            double t = count <= 1 ? 1.0D : i / (double) (count - 1);
            double lift = arc ? Math.sin(t * Math.PI) * 0.25D : 0.0D;
            spawnWisp(world, x1 + dx * t + spread(world, 0.04D), y1 + dy * t + lift + spread(world, 0.04D),
                    z1 + dz * t + spread(world, 0.04D), dx * 0.004D, dy * 0.004D, dz * 0.004D,
                    red, green, blue);
        }
    }

    public static void spawnLineWisp(World world, double x, double y, double z, double targetX, double targetY,
                                     double targetZ, EidolonParticleData data) {
        if (world == null || !world.isRemote || !ClientConfig.visualEffectsEnabled() || particleCount(1) <= 0) {
            return;
        }
        Minecraft.getMinecraft().effectRenderer.addEffect(
                new EidolonLineWispParticle(world, sprite(WISP), x, y, z, targetX, targetY, targetZ, data));
    }

    public static void spawnBurst(World world, double x, double y, double z, float red, float green, float blue,
                                  int baseCount) {
        int count = particleCount(baseCount);
        for (int i = 0; i < count; i++) {
            double vx = spread(world, 0.16D);
            double vy = spread(world, 0.16D);
            double vz = spread(world, 0.16D);
            spawnWisp(world, x + vx * 0.5D, y + vy * 0.5D, z + vz * 0.5D, vx, vy, vz, red, green, blue);
            if (i % 3 == 0) {
                spawnSparkle(world, x, y, z, vx * 0.7D, vy * 0.7D, vz * 0.7D, red, green, blue);
            }
        }
    }

    public static final class ParticleBuilder {
        private final ResourceLocation texture;
        private final EidolonParticleData data = new EidolonParticleData();
        private double vx;
        private double vy;
        private double vz;
        private double maxXSpeed;
        private double maxYSpeed;
        private double maxZSpeed;
        private double maxXDist;
        private double maxYDist;
        private double maxZDist;
        private boolean hasLineTarget;
        private double targetX;
        private double targetY;
        private double targetZ;

        private ParticleBuilder(ResourceLocation texture) {
            this.texture = texture;
        }

        public ParticleBuilder color(float red, float green, float blue) {
            data.color(red, green, blue);
            return this;
        }

        public ParticleBuilder color(float startRed, float startGreen, float startBlue,
                                     float endRed, float endGreen, float endBlue) {
            data.color(startRed, startGreen, startBlue, endRed, endGreen, endBlue);
            return this;
        }

        public ParticleBuilder alpha(float startAlpha, float endAlpha) {
            data.alpha(startAlpha, endAlpha);
            return this;
        }

        public ParticleBuilder scale(float startScale, float endScale) {
            data.scale(startScale, endScale);
            return this;
        }

        public ParticleBuilder lifetime(int lifetime) {
            data.lifetime(lifetime);
            return this;
        }

        public ParticleBuilder spin(float spin) {
            data.spin(spin);
            return this;
        }

        public ParticleBuilder gravity(float gravity) {
            data.gravity(gravity);
            return this;
        }

        public ParticleBuilder enableGravity() {
            data.gravity(0.04F);
            return this;
        }

        public ParticleBuilder fullbright(boolean fullbright) {
            data.fullbright(fullbright);
            return this;
        }

        public ParticleBuilder sourceSmokeCurve(float yDamping) {
            data.sourceSmokeCurve(yDamping);
            return this;
        }

        public ParticleBuilder yDamping(float yDamping) {
            data.yDamping(yDamping);
            return this;
        }

        public ParticleBuilder endSprite(ResourceLocation texture) {
            data.endSprite(sprite(texture));
            return this;
        }

        public ParticleBuilder randomVelocity(double maxSpeed) {
            return randomVelocity(maxSpeed, maxSpeed, maxSpeed);
        }

        public ParticleBuilder randomVelocity(double maxHorizontalSpeed, double maxVerticalSpeed) {
            return randomVelocity(maxHorizontalSpeed, maxVerticalSpeed, maxHorizontalSpeed);
        }

        public ParticleBuilder randomVelocity(double maxXSpeed, double maxYSpeed, double maxZSpeed) {
            this.maxXSpeed = maxXSpeed;
            this.maxYSpeed = maxYSpeed;
            this.maxZSpeed = maxZSpeed;
            return this;
        }

        public ParticleBuilder addVelocity(double vx, double vy, double vz) {
            this.vx += vx;
            this.vy += vy;
            this.vz += vz;
            return this;
        }

        public ParticleBuilder lineTarget(double x, double y, double z) {
            this.hasLineTarget = true;
            this.targetX = x;
            this.targetY = y;
            this.targetZ = z;
            return this;
        }

        public ParticleBuilder setVelocity(double vx, double vy, double vz) {
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            return this;
        }

        public ParticleBuilder randomOffset(double maxDistance) {
            return randomOffset(maxDistance, maxDistance, maxDistance);
        }

        public ParticleBuilder randomOffset(double maxHorizontalDistance, double maxVerticalDistance) {
            return randomOffset(maxHorizontalDistance, maxVerticalDistance, maxHorizontalDistance);
        }

        public ParticleBuilder randomOffset(double maxXDistance, double maxYDistance, double maxZDistance) {
            this.maxXDist = maxXDistance;
            this.maxYDist = maxYDistance;
            this.maxZDist = maxZDistance;
            return this;
        }

        public ParticleBuilder spawn(World world, double x, double y, double z) {
            if (world == null) {
                return this;
            }
            double yaw = world.rand.nextDouble() * Math.PI * 2.0D;
            double pitch = world.rand.nextDouble() * Math.PI - Math.PI / 2.0D;
            double xSpeed = world.rand.nextDouble() * maxXSpeed;
            double ySpeed = world.rand.nextDouble() * maxYSpeed;
            double zSpeed = world.rand.nextDouble() * maxZSpeed;
            double sx = vx + Math.sin(yaw) * Math.cos(pitch) * xSpeed;
            double sy = vy + Math.sin(pitch) * ySpeed;
            double sz = vz + Math.cos(yaw) * Math.cos(pitch) * zSpeed;

            double yaw2 = world.rand.nextDouble() * Math.PI * 2.0D;
            double pitch2 = world.rand.nextDouble() * Math.PI - Math.PI / 2.0D;
            double xDist = world.rand.nextDouble() * maxXDist;
            double yDist = world.rand.nextDouble() * maxYDist;
            double zDist = world.rand.nextDouble() * maxZDist;
            double dx = Math.sin(yaw2) * Math.cos(pitch2) * xDist;
            double dy = Math.sin(pitch2) * yDist;
            double dz = Math.cos(yaw2) * Math.cos(pitch2) * zDist;
            if (hasLineTarget) {
                EidolonParticles.spawnLineWisp(world, x + dx, y + dy, z + dz, targetX + sx, targetY + sy,
                        targetZ + sz, data);
            } else {
                EidolonParticles.spawn(world, texture, x + dx, y + dy, z + dz, sx, sy, sz, data);
            }
            return this;
        }

        public ParticleBuilder repeat(World world, double x, double y, double z, int count) {
            for (int i = 0; i < count; i++) {
                spawn(world, x, y, z);
            }
            return this;
        }
    }

    private static void spawn(World world, ResourceLocation texture, double x, double y, double z,
                              double vx, double vy, double vz, EidolonParticleData data) {
        if (world == null || !world.isRemote || !ClientConfig.visualEffectsEnabled() || particleCount(1) <= 0) {
            return;
        }
        TextureAtlasSprite sprite = sprite(texture);
        Minecraft.getMinecraft().effectRenderer.addEffect(new EidolonParticle(world, sprite, x, y, z, vx, vy, vz, data));
    }

    private static TextureAtlasSprite sprite(ResourceLocation texture) {
        TextureAtlasSprite sprite = SPRITES.get(texture);
        if (sprite == null) {
            TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
            sprite = map.getAtlasSprite(texture.toString());
            if (sprite == null || "missingno".equals(sprite.getIconName())) {
                sprite = SPRITES.get(WISP);
            }
        }
        return sprite;
    }

    private static int particleCount(int baseCount) {
        if (baseCount <= 0) {
            return 0;
        }
        double density = ClientConfig.particleDensity();
        if (density <= 0.0D) {
            return 0;
        }
        return Math.max(1, (int) Math.round(baseCount * density));
    }

    private static double spread(World world, double amount) {
        return (world.rand.nextDouble() - 0.5D) * amount;
    }

    private static float randomSpin(World world, float amount) {
        Random random = world == null ? new Random() : world.rand;
        return (random.nextFloat() - 0.5F) * amount;
    }
}
