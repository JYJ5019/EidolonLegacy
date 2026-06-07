package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MagicKnowledgeSyncPacket implements IMessage {
    public static final int SIGN = 0;
    public static final int RUNE = 1;
    public static final int FACT = 2;

    private int type;
    private String id;
    private boolean known;

    public MagicKnowledgeSyncPacket() {
    }

    public MagicKnowledgeSyncPacket(int type, ResourceLocation id, boolean known) {
        this.type = type;
        this.id = id.toString();
        this.known = known;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = buf.readInt();
        id = ByteBufUtils.readUTF8String(buf);
        known = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type);
        ByteBufUtils.writeUTF8String(buf, id);
        buf.writeBoolean(known);
    }

    public static class Handler implements IMessageHandler<MagicKnowledgeSyncPacket, IMessage> {
        @Override
        public IMessage onMessage(MagicKnowledgeSyncPacket message, MessageContext ctx) {
            Eidolon.proxy.syncMagicKnowledgeClient(message.type, new ResourceLocation(message.id), message.known);
            return null;
        }
    }
}
