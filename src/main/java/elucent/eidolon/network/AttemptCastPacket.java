package elucent.eidolon.network;

import elucent.eidolon.entity.ChantCasterEntity;
import elucent.eidolon.spell.Rune;
import elucent.eidolon.spell.Runes;
import elucent.eidolon.util.KnowledgeUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class AttemptCastPacket implements IMessage {
    private final List<ResourceLocation> runes = new ArrayList<>();

    public AttemptCastPacket() {
    }

    public AttemptCastPacket(List<Rune> runes) {
        for (Rune rune : runes) {
            if (this.runes.size() >= 18) {
                break;
            }
            if (rune != null) {
                this.runes.add(rune.getRegistryName());
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        runes.clear();
        int count = Math.max(0, buf.readInt());
        for (int i = 0; i < count; i++) {
            ResourceLocation rune = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
            if (i < 18) {
                runes.add(rune);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        int count = Math.min(18, runes.size());
        buf.writeInt(count);
        for (int i = 0; i < count; i++) {
            ByteBufUtils.writeUTF8String(buf, runes.get(i).toString());
        }
    }

    public static class Handler implements IMessageHandler<AttemptCastPacket, IMessage> {
        @Override
        public IMessage onMessage(AttemptCastPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private void handle(AttemptCastPacket message, EntityPlayerMP player) {
            List<Rune> decoded = new ArrayList<>();
            for (ResourceLocation id : message.runes) {
                Rune rune = Runes.find(id);
                if (rune == null || (!player.capabilities.isCreativeMode && !KnowledgeUtil.knowsRune(player, rune))) {
                    return;
                }
                decoded.add(rune);
            }
            if (decoded.isEmpty()) {
                return;
            }
            Vec3d look = player.getLookVec();
            Vec3d placement = player.getPositionVector()
                    .add(0.0D, player.height * 2.0D / 3.0D, 0.0D)
                    .add(look.scale(0.5D));
            ChantCasterEntity caster = new ChantCasterEntity(player.world, player, decoded, look);
            caster.setPosition(placement.x, placement.y, placement.z);
            player.world.spawnEntity(caster);
        }
    }
}
