package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import elucent.eidolon.capability.SoulData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SoulSyncPacket implements IMessage {
    private float maxMagic;
    private float magic;
    private float maxEtherealHealth;
    private float etherealHealth;

    public SoulSyncPacket() {
    }

    public SoulSyncPacket(EntityPlayer player) {
        SoulData.ensureDefaults(player);
        this.maxMagic = SoulData.getMaxMagic(player);
        this.magic = SoulData.getMagic(player);
        this.maxEtherealHealth = SoulData.getMaxEtherealHealth(player);
        this.etherealHealth = SoulData.getEtherealHealth(player);
    }

    public SoulSyncPacket(float maxMagic, float magic, float maxEtherealHealth, float etherealHealth) {
        this.maxMagic = maxMagic;
        this.magic = magic;
        this.maxEtherealHealth = maxEtherealHealth;
        this.etherealHealth = etherealHealth;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        maxMagic = buf.readFloat();
        magic = buf.readFloat();
        maxEtherealHealth = buf.readFloat();
        etherealHealth = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(maxMagic);
        buf.writeFloat(magic);
        buf.writeFloat(maxEtherealHealth);
        buf.writeFloat(etherealHealth);
    }

    public static class Handler implements IMessageHandler<SoulSyncPacket, IMessage> {
        @Override
        public IMessage onMessage(SoulSyncPacket message, MessageContext ctx) {
            Eidolon.proxy.syncSoulClient(message.maxMagic, message.magic,
                    message.maxEtherealHealth, message.etherealHealth);
            return null;
        }
    }
}
