package elucent.eidolon.gui;

import elucent.eidolon.Reference;
import elucent.eidolon.tile.WoodenBrewingStandTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class WoodenBrewingStandGui extends GuiContainer {
    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/wooden_brewing_stand.png");
    private static final int[] BUBBLE_LENGTHS = new int[] {29, 24, 20, 16, 11, 6, 0};

    private final WoodenBrewingStandContainer container;

    public WoodenBrewingStandGui(InventoryPlayer playerInventory, WoodenBrewingStandTileEntity tile) {
        super(new WoodenBrewingStandContainer(playerInventory, tile));
        this.container = (WoodenBrewingStandContainer) inventorySlots;
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.translateToLocal("container.eidolon.wooden_brewing_stand"), 8, 6, 0x3f2a1d);
        fontRenderer.drawString(I18n.translateToLocal("container.inventory"), 8, 73, 0x3f2a1d);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
        if (container.getHeat() > 0) {
            drawTexturedModalRect(left + 32, top + 52, 197, 0, 14, 14);
        }
        int brewTime = container.getBrewTime();
        if (brewTime > 0) {
            int progress = (int)(28.0F * (1.0F - (float)brewTime / WoodenBrewingStandTileEntity.BREW_TIME_TOTAL));
            if (progress > 0) {
                drawTexturedModalRect(left + 97, top + 16, 176, 0, 9, progress);
            }
            int bubbles = BUBBLE_LENGTHS[brewTime / 2 % BUBBLE_LENGTHS.length];
            if (bubbles > 0) {
                drawTexturedModalRect(left + 63, top + 14 + 29 - bubbles, 185, 29 - bubbles, 12, bubbles);
            }
        }
    }
}
