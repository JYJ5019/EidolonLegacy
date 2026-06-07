package elucent.eidolon.research;

import elucent.eidolon.Reference;
import elucent.eidolon.capability.SoulData;
import elucent.eidolon.network.ModNetwork;
import elucent.eidolon.network.SoulSyncPacket;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class KnowledgeEvents {
    private KnowledgeEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        syncPlayerData(event.player);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncPlayerData(event.player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        syncPlayerData(event.player);
    }

    private static void syncPlayerData(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
        SoulData.ensureDefaults(mpPlayer);
        KnowledgeUtil.syncAll(mpPlayer);
        ModNetwork.CHANNEL.sendTo(new SoulSyncPacket(mpPlayer), mpPlayer);
    }

}
