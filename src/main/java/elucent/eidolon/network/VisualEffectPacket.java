package elucent.eidolon.network;

import elucent.eidolon.client.ClientConfig;
import elucent.eidolon.particle.EidolonParticles;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
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
                default:
                    magicBurst(world, packet);
                    break;
            }
        }

        private void crucibleFail(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 0.8F);
            for (int i = 0; i < count(28); i++) {
                spawn(world, EnumParticleTypes.SMOKE_NORMAL, packet.x + spread(world, 0.45D),
                        packet.y + 0.35D + spread(world, 0.12D), packet.z + spread(world, 0.45D),
                        spread(world, 0.025D), 0.02D + world.rand.nextDouble() * 0.03D, spread(world, 0.025D));
            }
            for (int i = 0; i < count(7); i++) {
                spawn(world, EnumParticleTypes.SMOKE_LARGE, packet.x + spread(world, 0.3D),
                        packet.y + 0.45D, packet.z + spread(world, 0.3D), 0.0D, 0.03D, 0.0D);
            }
        }

        private void crucibleSuccess(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.7F, 0.85F);
            playSound(world, packet, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.65F, 1.25F);
            for (int i = 0; i < count(30); i++) {
                spawnColor(world, packet, packet.x + spread(world, 0.5D), packet.y + 0.45D + spread(world, 0.2D),
                        packet.z + spread(world, 0.5D), 0.02D);
            }
            for (int i = 0; i < count(12); i++) {
                spawn(world, EnumParticleTypes.VILLAGER_HAPPY, packet.x + spread(world, 0.45D),
                        packet.y + 0.65D + spread(world, 0.35D), packet.z + spread(world, 0.45D),
                        0.0D, 0.02D, 0.0D);
            }
        }

        private void ritualComplete(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.9F, 1.05F);
            for (int i = 0; i < count(36); i++) {
                double angle = i * Math.PI * 2.0D / 36.0D;
                double radius = 0.25D + (i % 6) * 0.055D;
                double px = packet.x + Math.cos(angle) * radius;
                double pz = packet.z + Math.sin(angle) * radius;
                spawnColor(world, packet, px, packet.y + 0.55D + i * 0.018D, pz, 0.0D);
            }
            for (int i = 0; i < count(8); i++) {
                spawn(world, EnumParticleTypes.FLAME, packet.x + spread(world, 0.25D), packet.y + 0.45D,
                        packet.z + spread(world, 0.25D), 0.0D, 0.04D, 0.0D);
            }
        }

        private void ritualConsume(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.45F, 1.55F);
            lineParticles(world, packet, 24, true);
            for (int i = 0; i < count(8); i++) {
                spawn(world, EnumParticleTypes.SMOKE_NORMAL, packet.x + spread(world, 0.12D), packet.y,
                        packet.z + spread(world, 0.12D), 0.0D, 0.03D, 0.0D);
            }
        }

        private void magicBurst(World world, VisualEffectPacket packet) {
            for (int i = 0; i < count(42); i++) {
                double vx = spread(world, 0.18D);
                double vy = spread(world, 0.18D);
                double vz = spread(world, 0.18D);
                spawnColor(world, packet, packet.x + vx * 0.5D, packet.y + vy * 0.5D, packet.z + vz * 0.5D, 0.0D);
                spawn(world, EnumParticleTypes.CRIT_MAGIC, packet.x, packet.y, packet.z, vx, vy, vz);
            }
        }

        private void lifesteal(World world, VisualEffectPacket packet) {
            lineParticles(world, packet, 30, false);
            for (int i = 0; i < count(5); i++) {
                spawn(world, EnumParticleTypes.HEART, packet.x2 + spread(world, 0.25D), packet.y2 + spread(world, 0.25D),
                        packet.z2 + spread(world, 0.25D), 0.0D, 0.02D, 0.0D);
            }
        }

        private void chilled(World world, VisualEffectPacket packet) {
            for (int i = 0; i < count(36); i++) {
                spawn(world, EnumParticleTypes.SNOWBALL, packet.x + spread(world, 0.6D), packet.y + spread(world, 0.6D),
                        packet.z + spread(world, 0.6D), spread(world, 0.04D), spread(world, 0.04D), spread(world, 0.04D));
            }
            for (int i = 0; i < count(16); i++) {
                spawn(world, EnumParticleTypes.CLOUD, packet.x + spread(world, 0.45D), packet.y + spread(world, 0.45D),
                        packet.z + spread(world, 0.45D), 0.0D, 0.02D, 0.0D);
            }
        }

        private void crystallize(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.7F, 1.4F);
            for (int i = 0; i < count(40); i++) {
                spawn(world, EnumParticleTypes.CRIT_MAGIC, packet.x + spread(world, 0.4D), packet.y + spread(world, 0.6D),
                        packet.z + spread(world, 0.4D), spread(world, 0.07D), world.rand.nextDouble() * 0.08D,
                        spread(world, 0.07D));
            }
        }

        private void ignite(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 0.7F, 0.95F);
            flame(world, packet);
        }

        private void extinguish(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.7F, 1.15F);
            for (int i = 0; i < count(18); i++) {
                spawn(world, EnumParticleTypes.SMOKE_NORMAL, packet.x + spread(world, 0.24D), packet.y + spread(world, 0.16D),
                        packet.z + spread(world, 0.24D), spread(world, 0.03D), 0.03D, spread(world, 0.03D));
            }
        }

        private void flame(World world, VisualEffectPacket packet) {
            for (int i = 0; i < count(18); i++) {
                spawn(world, EnumParticleTypes.FLAME, packet.x + spread(world, 0.22D), packet.y + spread(world, 0.2D),
                        packet.z + spread(world, 0.22D), 0.0D, 0.035D + world.rand.nextDouble() * 0.03D, 0.0D);
                spawnColor(world, packet, packet.x + spread(world, 0.24D), packet.y + spread(world, 0.24D),
                        packet.z + spread(world, 0.24D), 0.0D);
            }
        }

        private void soulfireImpact(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.35F, 1.65F);
            for (int i = 0; i < count(34); i++) {
                EidolonParticles.spawnFlame(world, packet.x + spread(world, 0.38D), packet.y + spread(world, 0.38D),
                        packet.z + spread(world, 0.38D), spread(world, 0.04D), spread(world, 0.04D),
                        spread(world, 0.04D), packet.r, packet.g, packet.b);
                spawn(world, EnumParticleTypes.FLAME, packet.x + spread(world, 0.28D), packet.y + spread(world, 0.28D),
                        packet.z + spread(world, 0.28D), spread(world, 0.04D), spread(world, 0.04D), spread(world, 0.04D));
            }
        }

        private void bonechillImpact(World world, VisualEffectPacket packet) {
            chilled(world, packet);
            magicBurst(world, packet);
        }

        private void necromancerBurst(World world, VisualEffectPacket packet) {
            for (int i = 0; i < count(28); i++) {
                spawn(world, EnumParticleTypes.SPELL_WITCH, packet.x + spread(world, 0.4D), packet.y + spread(world, 0.45D),
                        packet.z + spread(world, 0.4D), packet.r, packet.g, packet.b);
                spawn(world, EnumParticleTypes.SMOKE_NORMAL, packet.x + spread(world, 0.3D), packet.y + spread(world, 0.3D),
                        packet.z + spread(world, 0.3D), spread(world, 0.03D), 0.02D, spread(world, 0.03D));
            }
        }

        private void summonBurst(World world, VisualEffectPacket packet) {
            playSound(world, packet, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 0.25F, 1.45F);
            for (int i = 0; i < count(26); i++) {
                spawn(world, EnumParticleTypes.SMOKE_LARGE, packet.x + spread(world, 0.45D), packet.y + spread(world, 0.35D),
                        packet.z + spread(world, 0.45D), spread(world, 0.04D), 0.04D, spread(world, 0.04D));
                spawnColor(world, packet, packet.x + spread(world, 0.45D), packet.y + spread(world, 0.35D),
                        packet.z + spread(world, 0.45D), 0.0D);
            }
        }

        private void lineParticles(World world, VisualEffectPacket packet, int count, boolean arc) {
            EidolonParticles.spawnLine(world, packet.x, packet.y, packet.z, packet.x2, packet.y2, packet.z2,
                    packet.r, packet.g, packet.b, arc);
            double dx = packet.x2 - packet.x;
            double dy = packet.y2 - packet.y;
            double dz = packet.z2 - packet.z;
            count = count(count);
            for (int i = 0; i < count; i++) {
                double t = count <= 1 ? 1.0D : i / (double) (count - 1);
                double lift = arc ? Math.sin(t * Math.PI) * 0.25D : 0.0D;
                double px = packet.x + dx * t + spread(world, 0.04D);
                double py = packet.y + dy * t + lift + spread(world, 0.04D);
                double pz = packet.z + dz * t + spread(world, 0.04D);
                spawnColor(world, packet, px, py, pz, 0.0D);
                if (i % 3 == 0) {
                    spawn(world, EnumParticleTypes.CRIT_MAGIC, px, py, pz, dx * 0.005D, dy * 0.005D, dz * 0.005D);
                }
            }
        }

        private void spawnColor(World world, VisualEffectPacket packet, double x, double y, double z, double speed) {
            float mix = world.rand.nextFloat();
            double cr = Math.max(0.001D, packet.r + (packet.r2 - packet.r) * mix);
            double cg = Math.max(0.001D, packet.g + (packet.g2 - packet.g) * mix);
            double cb = Math.max(0.001D, packet.b + (packet.b2 - packet.b) * mix);
            EidolonParticles.spawnWisp(world, x, y, z, spread(world, speed), spread(world, speed), spread(world, speed),
                    (float) cr, (float) cg, (float) cb);
            spawn(world, EnumParticleTypes.SPELL_MOB, x, y, z, cr + spread(world, speed), cg, cb + spread(world, speed));
        }

        private void spawn(World world, EnumParticleTypes particle, double x, double y, double z,
                           double vx, double vy, double vz) {
            world.spawnParticle(particle, x, y, z, vx, vy, vz);
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
