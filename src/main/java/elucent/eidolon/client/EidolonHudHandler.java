package elucent.eidolon.client;

import elucent.eidolon.Reference;
import elucent.eidolon.capability.SoulData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EidolonHudHandler extends Gui {
    private static final ResourceLocation ICONS_TEXTURE =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/icons.png");
    private static final ResourceLocation MANA_BAR_TEXTURE =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/mana_bar.png");

    private float lastEtherealHealth = -1.0F;
    private int healthBlinkTime = 0;

    @SubscribeEvent
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            renderEtherealHearts(event);
        } else if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            renderManaBar(event);
        }
    }

    private void renderEtherealHearts(RenderGameOverlayEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.playerController == null || !mc.playerController.shouldDrawHUD()) {
            return;
        }

        EntityPlayer player = mc.player;
        float etherealMax = SoulData.getMaxEtherealHealth(player);
        if (etherealMax <= 0.0F) {
            lastEtherealHealth = -1.0F;
            return;
        }

        float etherealHealth = clamp(SoulData.getEtherealHealth(player), 0.0F, etherealMax);
        int etherealHearts = ceil(etherealMax / 2.0F);
        if (etherealHearts <= 0) {
            return;
        }

        int ticks = mc.ingameGUI.getUpdateCounter();
        if (lastEtherealHealth >= 0.0F && etherealHealth != lastEtherealHealth) {
            healthBlinkTime = ticks + (etherealHealth < lastEtherealHealth && player.hurtTime > 0 ? 20 : 10);
        }
        lastEtherealHealth = etherealHealth;
        boolean highlight = healthBlinkTime > ticks && (healthBlinkTime - ticks) / 3 % 2 == 1;

        ScaledResolution resolution = event.getResolution();
        int left = resolution.getScaledWidth() / 2 - 91;
        int top = resolution.getScaledHeight() - GuiIngameForge.left_height;
        int healthRows = ceil((player.getMaxHealth() + player.getAbsorptionAmount()) / 20.0F);
        int rowHeight = clamp(10 - (healthRows - 2), 3, 10);

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(ICONS_TEXTURE);
        for (int i = 0; i < etherealHearts; i++) {
            int row = i / 10;
            int column = i % 10;
            int x = left + column * 8;
            int y = top - row * rowHeight;
            drawTexturedModalRect(x, y, highlight ? 9 : 0, 18, 9, 9);
        }
        for (int i = 0; i < etherealHearts; i++) {
            int row = i / 10;
            int column = i % 10;
            int x = left + column * 8;
            int y = top - row * rowHeight;
            if (i * 2 + 1 < etherealHealth) {
                drawTexturedModalRect(x, y, 0, 9, 9, 9);
            } else if (i * 2 + 1 == etherealHealth) {
                drawTexturedModalRect(x, y, 9, 9, 9, 9);
            }
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        GuiIngameForge.left_height += ceil(etherealHearts / 10.0F) * rowHeight;
    }

    private void renderManaBar(RenderGameOverlayEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) {
            return;
        }

        EntityPlayer player = mc.player;
        float maxMagic = SoulData.getMaxMagic(player);
        if (maxMagic <= 0.0F) {
            return;
        }

        float magic = clamp(SoulData.getMagic(player), 0.0F, maxMagic);
        ScaledResolution resolution = event.getResolution();
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();

        int xPosition = xPosition();
        int yPosition = yPosition();
        boolean horizontal = isHorizontal();

        int barWidth = horizontal ? 120 : 28;
        int barHeight = horizontal ? 28 : 120;
        int originX = width / 2 - barWidth / 2;
        int originY = height / 2 - barHeight / 2;
        if (horizontal) {
            if (yPosition == -1) {
                originY = 4;
            } else if (yPosition == 1) {
                originY = height + 4 - barHeight;
            }
            if (xPosition == -1) {
                originX = 8;
            } else if (xPosition == 1) {
                originX = width - 4 - barWidth;
            }
        } else {
            if (yPosition == -1) {
                originY = -8;
            } else if (yPosition == 1) {
                originY = height - 20 - barHeight;
            }
            if (xPosition == -1) {
                originX = 4;
            } else if (xPosition == 1) {
                originX = width + 4 - barWidth;
            }
        }

        int barLength = 114;
        int filledLength = clamp(ceil(barLength * magic / maxMagic), 0, barLength);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 0.01F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(MANA_BAR_TEXTURE);
        if (horizontal) {
            renderHorizontalManaBar(originX, originY, xPosition, filledLength, filledLength == barLength);
        } else {
            renderVerticalManaBar(originX, originY, yPosition, filledLength, filledLength == barLength);
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderHorizontalManaBar(int x, int y, int xPosition, int filledLength, boolean full) {
        int length = filledLength;
        int iconU = 48;
        int iconV = 48;

        x -= 4;
        drawTexturedModalRect(x, y, 2, length == 0 ? 6 : 38, 6, 20);
        if (xPosition > 0) {
            drawTexturedModalRect(x - 23, y - 2, 0, 64, 24, 24);
            drawTexturedModalRect(x - 18, y + 4, iconU, iconV, 12, 12);
        }
        x += 6;

        int firstSegment = Math.min(8, length);
        length -= firstSegment;
        drawTexturedModalRect(x, y, 8, 38, firstSegment, 20);
        x += firstSegment;
        if (firstSegment < 8) {
            drawTexturedModalRect(x, y, 8 + firstSegment, 6, 8 - firstSegment, 20);
            x += 8 - firstSegment;
        }

        for (int i = 0; i < 6; i++) {
            int segment = Math.min(16, length);
            length -= segment;
            drawTexturedModalRect(x, y, 16, 38, segment, 20);
            x += segment;
            if (segment < 16) {
                drawTexturedModalRect(x, y, 16 + segment, 6, 16 - segment, 20);
                x += 16 - segment;
            }
        }

        int lastSegment = Math.min(8, length);
        drawTexturedModalRect(x, y, 32, 38, lastSegment, 20);
        x += lastSegment;
        if (lastSegment < 8) {
            drawTexturedModalRect(x, y, 32 + lastSegment, 6, 8 - lastSegment, 20);
            x += 8 - lastSegment;
        }

        drawTexturedModalRect(x, y, 40, full ? 6 : 38, 7, 20);
        if (xPosition <= 0) {
            drawTexturedModalRect(x + 5, y - 2, 32, 64, 24, 24);
            drawTexturedModalRect(x + 12, y + 4, iconU, iconV, 12, 12);
        }
    }

    private void renderVerticalManaBar(int x, int y, int yPosition, int filledLength, boolean full) {
        int length = filledLength;
        int iconU = 48;
        int iconV = 48;

        y += 16;
        y += 114;
        drawTexturedModalRect(x, y, length == 0 ? 54 : 86, 40, 20, 6);
        if (yPosition < 0) {
            drawTexturedModalRect(x - 2, y + 5, 32, 96, 24, 24);
            drawTexturedModalRect(x + 4, y + 12, iconU, iconV, 12, 12);
        }

        int firstSegment = Math.min(8, length);
        length -= firstSegment;
        y -= firstSegment;
        drawTexturedModalRect(x, y, 86, 32, 20, firstSegment);
        if (firstSegment < 8) {
            y -= 8 - firstSegment;
            drawTexturedModalRect(x, y, 54, 32 + firstSegment, 20, 8 - firstSegment);
        }

        for (int i = 0; i < 6; i++) {
            int segment = Math.min(16, length);
            length -= segment;
            y -= segment;
            drawTexturedModalRect(x, y, 86, 16, 20, segment);
            if (segment < 16) {
                y -= 16 - segment;
                drawTexturedModalRect(x, y, 54, 16 + segment, 20, 16 - segment);
            }
        }

        int lastSegment = Math.min(8, length);
        y -= lastSegment;
        drawTexturedModalRect(x, y, 86, 8, 20, lastSegment);
        if (lastSegment < 8) {
            y -= 8 - lastSegment;
            drawTexturedModalRect(x, y, 54, 8 + lastSegment, 20, 8 - lastSegment);
        }

        y -= 6;
        drawTexturedModalRect(x, y, full ? 54 : 86, 2, 20, 6);
        if (yPosition >= 0) {
            drawTexturedModalRect(x - 2, y - 23, 0, 96, 24, 24);
            drawTexturedModalRect(x + 4, y - 18, iconU, iconV, 12, 12);
        }
    }

    private int xPosition() {
        String origin = ClientConfig.magicBarPosition();
        if (ClientConfig.POSITION_BOTTOM_LEFT.equals(origin)
                || ClientConfig.POSITION_LEFT.equals(origin)
                || ClientConfig.POSITION_TOP_LEFT.equals(origin)) {
            return -1;
        }
        if (ClientConfig.POSITION_BOTTOM_RIGHT.equals(origin)
                || ClientConfig.POSITION_RIGHT.equals(origin)
                || ClientConfig.POSITION_TOP_RIGHT.equals(origin)) {
            return 1;
        }
        return 0;
    }

    private int yPosition() {
        String origin = ClientConfig.magicBarPosition();
        if (ClientConfig.POSITION_TOP_LEFT.equals(origin)
                || ClientConfig.POSITION_TOP.equals(origin)
                || ClientConfig.POSITION_TOP_RIGHT.equals(origin)) {
            return -1;
        }
        if (ClientConfig.POSITION_BOTTOM_LEFT.equals(origin)
                || ClientConfig.POSITION_BOTTOM_RIGHT.equals(origin)) {
            return 1;
        }
        return 0;
    }

    private boolean isHorizontal() {
        String orientation = ClientConfig.magicBarOrientation();
        String origin = ClientConfig.magicBarPosition();
        if (ClientConfig.ORIENTATION_HORIZONTAL.equals(orientation)) {
            return true;
        }
        if (ClientConfig.ORIENTATION_VERTICAL.equals(orientation)) {
            return false;
        }
        return !ClientConfig.POSITION_LEFT.equals(origin) && !ClientConfig.POSITION_RIGHT.equals(origin);
    }

    private static int ceil(float value) {
        return (int) Math.ceil(value);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
