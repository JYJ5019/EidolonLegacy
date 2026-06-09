package elucent.eidolon.network;

import elucent.eidolon.item.RavenCloakItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RavenCloakPacket implements IMessage {
    public static final int ACTION_FLAP = 0;
    public static final int ACTION_DASH = 1;

    private int action;

    public RavenCloakPacket() {
    }

    public RavenCloakPacket(int action) {
        this.action = action;
    }

    public static Sync sync(EntityPlayer player, ItemStack stack, boolean flapped) {
        return new Sync(player, stack, flapped);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        action = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(action);
    }

    public static class Handler implements IMessageHandler<RavenCloakPacket, IMessage> {
        @Override
        public IMessage onMessage(RavenCloakPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                if (chest.isEmpty() || !(chest.getItem() instanceof RavenCloakItem)) {
                    return;
                }
                if (message.action == ACTION_FLAP) {
                    RavenCloakItem.tryFlap(player, chest);
                } else if (message.action == ACTION_DASH) {
                    RavenCloakItem.tryStartDash(player, chest);
                }
            });
            return null;
        }
    }

    public static class Sync implements IMessage {
        private int entityId;
        private boolean flying;
        private int dashTicks;
        private int flapCharges;
        private boolean flapped;

        public Sync() {
        }

        private Sync(EntityPlayer player, ItemStack stack, boolean flapped) {
            this.entityId = player == null ? -1 : player.getEntityId();
            this.flying = RavenCloakItem.isFlying(stack);
            this.dashTicks = RavenCloakItem.getDashTicks(stack);
            this.flapCharges = RavenCloakItem.getFlapCharges(stack);
            this.flapped = flapped;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            entityId = buf.readInt();
            flying = buf.readBoolean();
            dashTicks = buf.readInt();
            flapCharges = buf.readInt();
            flapped = buf.readBoolean();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(entityId);
            buf.writeBoolean(flying);
            buf.writeInt(dashTicks);
            buf.writeInt(flapCharges);
            buf.writeBoolean(flapped);
        }
    }

    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler<Sync, IMessage> {
        @Override
        public IMessage onMessage(Sync message, MessageContext ctx) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> handleClient(message));
            return null;
        }

        private static void handleClient(Sync message) {
            elucent.eidolon.client.render.RavenCloakRenderState.applySync(message.entityId, message.flying, message.dashTicks,
                    message.flapCharges, message.flapped);
        }
    }
}
