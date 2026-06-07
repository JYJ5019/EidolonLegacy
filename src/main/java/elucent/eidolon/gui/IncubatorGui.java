package elucent.eidolon.gui;

import elucent.eidolon.Reference;
import elucent.eidolon.tile.IncubatorTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class IncubatorGui extends GuiContainer {
    private static final ResourceLocation INVENTORY_BACKGROUND =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/wooden_brewing_stand.png");

    private final IncubatorContainer container;

    public IncubatorGui(InventoryPlayer playerInventory, IncubatorTileEntity tile) {
        super(new IncubatorContainer(playerInventory, tile));
        this.container = (IncubatorContainer) inventorySlots;
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.translateToLocal("container.eidolon.incubator"), 8, 6, 0x3f2a1d);
        fontRenderer.drawString(I18n.translateToLocal("container.inventory"), 8, 73, 0x3f2a1d);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawRect(left, top, left + xSize, top + ySize, 0xffc6c6c6);
        drawRect(left + 1, top + 1, left + xSize - 1, top + 2, 0xffffffff);
        drawRect(left + 1, top + 1, left + 2, top + ySize - 1, 0xffffffff);
        drawRect(left + 1, top + ySize - 2, left + xSize - 1, top + ySize - 1, 0xff555555);
        drawRect(left + xSize - 2, top + 1, left + xSize - 1, top + ySize - 1, 0xff555555);
        drawSlot(left + 43, top + 34);
        drawSlot(left + 43, top + 58);
        drawSlot(left + 115, top + 46);
        drawProgress(left + 70, top + 50);
        drawPlayerInventoryBackground(left, top);
    }

    private void drawPlayerInventoryBackground(int left, int top) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        drawTexturedModalRect(left + 7, top + 83, 7, 83, 162, 76);
    }

    private void drawProgress(int x, int y) {
        int width = container.getProgress() * 30 / container.getMaxProgress();
        drawRect(x, y, x + 34, y + 8, 0xff555555);
        drawRect(x + 1, y + 1, x + 33, y + 7, 0xff8d8d8d);
        drawRect(x + 2, y + 2, x + 2 + width, y + 6, 0xff4f8f6a);
    }

    private void drawSlot(int x, int y) {
        drawRect(x - 1, y - 1, x + 17, y + 17, 0xff555555);
        drawRect(x, y, x + 18, y + 18, 0xffffffff);
        drawRect(x, y, x + 17, y + 17, 0xff8b8b8b);
        drawRect(x + 1, y + 1, x + 17, y + 17, 0xffeeeeee);
    }
}
