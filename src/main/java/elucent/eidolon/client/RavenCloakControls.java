package elucent.eidolon.client;

import elucent.eidolon.network.ModNetwork;
import elucent.eidolon.network.RavenCloakPacket;
import elucent.eidolon.item.RavenCloakItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class RavenCloakControls {
    private static boolean registered;
    private static int jumpTicks;
    private static boolean wasJumping;

    private RavenCloakControls() {
    }

    public static void init() {
        if (registered) {
            return;
        }
        registered = true;
        MinecraftForge.EVENT_BUS.register(RavenCloakControls.class);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.player;
        if (player == null) {
            jumpTicks = 0;
            wasJumping = false;
            return;
        }
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chest.isEmpty() || !(chest.getItem() instanceof RavenCloakItem) || !RavenCloakItem.canFlap(player)) {
            if (player.onGround) {
                jumpTicks = 0;
            }
            wasJumping = player.onGround || minecraft.gameSettings.keyBindJump.isKeyDown();
            return;
        }
        boolean jumping = minecraft.gameSettings.keyBindJump.isKeyDown();
        if (jumping && (!wasJumping || jumpTicks > 0)) {
            jumpTicks++;
            if (jumpTicks > 20) {
                jumpTicks = 20;
            }
        } else if (wasJumping && jumpTicks > 0) {
            if (jumpTicks >= 20 && !RavenCloakItem.isDashing(chest)) {
                ModNetwork.CHANNEL.sendToServer(new RavenCloakPacket(RavenCloakPacket.ACTION_DASH));
            } else {
                ModNetwork.CHANNEL.sendToServer(new RavenCloakPacket(RavenCloakPacket.ACTION_FLAP));
            }
            jumpTicks = 0;
        }
        if (player.onGround) {
            jumpTicks = 0;
        }
        wasJumping = player.onGround || jumping;
    }
}
