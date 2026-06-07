package elucent.eidolon.network;

import elucent.eidolon.particle.EidolonParticles;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DeathbringerSlashEffectPacket implements IMessage {
    private double x1;
    private double y1;
    private double z1;
    private double x2;
    private double y2;
    private double z2;

    public DeathbringerSlashEffectPacket() {
    }

    public DeathbringerSlashEffectPacket(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x1 = buf.readDouble();
        y1 = buf.readDouble();
        z1 = buf.readDouble();
        x2 = buf.readDouble();
        y2 = buf.readDouble();
        z2 = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x1);
        buf.writeDouble(y1);
        buf.writeDouble(z1);
        buf.writeDouble(x2);
        buf.writeDouble(y2);
        buf.writeDouble(z2);
    }

    public static class Handler implements IMessageHandler<DeathbringerSlashEffectPacket, IMessage> {
        @Override
        public IMessage onMessage(DeathbringerSlashEffectPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> spawnParticles(message));
            return null;
        }

        private void spawnParticles(DeathbringerSlashEffectPacket message) {
            World world = Minecraft.getMinecraft().world;
            if (world == null) {
                return;
            }
            double dx = message.x2 - message.x1;
            double dy = message.y2 - message.y1;
            double dz = message.z2 - message.z1;
            double len = Math.max(0.001D, Math.sqrt(dx * dx + dy * dy + dz * dz));
            dx /= len;
            dy /= len;
            dz /= len;
            for (int i = 0; i < 18; i++) {
                double t = i / 17.0D;
                double arc = Math.sin(t * Math.PI);
                double x = message.x1 + (message.x2 - message.x1) * t;
                double y = message.y1 + (message.y2 - message.y1) * t + arc * 0.35D;
                double z = message.z1 + (message.z2 - message.z1) * t;
                if (i == 0) {
                    EidolonParticles.spawnSlash(world, message.x1, message.y1, message.z1,
                            message.x2, message.y2, message.z2, 0.63F, 1.0F, 0.48F);
                }
                world.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, 0.63D, 1.0D, 0.48D);
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - dz * arc * 0.25D, y, z + dx * arc * 0.25D,
                        -dx * 0.02D, -0.01D, -dz * 0.02D);
            }
        }
    }
}
