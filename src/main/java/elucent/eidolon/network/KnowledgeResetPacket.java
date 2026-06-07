package elucent.eidolon.network;

import elucent.eidolon.Eidolon;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class KnowledgeResetPacket implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<KnowledgeResetPacket, IMessage> {
        @Override
        public IMessage onMessage(KnowledgeResetPacket message, MessageContext ctx) {
            Eidolon.proxy.resetKnowledgeClient();
            return null;
        }
    }
}
