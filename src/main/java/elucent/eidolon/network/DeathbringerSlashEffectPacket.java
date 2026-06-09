package elucent.eidolon.network;

import elucent.eidolon.client.ClientConfig;
import elucent.eidolon.particle.EidolonSlashParticle;
import elucent.eidolon.particle.EidolonParticles;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
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
    private int color1;
    private int color2;
    private int color3;
    private int color4;

    public DeathbringerSlashEffectPacket() {
    }

    public DeathbringerSlashEffectPacket(double x1, double y1, double z1, double x2, double y2, double z2) {
        this(x1, y1, z1, x2, y2, z2, packColor(255, 33, 26, 23), packColor(255, 10, 10, 11),
                packColor(255, 161, 255, 123), packColor(255, 194, 171, 70));
    }

    public DeathbringerSlashEffectPacket(double x1, double y1, double z1, double x2, double y2, double z2,
                                         int color1, int color2, int color3, int color4) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.color4 = color4;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x1 = buf.readDouble();
        y1 = buf.readDouble();
        z1 = buf.readDouble();
        x2 = buf.readDouble();
        y2 = buf.readDouble();
        z2 = buf.readDouble();
        if (buf.isReadable(16)) {
            color1 = buf.readInt();
            color2 = buf.readInt();
            color3 = buf.readInt();
            color4 = buf.readInt();
        } else {
            color1 = packColor(255, 33, 26, 23);
            color2 = packColor(255, 10, 10, 11);
            color3 = packColor(255, 161, 255, 123);
            color4 = packColor(255, 194, 171, 70);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x1);
        buf.writeDouble(y1);
        buf.writeDouble(z1);
        buf.writeDouble(x2);
        buf.writeDouble(y2);
        buf.writeDouble(z2);
        buf.writeInt(color1);
        buf.writeInt(color2);
        buf.writeInt(color3);
        buf.writeInt(color4);
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
            float roll = (float) (Math.PI / 6.0D - world.rand.nextFloat() * (Math.PI * 7.0D / 6.0D));
            float scale = 1.0F + world.rand.nextFloat() * 0.2F;
            double dx = message.x2 - message.x1;
            double dz = message.z2 - message.z1;
            float yaw = (float) Math.atan2(dx, dz);
            float pitch = (float) Math.atan2(message.y2 - message.y1, Math.sqrt(dx * dx + dz * dz));
            float sy = MathHelper.sin(yaw);
            float cy = MathHelper.cos(yaw);
            float sp = MathHelper.sin(pitch);
            float cp = MathHelper.cos(pitch);
            float sr = MathHelper.sin(yaw + (float) Math.PI / 2.0F);
            float cr = MathHelper.cos(yaw + (float) Math.PI / 2.0F);
            float su = MathHelper.sin(pitch + (float) Math.PI / 2.0F);
            float cu = MathHelper.cos(pitch + (float) Math.PI / 2.0F);
            float axisScale = 0.5F;
            float xAxisX = axisScale * sr * cp;
            float xAxisY = 0.0F;
            float xAxisZ = axisScale * cr * cp;
            float yAxisX = axisScale * sy * cu;
            float yAxisY = axisScale * su;
            float yAxisZ = axisScale * cy * cu;
            float zAxisX = axisScale * sy * cp;
            float zAxisY = axisScale * sp;
            float zAxisZ = axisScale * cy * cp;
            float cosRoll = MathHelper.cos(roll);
            float sinRoll = MathHelper.sin(roll);
            float rotatedXAxisX = xAxisX * cosRoll - yAxisX * sinRoll;
            float rotatedXAxisY = xAxisY * cosRoll - yAxisY * sinRoll;
            float rotatedXAxisZ = xAxisZ * cosRoll - yAxisZ * sinRoll;
            yAxisX = xAxisX * sinRoll + yAxisX * cosRoll;
            yAxisY = xAxisY * sinRoll + yAxisY * cosRoll;
            yAxisZ = xAxisZ * sinRoll + yAxisZ * cosRoll;
            xAxisX = rotatedXAxisX;
            xAxisY = rotatedXAxisY;
            xAxisZ = rotatedXAxisZ;
            float[] primary1 = unpack(message.color1);
            float[] primary2 = unpack(message.color2);
            float[] highlight1 = unpack(message.color3);
            float[] highlight2 = unpack(message.color4);

            for (float i = 0; i < 6; i++) {
                float c = (i + 0.5F) / 6.0F;
                float angle = (float) Math.toRadians(-75.0F + c * 150.0F);
                float angleSin = MathHelper.sin(angle);
                float angleCos = MathHelper.cos(angle);
                float smokeDx = angleSin * xAxisX + angleCos * zAxisX;
                float smokeDy = angleSin * xAxisY + angleCos * zAxisY;
                float smokeDz = angleSin * xAxisZ + angleCos * zAxisZ;
                EidolonParticles.create(EidolonParticles.SMOKE)
                        .randomVelocity(0.025D)
                        .addVelocity(smokeDx * 0.25D, smokeDy * 0.25D, smokeDz * 0.25D)
                        .color(33.0F / 255.0F, 26.0F / 255.0F, 23.0F / 255.0F,
                                10.0F / 255.0F, 10.0F / 255.0F, 12.0F / 255.0F)
                        .alpha(0.125F, 0.0F)
                        .randomOffset(0.1D)
                        .scale(0.375F, 0.125F)
                        .fullbright(false)
                        .repeat(world, message.x2 - sy * cp + smokeDx, message.y2 - sp + smokeDy,
                                message.z2 - cy * cp + smokeDz, particleCount(4));
            }
            spawnSlash(world, message, roll, scale, primary1, primary2, 0.9F, 250.0F, 1.0F, 0.0F, 11);
            spawnSlash(world, message, roll, scale, primary1, primary2, 0.8F, 250.0F, 0.75F, 0.0F, 11);
            spawnSlash(world, message, roll, scale, primary1, primary2, 0.7F, 250.0F, 0.5F, 0.0F, 13);
            spawnSlash(world, message, roll, scale, highlight1, highlight2, 0.8F, 210.0F, 0.625F, 0.75F, 8);
            spawnSlash(world, message, roll, scale, highlight1, highlight2, 0.9F, 210.0F, 0.25F, 0.625F, 10);
        }

        private void spawnSlash(World world, DeathbringerSlashEffectPacket message, float roll, float scale,
                                float[] startColor, float[] endColor, float radius, float angle, float width,
                                float highlight, int lifetime) {
            EidolonParticles.spawnGlowingSlash(world, message.x2, message.y2, message.z2, 0.0D, 0.0D, 0.0D,
                    EidolonSlashParticle.data()
                            .lookAt(message.x1, message.y1, message.z1, message.x2, message.y2, message.z2)
                            .color(startColor[0], startColor[1], startColor[2], endColor[0], endColor[1], endColor[2])
                            .radius(radius * scale)
                            .angle(angle)
                            .width(width * scale)
                            .highlight(highlight)
                            .roll(roll)
                            .lifetime(lifetime));
        }

        private int particleCount(int baseCount) {
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

    private static int packColor(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private static float[] unpack(int color) {
        return new float[] {
                ((color >> 16) & 255) / 255.0F,
                ((color >> 8) & 255) / 255.0F,
                (color & 255) / 255.0F
        };
    }
}
