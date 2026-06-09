package elucent.eidolon.network;

import elucent.eidolon.client.ClientConfig;
import elucent.eidolon.particle.EidolonParticles;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class VisualEffectPacket implements IMessage {
    public static final int CRUCIBLE_FAIL = 0;
    public static final int CRUCIBLE_SUCCESS = 1;
    public static final int RITUAL_COMPLETE = 2;
    public static final int RITUAL_CONSUME = 3;
    public static final int MAGIC_BURST = 4;
    public static final int LIFESTEAL = 5;
    public static final int CHILLED = 6;
    public static final int CRYSTALLIZE = 7;
    public static final int IGNITE = 8;
    public static final int EXTINGUISH = 9;
    public static final int FLAME = 10;
    public static final int SOULFIRE_IMPACT = 11;
    public static final int BONECHILL_IMPACT = 12;
    public static final int NECROMANCER_BURST = 13;
    public static final int SUMMON_BURST = 14;
    public static final int CHANT_FLAME = 15;
    public static final int CRUCIBLE_STEP = 16;

    private int effect;
    private double x;
    private double y;
    private double z;
    private double x2;
    private double y2;
    private double z2;
    private float r;
    private float g;
    private float b;
    private float r2;
    private float g2;
    private float b2;

    public VisualEffectPacket() {
    }

    public VisualEffectPacket(int effect, double x, double y, double z, double x2, double y2, double z2,
                              float r, float g, float b, float r2, float g2, float b2) {
        this.effect = effect;
        this.x = x;
        this.y = y;
        this.z = z;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.r = r;
        this.g = g;
        this.b = b;
        this.r2 = r2;
        this.g2 = g2;
        this.b2 = b2;
    }

    public static VisualEffectPacket at(int effect, BlockPos pos, float r, float g, float b) {
        return at(effect, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, r, g, b);
    }

    public static VisualEffectPacket at(int effect, double x, double y, double z, float r, float g, float b) {
        return new VisualEffectPacket(effect, x, y, z, x, y, z, r, g, b, Math.min(1.0F, r * 1.35F),
                Math.min(1.0F, g * 1.35F), Math.min(1.0F, b * 1.35F));
    }

    public static VisualEffectPacket at(int effect, double x, double y, double z, float r, float g, float b,
                                        float r2, float g2, float b2) {
        return new VisualEffectPacket(effect, x, y, z, x, y, z, r, g, b, r2, g2, b2);
    }

    public static VisualEffectPacket line(int effect, BlockPos from, BlockPos to, float r, float g, float b) {
        return line(effect, from.getX() + 0.5D, from.getY() + 0.9D, from.getZ() + 0.5D,
                to.getX() + 0.5D, to.getY() + 1.0D, to.getZ() + 0.5D, r, g, b);
    }

    public static VisualEffectPacket line(int effect, double x, double y, double z, double x2, double y2, double z2,
                                          float r, float g, float b) {
        return new VisualEffectPacket(effect, x, y, z, x2, y2, z2, r, g, b,
                Math.min(1.0F, r * 1.35F), Math.min(1.0F, g * 1.35F), Math.min(1.0F, b * 1.35F));
    }

    public static void sendAround(World world, BlockPos pos, VisualEffectPacket packet) {
        sendAround(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, packet);
    }

    public static void sendAround(World world, double x, double y, double z, VisualEffectPacket packet) {
        if (world == null || world.isRemote) {
            return;
        }
        ModNetwork.CHANNEL.sendToAllAround(packet,
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, 48.0D));
    }

    public static void playClient(VisualEffectPacket packet) {
        new Handler().play(packet);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        effect = buf.readInt();
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        x2 = buf.readDouble();
        y2 = buf.readDouble();
        z2 = buf.readDouble();
        r = buf.readFloat();
        g = buf.readFloat();
        b = buf.readFloat();
        r2 = buf.readFloat();
        g2 = buf.readFloat();
        b2 = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(effect);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(x2);
        buf.writeDouble(y2);
        buf.writeDouble(z2);
        buf.writeFloat(r);
        buf.writeFloat(g);
        buf.writeFloat(b);
        buf.writeFloat(r2);
        buf.writeFloat(g2);
        buf.writeFloat(b2);
    }

    public static class Handler implements IMessageHandler<VisualEffectPacket, IMessage> {
        @Override
        public IMessage onMessage(VisualEffectPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> play(message));
            return null;
        }

        private void play(VisualEffectPacket packet) {
            World world = Minecraft.getMinecraft().world;
            if (world == null) {
                return;
            }
            if (!ClientConfig.visualEffectsEnabled()) {
                return;
            }
            switch (packet.effect) {
                case CRUCIBLE_FAIL:
                    crucibleFail(world, packet);
                    break;
                case CRUCIBLE_SUCCESS:
                    crucibleSuccess(world, packet);
                    break;
                case RITUAL_COMPLETE:
                    ritualComplete(world, packet);
                    break;
                case RITUAL_CONSUME:
                    ritualConsume(world, packet);
                    break;
                case MAGIC_BURST:
                    magicBurst(world, packet);
                    break;
                case LIFESTEAL:
                    lifesteal(world, packet);
                    break;
                case CHILLED:
                    chilled(world, packet);
                    break;
                case CRYSTALLIZE:
                    crystallize(world, packet);
                    break;
                case IGNITE:
                    ignite(world, packet);
                    break;
                case EXTINGUISH:
                    extinguish(world, packet);
                    break;
                case FLAME:
                    flame(world, packet);
                    break;
                case SOULFIRE_IMPACT:
                    soulfireImpact(world, packet);
                    break;
                case BONECHILL_IMPACT:
                    bonechillImpact(world, packet);
                    break;
                case NECROMANCER_BURST:
                    necromancerBurst(world, packet);
                    break;
                case SUMMON_BURST:
                    summonBurst(world, packet);
                    break;
                case CHANT_FLAME:
                    chantFlame(world, packet);
                    break;
                case CRUCIBLE_STEP:
                    crucibleStep(world, packet);
                    break;
                default:
                    magicBurst(world, packet);
                    break;
            }
        }

        private void crucibleFail(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
            EidolonParticles.create(EidolonParticles.SMOKE)
                    .alpha(0.38F, 0.0F)
                    .scale(0.58F, 0.18F)
                    .lifetime(56)
                    .randomOffset(0.42D, 0.18D)
                    .randomVelocity(0.022D, 0.018D)
                    .addVelocity(0.0D, 0.035D, 0.0D)
                    .color(0.62F, 0.62F, 0.62F, 0.22F, 0.22F, 0.22F)
                    .fullbright(false)
                    .repeat(world, packet.x, packet.y, packet.z, count(42));
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(0.9F, 0.0F)
                    .scale(0.11F, 0.0F)
                    .lifetime(18)
                    .randomOffset(0.34D, 0.12D)
                    .randomVelocity(0.045D, 0.02D)
                    .color(0.65F, 0.65F, 0.65F, 0.25F, 0.25F, 0.25F)
                    .spin(0.25F)
                    .repeat(world, packet.x, packet.y + 0.18D, packet.z, count(10));
        }

        private void crucibleSuccess(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 0.75F);
            playSound(world, packet, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0F, 0.75F);
            EidolonParticles.create(EidolonParticles.SMOKE)
                    .alpha(0.32F, 0.0F)
                    .scale(0.64F, 0.18F)
                    .lifetime(62)
                    .randomOffset(0.46D, 0.18D)
                    .randomVelocity(0.04D, 0.025D)
                    .addVelocity(0.0D, 0.03D, 0.0D)
                    .color(packet.r, packet.g, packet.b)
                    .sourceSmokeCurve(0.99F)
                    .fullbright(false)
                    .repeat(world, packet.x, packet.y - 0.16D, packet.z, count(44));
            EidolonParticles.create(EidolonParticles.WISP)
                    .alpha(0.75F, 0.0F)
                    .scale(0.24F, 0.03F)
                    .lifetime(28)
                    .randomOffset(0.34D, 0.18D)
                    .randomVelocity(0.07D, 0.06D)
                    .addVelocity(0.0D, 0.04D, 0.0D)
                    .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                    .repeat(world, packet.x, packet.y + 0.06D, packet.z, count(22));
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(1.0F, 0.0F)
                    .scale(0.2F, 0.0F)
                    .lifetime(60)
                    .randomOffset(0.42D, 0.42D)
                    .randomVelocity(0.07D, 0.055D)
                    .addVelocity(0.0D, 0.045D, 0.0D)
                    .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                    .spin(0.3F)
                    .repeat(world, packet.x, packet.y + 0.32D, packet.z, count(28));
        }

        private void crucibleStep(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 0.75F, 1.45F);
            EidolonParticles.create(EidolonParticles.BUBBLE)
                    .alpha(0.95F, 0.0F)
                    .scale(0.18F, 0.26F)
                    .lifetime(18)
                    .randomOffset(0.34D, 0.02D)
                    .randomVelocity(0.035D, 0.02D)
                    .addVelocity(0.0D, 0.035D, 0.0D)
                    .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                    .repeat(world, packet.x, packet.y, packet.z, count(18));
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(1.0F, 0.0F)
                    .scale(0.13F, 0.0F)
                    .lifetime(26)
                    .randomOffset(0.32D, 0.08D)
                    .randomVelocity(0.055D, 0.035D)
                    .addVelocity(0.0D, 0.04D, 0.0D)
                    .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                    .spin(0.25F)
                    .repeat(world, packet.x, packet.y + 0.1D, packet.z, count(16));
            EidolonParticles.create(EidolonParticles.SMOKE)
                    .alpha(0.18F, 0.0F)
                    .scale(0.34F, 0.12F)
                    .lifetime(38)
                    .randomOffset(0.32D, 0.1D)
                    .randomVelocity(0.018D, 0.018D)
                    .addVelocity(0.0D, 0.035D, 0.0D)
                    .color(packet.r, packet.g, packet.b)
                    .sourceSmokeCurve(0.99F)
                    .fullbright(false)
                    .repeat(world, packet.x, packet.y - 0.04D, packet.z, count(12));
        }

        private void ritualComplete(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            ritualFlameBurst(world, packet, 60, world.rand.nextInt(4) + 8);
            ritualCompleteRing(world, packet);
        }

        private void ritualConsume(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, 1.18F);
            ritualConsumeSourceFlash(world, packet);
            sourceLineWisps(world, packet, packet.x2, packet.y2, packet.z2, 42);
            ritualConsumeTrail(world, packet);
            ritualConsumeDestinationFlash(world, packet);
        }

        private void magicBurst(World world, VisualEffectPacket packet) {
            EidolonParticles.create(EidolonParticles.WISP)
                    .alpha(0.5F, 0.0F)
                    .scale(0.25F, 0.0F)
                    .lifetime(20)
                    .randomOffset(0.125D, 0.125D)
                    .randomVelocity(0.0625D, 0.0625D)
                    .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                    .repeat(world, packet.x, packet.y, packet.z, count(12));
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(1.0F, 0.0F)
                    .scale(0.0625F, 0.0F)
                    .lifetime(80)
                    .randomOffset(0.0625D, 0.0D)
                    .randomVelocity(0.125D, 0.125D)
                    .addVelocity(0.0D, 0.25D, 0.0D)
                    .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                    .enableGravity()
                    .spin(0.4F)
                    .repeat(world, packet.x, packet.y, packet.z, count(world.rand.nextInt(4) + 3));
            EidolonParticles.create(EidolonParticles.SMOKE)
                    .alpha(0.25F, 0.0F)
                    .scale(0.375F, 0.0F)
                    .lifetime(20)
                    .randomOffset(0.25D, 0.25D)
                    .randomVelocity(0.015625D, 0.015625D)
                    .color(packet.r2, packet.g2, packet.b2)
                    .fullbright(false)
                    .repeat(world, packet.x, packet.y, packet.z, count(6));
        }

        private void lifesteal(World world, VisualEffectPacket packet) {
            lifestealLineWisps(world, packet);
            lifestealImpact(world, packet);
        }

        private void chilled(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
            int ice = Block.getStateId(Blocks.ICE.getDefaultState());
            for (int i = 0; i < count(5); i++) {
                spawn(world, EnumParticleTypes.BLOCK_CRACK, packet.x, packet.y, packet.z,
                        world.rand.nextGaussian() * 0.05D,
                        world.rand.nextGaussian() * 0.05D,
                        world.rand.nextGaussian() * 0.05D,
                        ice);
            }
        }

        private void crystallize(World world, VisualEffectPacket packet) {
            float r = 247.0F / 255.0F;
            float g = 156.0F / 255.0F;
            float b = 220.0F / 255.0F;
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(1.0F, 0.0F)
                    .scale(0.25F, 0.0F)
                    .lifetime(20)
                    .randomOffset(0.5D, 0.0D)
                    .randomVelocity(0.0D, 0.375D)
                    .addVelocity(0.0D, 0.125D, 0.0D)
                    .color(r, g, b, r, g * 0.5F, b * 1.5F)
                    .spin(0.4F)
                    .repeat(world, packet.x, packet.y - 0.4D, packet.z, count(20));
        }

        private void ignite(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            flameBurst(world, packet, 20, world.rand.nextInt(2) + 2, 0.00625D, 0.01875D, 0.00625D, 40, 0.125D);
        }

        private void extinguish(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
            EidolonParticles.create(EidolonParticles.SMOKE)
                    .alpha(0.125F, 0.0F)
                    .scale(0.3125F, 0.125F)
                    .lifetime(80)
                    .randomOffset(0.375D, 0.125D)
                    .randomVelocity(0.0125D, 0.0125D)
                    .color(0.5F, 0.5F, 0.5F, 0.25F, 0.25F, 0.25F)
                    .fullbright(false)
                    .repeat(world, packet.x, packet.y, packet.z, count(10));
        }

        private void flame(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS, 1.0F, 0.75F);
            flameBurst(world, packet, 40, world.rand.nextInt(4) + 3, 0.003125D, 0.009375D, 0.003125D, 80, 0.25D);
        }

        private void soulfireImpact(World world, VisualEffectPacket packet) {
            magicBurst(world, packet);
            impactSparkles(world, packet, 18, 0.25F, 18);
            projectileImpactWisps(world, packet, 18, 0.42F, 22);
            projectileImpactSmoke(world, packet, 8, 0.42F, 18, 0.22F);
            EidolonParticles.create(EidolonParticles.FLAME)
                    .alpha(0.85F, 0.0F)
                    .scale(0.25F, 0.0625F)
                    .lifetime(16)
                    .randomOffset(0.22D, 0.22D)
                    .randomVelocity(0.055D, 0.055D)
                    .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                    .repeat(world, packet.x, packet.y, packet.z, count(12));
        }

        private void bonechillImpact(World world, VisualEffectPacket packet) {
            magicBurst(world, packet);
            impactSparkles(world, packet, 20, 0.25F, 18);
            projectileImpactWisps(world, packet, 18, 0.48F, 22);
            projectileImpactSmoke(world, packet, 10, 0.5F, 22, 0.28F);
            int ice = Block.getStateId(Blocks.ICE.getDefaultState());
            for (int i = 0; i < count(10); i++) {
                spawn(world, EnumParticleTypes.BLOCK_CRACK,
                        packet.x + spread(world, 0.24D),
                        packet.y + spread(world, 0.24D),
                        packet.z + spread(world, 0.24D),
                        spread(world, 0.06D), spread(world, 0.06D), spread(world, 0.06D), ice);
            }
        }

        private void necromancerBurst(World world, VisualEffectPacket packet) {
            magicBurst(world, packet);
        }

        private void summonBurst(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 0.25F, 1.45F);
            EidolonParticles.create(EidolonParticles.RING)
                    .alpha(0.9F, 0.0F)
                    .scale(1.35F, 0.35F)
                    .lifetime(24)
                    .randomVelocity(0.015D, 0.01D)
                    .color(0.46F, 0.68F, 0.24F, 0.72F, 0.36F, 1.0F)
                    .spin(0.08F)
                    .repeat(world, packet.x, packet.y + 0.08D, packet.z, count(1));
            EidolonParticles.create(EidolonParticles.RING)
                    .alpha(0.62F, 0.0F)
                    .scale(2.0F, 0.72F)
                    .lifetime(32)
                    .randomVelocity(0.01D, 0.006D)
                    .color(0.36F, 0.26F, 0.58F, 0.58F, 0.82F, 0.34F)
                    .spin(-0.045F)
                    .repeat(world, packet.x, packet.y + 0.18D, packet.z, count(1));
            EidolonParticles.create(EidolonParticles.WISP)
                    .alpha(1.0F, 0.0F)
                    .scale(0.58F, 0.05F)
                    .lifetime(32)
                    .randomOffset(0.58D, 0.5D)
                    .randomVelocity(0.2D, 0.16D)
                    .color(0.58F, 0.82F, 0.3F, 0.72F, 0.3F, 1.0F)
                    .repeat(world, packet.x, packet.y + 0.06D, packet.z, count(56));
            EidolonParticles.create(EidolonParticles.SMOKE)
                    .alpha(0.48F, 0.0F)
                    .scale(0.95F, 0.2F)
                    .lifetime(46)
                    .randomOffset(0.78D, 0.46D)
                    .randomVelocity(0.07D, 0.055D)
                    .addVelocity(0.0D, 0.045D, 0.0D)
                    .color(0.22F, 0.16F, 0.3F, 0.4F, 0.48F, 0.22F)
                    .fullbright(false)
                    .repeat(world, packet.x, packet.y + 0.02D, packet.z, count(28));
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(1.0F, 0.0F)
                    .scale(0.2F, 0.0F)
                    .lifetime(54)
                    .randomOffset(0.32D, 0.12D)
                    .randomVelocity(0.25D, 0.18D)
                    .addVelocity(0.0D, 0.18D, 0.0D)
                    .color(0.8F, 1.0F, 0.42F, 0.94F, 0.62F, 1.0F)
                    .enableGravity()
                    .spin(0.48F)
                    .repeat(world, packet.x, packet.y + 0.08D, packet.z, count(26));
            EidolonParticles.create(EidolonParticles.FLAME)
                    .alpha(0.95F, 0.0F)
                    .scale(0.4F, 0.08F)
                    .lifetime(26)
                    .randomOffset(0.44D, 0.3D)
                    .randomVelocity(0.075D, 0.095D)
                    .addVelocity(0.0D, 0.05D, 0.0D)
                    .color(0.44F, 0.66F, 0.22F, 0.78F, 0.34F, 0.96F)
                    .repeat(world, packet.x, packet.y + 0.02D, packet.z, count(22));
            EidolonParticles.create(EidolonParticles.BURST)
                    .alpha(0.72F, 0.0F)
                    .scale(0.62F, 0.14F)
                    .lifetime(16)
                    .randomOffset(0.22D, 0.14D)
                    .randomVelocity(0.12D, 0.1D)
                    .color(0.66F, 0.9F, 0.34F, 0.78F, 0.38F, 1.0F)
                    .spin(0.22F)
                    .repeat(world, packet.x, packet.y + 0.18D, packet.z, count(12));
            summonRingWisps(world, packet, 0.75D, 20, 0.2D, true);
            summonRingWisps(world, packet, 1.15D, 24, 0.34D, true);
            summonRingWisps(world, packet, 1.55D, 28, 0.5D, false);
        }

        private void summonRingWisps(World world, VisualEffectPacket packet, double radius, int amount,
                                     double height, boolean brightCore) {
            double targetY = packet.y + height + 0.32D;
            for (int i = 0; i < count(amount); i++) {
                double angle = Math.PI * 2.0D * i / Math.max(1, amount);
                double x = packet.x + Math.sin(angle) * radius;
                double z = packet.z + Math.cos(angle) * radius;
                EidolonParticles.create(EidolonParticles.WISP)
                        .alpha(brightCore ? 1.0F : 0.82F, 0.0F)
                        .scale((brightCore ? 0.34F : 0.26F) + 0.1F * world.rand.nextFloat(), 0.0F)
                        .lifetime(20 + world.rand.nextInt(10))
                        .randomOffset(0.07D, 0.1D)
                        .randomVelocity(0.045D, 0.04D)
                        .lineTarget(packet.x, targetY, packet.z)
                        .color(brightCore ? 0.68F : 0.56F, brightCore ? 0.9F : 0.34F,
                                brightCore ? 0.36F : 0.86F, 0.72F, 0.28F, 1.0F)
                        .spawn(world, x, packet.y + height + world.rand.nextDouble() * 0.28D, z);
                if (i % 4 == 0) {
                    EidolonParticles.create(EidolonParticles.SPARKLE)
                            .alpha(1.0F, 0.0F)
                            .scale(brightCore ? 0.14F : 0.1F, 0.0F)
                            .lifetime(24)
                            .randomVelocity(0.05D, 0.055D)
                            .addVelocity(0.0D, 0.05D, 0.0D)
                            .color(0.78F, 1.0F, 0.4F, 0.92F, 0.58F, 1.0F)
                            .spin(0.28F)
                            .spawn(world, x, packet.y + height + 0.08D, z);
                }
            }
        }

        private void chantFlame(World world, VisualEffectPacket packet) {
            chantFlameCluster(world, packet.x, packet.y, packet.z, packet.r, packet.g, packet.b, 0.125F, 0.0625F);
            chantFlameCluster(world, packet.x2, packet.y2, packet.z2, packet.r, packet.g, packet.b, 0.1875F, 0.125F);
        }

        private void chantFlameCluster(World world, double x, double y, double z, float r, float g, float b,
                                       float startScale, float endScale) {
            EidolonParticles.create(EidolonParticles.FLAME)
                    .alpha(0.5F, 0.0F)
                    .scale(startScale, endScale)
                    .lifetime(20)
                    .randomOffset(0.01D)
                    .randomVelocity(0.0025D)
                    .addVelocity(0.0D, 0.005D, 0.0D)
                    .color(r, g, b, r, g * 0.5F, b * 1.5F)
                    .repeat(world, x, y, z, count(8));
        }

        private void impactSparkles(World world, VisualEffectPacket packet, int amount, float scale, int lifetime) {
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(1.0F, 0.0F)
                    .scale(scale, 0.0F)
                    .lifetime(lifetime)
                    .randomOffset(0.12D, 0.12D)
                    .randomVelocity(0.08D, 0.08D)
                    .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                    .spin(0.25F)
                    .repeat(world, packet.x, packet.y, packet.z, count(amount));
        }

        private void projectileImpactWisps(World world, VisualEffectPacket packet, int amount, float scale, int lifetime) {
            EidolonParticles.create(EidolonParticles.WISP)
                    .alpha(0.95F, 0.0F)
                    .scale(scale, 0.0625F)
                    .lifetime(lifetime)
                    .randomOffset(0.3D, 0.3D)
                    .randomVelocity(0.085D, 0.085D)
                    .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                    .repeat(world, packet.x, packet.y, packet.z, count(amount));
        }

        private void projectileImpactSmoke(World world, VisualEffectPacket packet, int amount, float scale,
                                           int lifetime, float alpha) {
            EidolonParticles.create(EidolonParticles.SMOKE)
                    .alpha(alpha, 0.0F)
                    .scale(scale, 0.125F)
                    .lifetime(lifetime)
                    .randomOffset(0.28D, 0.22D)
                    .randomVelocity(0.025D, 0.03D)
                    .addVelocity(0.0D, 0.0125D, 0.0D)
                    .color(packet.r2, packet.g2, packet.b2, packet.r, packet.g, packet.b)
                    .fullbright(false)
                    .repeat(world, packet.x, packet.y, packet.z, count(amount));
        }

        private void sourceLineWisps(World world, VisualEffectPacket packet, double targetX, double targetY,
                                     double targetZ, int amount) {
            for (int i = 0; i < count(amount); i++) {
                boolean brightCore = i % 3 == 0;
                EidolonParticles.create(EidolonParticles.WISP)
                        .alpha(brightCore ? 1.0F : 0.82F, 0.0F)
                        .scale((brightCore ? 0.38F : 0.28F) + 0.1F * world.rand.nextFloat(), 0.0F)
                        .lifetime(24 + world.rand.nextInt(10))
                        .randomOffset(0.24D, 0.12D)
                        .randomVelocity(0.055D, 0.04D)
                        .lineTarget(targetX, targetY + 0.04D, targetZ)
                        .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                        .spawn(world, packet.x, packet.y + world.rand.nextDouble() * 0.08D, packet.z);
            }
        }

        private void ritualConsumeSourceFlash(World world, VisualEffectPacket packet) {
            EidolonParticles.create(EidolonParticles.WISP)
                    .alpha(0.95F, 0.0F)
                    .scale(0.34F, 0.05F)
                    .lifetime(20)
                    .randomOffset(0.26D, 0.16D)
                    .randomVelocity(0.06D, 0.045D)
                    .addVelocity(0.0D, 0.035D, 0.0D)
                    .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                    .repeat(world, packet.x, packet.y, packet.z, count(16));
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(1.0F, 0.0F)
                    .scale(0.17F, 0.0F)
                    .lifetime(24)
                    .randomOffset(0.24D, 0.1D)
                    .randomVelocity(0.09D, 0.05D)
                    .addVelocity(0.0D, 0.055D, 0.0D)
                    .color(packet.r2, packet.g2, packet.b2, packet.r, packet.g, packet.b)
                    .spin(0.35F)
                    .repeat(world, packet.x, packet.y + 0.06D, packet.z, count(12));
            EidolonParticles.create(EidolonParticles.SMOKE)
                    .alpha(0.2F, 0.0F)
                    .scale(0.3F, 0.08F)
                    .lifetime(30)
                    .randomOffset(0.22D, 0.08D)
                    .randomVelocity(0.018D, 0.018D)
                    .addVelocity(0.0D, 0.025D, 0.0D)
                    .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                    .sourceSmokeCurve(0.99F)
                    .fullbright(false)
                    .repeat(world, packet.x, packet.y - 0.04D, packet.z, count(8));
            EidolonParticles.create(EidolonParticles.RING)
                    .alpha(0.68F, 0.0F)
                    .scale(0.62F, 0.08F)
                    .lifetime(16)
                    .randomVelocity(0.006D, 0.004D)
                    .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                    .spin(0.08F)
                    .spawn(world, packet.x, packet.y + 0.04D, packet.z);
        }

        private void ritualConsumeTrail(World world, VisualEffectPacket packet) {
            double dx = packet.x2 - packet.x;
            double dy = packet.y2 - packet.y;
            double dz = packet.z2 - packet.z;
            int amount = count(22);
            for (int i = 0; i < amount; i++) {
                double t = (i + world.rand.nextDouble()) / Math.max(1, amount);
                double lift = Math.sin(t * Math.PI) * 0.28D;
                EidolonParticles.create(EidolonParticles.SPARKLE)
                        .alpha(0.9F, 0.0F)
                        .scale(0.09F, 0.0F)
                        .lifetime(18 + world.rand.nextInt(10))
                        .randomOffset(0.09D, 0.09D)
                        .randomVelocity(0.04D, 0.03D)
                        .color(packet.r2, packet.g2, packet.b2, packet.r, packet.g, packet.b)
                        .spin(0.25F)
                        .spawn(world, packet.x + dx * t, packet.y + dy * t + lift, packet.z + dz * t);
            }
        }

        private void ritualConsumeDestinationFlash(World world, VisualEffectPacket packet) {
            EidolonParticles.create(EidolonParticles.WISP)
                    .alpha(0.82F, 0.0F)
                    .scale(0.28F, 0.04F)
                    .lifetime(18)
                    .randomOffset(0.28D, 0.12D)
                    .randomVelocity(0.05D, 0.045D)
                    .addVelocity(0.0D, 0.03D, 0.0D)
                    .color(packet.r2, packet.g2, packet.b2, packet.r, packet.g, packet.b)
                    .repeat(world, packet.x2, packet.y2, packet.z2, count(12));
            EidolonParticles.create(EidolonParticles.FLAME)
                    .alpha(0.72F, 0.0F)
                    .scale(0.22F, 0.05F)
                    .lifetime(18)
                    .randomOffset(0.24D, 0.12D)
                    .randomVelocity(0.012D, 0.035D)
                    .addVelocity(0.0D, 0.028D, 0.0D)
                    .color(packet.r, packet.g, packet.b, packet.r, packet.g * 0.5F, packet.b * 1.5F)
                    .repeat(world, packet.x2, packet.y2 - 0.04D, packet.z2, count(6));
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(1.0F, 0.0F)
                    .scale(0.12F, 0.0F)
                    .lifetime(24)
                    .randomOffset(0.18D, 0.08D)
                    .randomVelocity(0.07D, 0.05D)
                    .addVelocity(0.0D, 0.06D, 0.0D)
                    .color(packet.r2, packet.g2, packet.b2, packet.r, packet.g, packet.b)
                    .spin(0.35F)
                    .repeat(world, packet.x2, packet.y2 + 0.04D, packet.z2, count(8));
        }

        private void lifestealLineWisps(World world, VisualEffectPacket packet) {
            for (int i = 0; i < count(18); i++) {
                boolean brightCore = i % 3 == 0;
                EidolonParticles.create(EidolonParticles.WISP)
                        .alpha(brightCore ? 1.0F : 0.85F, 0.0F)
                        .scale((brightCore ? 0.34F : 0.27F) + 0.1F * world.rand.nextFloat(), 0.0F)
                        .lifetime(20 + world.rand.nextInt(6))
                        .randomOffset(0.25D, 0.25D)
                        .randomVelocity(0.075D, 0.075D)
                        .lineTarget(packet.x2, packet.y2, packet.z2)
                        .color(1.0F, 0.08F, 0.12F, 1.0F, 0.0F, 0.28F)
                        .spawn(world, packet.x, packet.y, packet.z);
            }
        }

        private void lifestealImpact(World world, VisualEffectPacket packet) {
            EidolonParticles.create(EidolonParticles.WISP)
                    .alpha(0.9F, 0.0F)
                    .scale(0.18F, 0.02F)
                    .lifetime(18)
                    .randomOffset(0.22D, 0.18D)
                    .randomVelocity(0.035D, 0.035D)
                    .color(1.0F, 0.04F, 0.08F, 0.55F, 0.0F, 0.16F)
                    .repeat(world, packet.x, packet.y, packet.z, count(10));
            EidolonParticles.create(EidolonParticles.SMOKE)
                    .alpha(0.28F, 0.0F)
                    .scale(0.28F, 0.08F)
                    .lifetime(24)
                    .randomOffset(0.22D, 0.12D)
                    .randomVelocity(0.025D, 0.015D)
                    .color(0.45F, 0.02F, 0.04F, 0.12F, 0.0F, 0.03F)
                    .fullbright(false)
                    .repeat(world, packet.x, packet.y, packet.z, count(6));
            for (int i = 0; i < count(3); i++) {
                spawn(world, EnumParticleTypes.HEART, packet.x2 + spread(world, 0.25D),
                        packet.y2 + spread(world, 0.2D), packet.z2 + spread(world, 0.25D),
                        0.0D, 0.02D, 0.0D);
            }
        }

        private void ritualFlameBurst(World world, VisualEffectPacket packet, int sparkleLifetime, int sparkleCount) {
            EidolonParticles.create(EidolonParticles.FLAME)
                    .alpha(0.9F, 0.0F)
                    .scale(0.62F, 0.2F)
                    .lifetime(48)
                    .randomOffset(0.65D, 0.18D)
                    .randomVelocity(0.01D, 0.018D)
                    .addVelocity(0.0D, 0.01D, 0.0D)
                    .color(packet.r, packet.g, packet.b, packet.r, packet.g * 0.5F, packet.b * 1.5F)
                    .repeat(world, packet.x, packet.y, packet.z, count(18));
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(1.0F, 0.0F)
                    .scale(0.14F, 0.0F)
                    .lifetime(sparkleLifetime)
                    .randomOffset(0.25D, 0.08D)
                    .randomVelocity(0.18D, 0.08D)
                    .addVelocity(0.0D, 0.2D, 0.0D)
                    .color(packet.r, packet.g * 1.5F, packet.b * 2.0F, packet.r, packet.g, packet.b)
                    .enableGravity()
                    .spin(0.4F)
                    .repeat(world, packet.x, packet.y, packet.z, count(sparkleCount));
        }

        private void ritualCompleteRing(World world, VisualEffectPacket packet) {
            for (int i = 0; i < count(32); i++) {
                double angle = i * Math.PI * 2.0D / 32.0D;
                double radius = 0.75D + (i % 4) * 0.18D;
                double x = packet.x + Math.cos(angle) * radius;
                double z = packet.z + Math.sin(angle) * radius;
                EidolonParticles.create(EidolonParticles.WISP)
                        .alpha(0.9F, 0.0F)
                        .scale(0.28F, 0.0F)
                        .lifetime(24 + world.rand.nextInt(10))
                        .randomOffset(0.08D, 0.08D)
                        .randomVelocity(0.05D, 0.04D)
                        .addVelocity(0.0D, 0.05D, 0.0D)
                        .color(packet.r, packet.g, packet.b, packet.r2, packet.g2, packet.b2)
                        .spawn(world, x, packet.y + 0.05D + (i % 3) * 0.12D, z);
            }
            EidolonParticles.create(EidolonParticles.SMOKE)
                    .alpha(0.32F, 0.0F)
                    .scale(0.5F, 0.08F)
                    .lifetime(36)
                    .randomOffset(0.85D, 0.2D)
                    .randomVelocity(0.035D, 0.025D)
                    .addVelocity(0.0D, 0.02D, 0.0D)
                    .color(packet.r2, packet.g2, packet.b2, packet.r, packet.g, packet.b)
                    .fullbright(false)
                    .repeat(world, packet.x, packet.y, packet.z, count(10));
        }

        private void flameBurst(World world, VisualEffectPacket packet, int flameLifetime, int sparkleCount,
                                double flameHSpeed, double flameVSpeed, double flameLift, int sparkleLifetime,
                                double sparkleLift) {
            EidolonParticles.create(EidolonParticles.FLAME)
                    .alpha(0.75F, 0.0F)
                    .scale(0.5F, 0.25F)
                    .lifetime(flameLifetime)
                    .randomOffset(0.5D, 0.125D)
                    .randomVelocity(flameHSpeed, flameVSpeed)
                    .addVelocity(0.0D, flameLift, 0.0D)
                    .color(packet.r, packet.g, packet.b, packet.r, packet.g * 0.5F, packet.b * 1.5F)
                    .repeat(world, packet.x, packet.y, packet.z, count(10));
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(1.0F, 0.0F)
                    .scale(0.0625F, 0.0F)
                    .lifetime(sparkleLifetime)
                    .randomOffset(0.0625D, 0.0D)
                    .randomVelocity(0.125D, sparkleLift == 0.125D ? 0.0D : 0.125D)
                    .addVelocity(0.0D, sparkleLift, 0.0D)
                    .color(packet.r, packet.g * 1.5F, packet.b * 2.0F, packet.r, packet.g, packet.b)
                    .enableGravity()
                    .spin(0.4F)
                    .repeat(world, packet.x, packet.y, packet.z, count(sparkleCount));
        }

        private void spawn(World world, EnumParticleTypes particle, double x, double y, double z,
                           double vx, double vy, double vz) {
            world.spawnParticle(particle, x, y, z, vx, vy, vz);
        }

        private void spawn(World world, EnumParticleTypes particle, double x, double y, double z,
                           double vx, double vy, double vz, int... args) {
            world.spawnParticle(particle, x, y, z, vx, vy, vz, args);
        }

        private void playSound(World world, VisualEffectPacket packet, net.minecraft.util.SoundEvent sound,
                               SoundCategory category, float volume, float pitch) {
            world.playSound(Minecraft.getMinecraft().player, packet.x, packet.y, packet.z, sound, category, volume, pitch);
        }

        private double spread(World world, double amount) {
            return (world.rand.nextDouble() - 0.5D) * amount;
        }

        private int count(int baseCount) {
            if (baseCount <= 0) {
                return 0;
            }
            double density = ClientConfig.particleDensity();
            if (density <= 0.0D) {
                return 0;
            }
            return Math.max(1, (int) Math.round(baseCount * density));
        }
    }
}
