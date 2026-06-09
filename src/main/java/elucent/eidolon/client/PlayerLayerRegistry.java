package elucent.eidolon.client;

import elucent.eidolon.client.render.LayerRavenOnShoulder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@SideOnly(Side.CLIENT)
public final class PlayerLayerRegistry {
    private static final PlayerLayerRegistry INSTANCE = new PlayerLayerRegistry();
    private static boolean eventHookRegistered;
    private static boolean layersRegistered;

    private PlayerLayerRegistry() {
    }

    public static void register() {
        if (!eventHookRegistered) {
            MinecraftForge.EVENT_BUS.register(INSTANCE);
            eventHookRegistered = true;
        }
        tryRegisterLayers();
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        tryRegisterLayers();
    }

    private static void tryRegisterLayers() {
        if (layersRegistered) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null) {
            return;
        }

        RenderManager renderManager = minecraft.getRenderManager();
        if (renderManager == null) {
            return;
        }

        Map<String, RenderPlayer> skinMap = renderManager.getSkinMap();
        if (skinMap == null || skinMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, RenderPlayer> entry : skinMap.entrySet()) {
            entry.getValue().addLayer(new LayerRavenOnShoulder(entry.getValue()));
        }
        layersRegistered = true;
    }
}
