package elucent.eidolon.gui;

import elucent.eidolon.Reference;
import elucent.eidolon.tile.SoulEnchanterTileEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;
import java.io.IOException;
import java.util.List;

public class SoulEnchanterGui extends GuiContainer {
    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/soul_enchanter.png");

    private final SoulEnchanterContainer container;

    public SoulEnchanterGui(InventoryPlayer playerInventory, SoulEnchanterTileEntity tile) {
        super(new SoulEnchanterContainer(playerInventory, tile));
        this.container = (SoulEnchanterContainer) inventorySlots;
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.translateToLocal("container.eidolon.soul_enchanter"), 8, 6, 0x3f2a1d);
        fontRenderer.drawString(I18n.translateToLocal("container.inventory"), 8, 73, 0x3f2a1d);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
        drawButton(left, top, 0, "I", mouseX, mouseY);
        drawButton(left, top, 1, "II", mouseX, mouseY);
        drawButton(left, top, 2, "III", mouseX, mouseY);
    }

    private void drawButton(int left, int top, int id, String label, int mouseX, int mouseY) {
        int x = left + 60;
        int y = top + 14 + id * 19;
        boolean enabled = container.canAttempt(mc.player, id + 1);
        boolean hovered = mouseX >= x && mouseX < x + 108 && mouseY >= y && mouseY < y + 19;
        int textureY = enabled ? hovered ? 204 : 166 : 185;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect(x, y, 0, textureY, 108, 19);
        drawTexturedModalRect(x + 1, y + 1, 16 * id, enabled ? 223 : 239, 16, 16);
        int level = container.getOfferLevel(mc.player, id + 1);
        String text = I18n.translateToLocalFormatted("container.eidolon.soul_enchanter.cost", label, level);
        fontRenderer.drawString(text, x + 23, y + 5, enabled ? 0x2f2118 : 0x6d6257);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        if (mouseButton == 0) {
            for (int i = 0; i < 3; i++) {
                int x = left + 60;
                int y = top + 14 + i * 19;
                if (mouseX >= x && mouseX < x + 108 && mouseY >= y && mouseY < y + 19 && container.canAttempt(mc.player, i + 1)) {
                    mc.playerController.sendEnchantPacket(inventorySlots.windowId, i);
                    return;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawOfferTooltip(mouseX, mouseY);
    }

    private void drawOfferTooltip(int mouseX, int mouseY) {
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        for (int i = 0; i < 3; i++) {
            int x = left + 60;
            int y = top + 14 + i * 19;
            if (mouseX < x || mouseX >= x + 108 || mouseY < y || mouseY >= y + 19) {
                continue;
            }
            int tier = i + 1;
            Enchantment enchantment = container.getOfferEnchantment(mc.player, tier);
            int level = container.getOfferLevel(mc.player, tier);
            if (enchantment == null || level <= 0) {
                return;
            }
            List<String> lines = new ArrayList<>();
            lines.add(enchantment.getTranslatedName(level));
            if (!mc.player.capabilities.isCreativeMode) {
                if (mc.player.experienceLevel < level) {
                    lines.add(TextFormatting.RED + I18n.translateToLocalFormatted("container.enchant.level.requirement", level));
                } else {
                    lines.add(TextFormatting.GRAY + I18n.translateToLocalFormatted("container.eidolon.enchant.shard.one", 1));
                    lines.add(TextFormatting.GRAY + I18n.translateToLocalFormatted("container.enchant.level.many", level));
                }
            }
            drawHoveringText(lines, mouseX, mouseY);
            return;
        }
    }
}
