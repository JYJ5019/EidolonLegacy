package elucent.eidolon.network;

import elucent.eidolon.item.RavenCloakItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RavenCloakPacket implements IMessage {
    public static final int ACTION_FLAP = 0;
    public static final int ACTION_DASH = 1;

    private int action;

    public RavenCloakPacket() {
    }

    public RavenCloakPacket(int action) {
        this.action = action;
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
}
