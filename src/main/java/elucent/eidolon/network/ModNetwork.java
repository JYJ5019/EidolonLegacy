package elucent.eidolon.network;

import elucent.eidolon.Reference;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class ModNetwork {
    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    private ModNetwork() {
    }

    public static void init() {
        CHANNEL.registerMessage(ResearchActionPacket.Handler.class, ResearchActionPacket.class, 0, Side.SERVER);
        CHANNEL.registerMessage(KnowledgeSyncPacket.Handler.class, KnowledgeSyncPacket.class, 1, Side.CLIENT);
        CHANNEL.registerMessage(DeathbringerSlashEffectPacket.Handler.class, DeathbringerSlashEffectPacket.class, 2, Side.CLIENT);
        CHANNEL.registerMessage(VisualEffectPacket.Handler.class, VisualEffectPacket.class, 3, Side.CLIENT);
        CHANNEL.registerMessage(MagicKnowledgeSyncPacket.Handler.class, MagicKnowledgeSyncPacket.class, 4, Side.CLIENT);
        CHANNEL.registerMessage(SoulSyncPacket.Handler.class, SoulSyncPacket.class, 5, Side.CLIENT);
        CHANNEL.registerMessage(RavenCloakPacket.Handler.class, RavenCloakPacket.class, 6, Side.SERVER);
        CHANNEL.registerMessage(KnowledgeResetPacket.Handler.class, KnowledgeResetPacket.class, 7, Side.CLIENT);
        CHANNEL.registerMessage(AttemptCastPacket.Handler.class, AttemptCastPacket.class, 8, Side.SERVER);
    }
}
