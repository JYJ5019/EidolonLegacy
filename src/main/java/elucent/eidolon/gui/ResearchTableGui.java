package elucent.eidolon.gui;

import elucent.eidolon.Reference;
import elucent.eidolon.network.ModNetwork;
import elucent.eidolon.network.ResearchActionPacket;
import elucent.eidolon.research.Research;
import elucent.eidolon.research.ResearchTask;
import elucent.eidolon.research.Researches;
import elucent.eidolon.registries.ModItems;
import elucent.eidolon.tile.ResearchTableTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class ResearchTableGui extends GuiContainer {
    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/research_table.png");
    private static final int TASK_X = 164;
    private static final int TASK_Y = 16;
    private static final int TASK_SPACING = 36;
    private final ResearchTableContainer tableContainer;

    public ResearchTableGui(InventoryPlayer playerInventory, ResearchTableTileEntity tile) {
        super(new ResearchTableContainer(playerInventory, tile));
        this.tableContainer = (ResearchTableContainer) inventorySlots;
        xSize = 192;
        ySize = 224;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
        if (mouseButton == 0 && (clickTaskSubmit(mouseX, mouseY) || clickStamp(mouseX, mouseY))) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        tableContainer.refreshVisibleTaskSlots();
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (tableContainer.getProgress() > 0) {
            return;
        }
        ResearchTask hovered = getHoveredTask(mouseX, mouseY);
        if (hovered != null) {
            List<String> tooltip = hovered.getTooltip();
            if (!tooltip.isEmpty()) {
                drawHoveringText(tooltip, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(BACKGROUND);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);

        Research research = getCurrentResearch();
        if (research == null) {
            return;
        }
        NBTTagCompound tag = inventorySlots.getSlot(ResearchTableTileEntity.SLOT_NOTES).getStack().getTagCompound();
        int stars = research.getStars();
        int stepsDone = tag.getInteger("stepsDone");
        int progress = tableContainer.getProgress();
        if (progress > 0) {
            int amount = progress * 104 / ResearchTableTileEntity.RESEARCH_PROGRESS_TICKS;
            drawTexturedModalRect(left + 137, top + 17 + amount, 192, 92 + amount, 9, 104 - amount);
        }
        if (stepsDone < stars && progress == 0) {
            List<ResearchTask> tasks = research.getTasks(tableContainer.getResearchSeed(), stepsDone);
            for (int i = 0; i < tasks.size(); i++) {
                drawTask(tasks, i, left + TASK_X, top + TASK_Y + i * TASK_SPACING);
            }
        } else if (!inventorySlots.getSlot(ResearchTableTileEntity.SLOT_SEAL).getStack().isEmpty()) {
            int textureX = isStampHovered(mouseX, mouseY) ? 234 : 213;
            drawTexturedModalRect(left + 73, top + 49, textureX, 64, 21, 18);
        }

        int starsY = 61 + 5 * stars;
        for (int i = 0; i < stars; i++) {
            int textureX = i < stepsDone ? 201 : 192;
            drawTexturedModalRect(left + 152, top + starsY - i * 10, textureX, 82, 9, 10);
        }
    }

    private void drawTask(List<ResearchTask> tasks, int taskIndex, int x, int y) {
        ResearchTask task = tasks.get(taskIndex);
        mc.getTextureManager().bindTexture(BACKGROUND);
        int taskWidth = task.getDisplayWidth();
        drawTexturedModalRect(x, y, 80, 224, 8, 32);
        drawTexturedModalRect(x + 8, y, 112, 224, 24, 32);
        task.drawIcon(x + 12, y + 8);
        resetGuiRenderState();
        mc.getTextureManager().bindTexture(BACKGROUND);
        task.drawCustom(x + 32, y);
        resetGuiRenderState();
        mc.getTextureManager().bindTexture(BACKGROUND);
        int buttonTextureX = tableContainer.isTaskCompleteClient(tasks, taskIndex) ? 160 : 136;
        drawTexturedModalRect(x + taskWidth - 32, y, buttonTextureX, 224, 24, 32);
        drawTexturedModalRect(x + taskWidth - 8, y, 96, 224, 8, 32);
    }

    private void resetGuiRenderState() {
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private ResearchTask getHoveredTask(int mouseX, int mouseY) {
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        Research research = getCurrentResearch();
        if (research == null || tableContainer.getProgress() > 0) {
            return null;
        }
        NBTTagCompound tag = inventorySlots.getSlot(ResearchTableTileEntity.SLOT_NOTES).getStack().getTagCompound();
        if (tag.getInteger("stepsDone") >= research.getStars()) {
            return null;
        }
        List<ResearchTask> tasks = research.getTasks(tableContainer.getResearchSeed(), tag.getInteger("stepsDone"));
        for (int i = 0; i < tasks.size(); i++) {
            int x = left + TASK_X + 10;
            int y = top + TASK_Y + i * TASK_SPACING + 6;
            if (mouseX >= x && mouseX < x + 20 && mouseY >= y && mouseY < y + 20) {
                return tasks.get(i);
            }
        }
        return null;
    }

    private boolean clickTaskSubmit(int mouseX, int mouseY) {
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        Research research = getCurrentResearch();
        if (research == null || tableContainer.getProgress() > 0) {
            return false;
        }
        NBTTagCompound tag = inventorySlots.getSlot(ResearchTableTileEntity.SLOT_NOTES).getStack().getTagCompound();
        int stepsDone = tag.getInteger("stepsDone");
        if (stepsDone >= research.getStars()) {
            return false;
        }
        List<ResearchTask> tasks = research.getTasks(tableContainer.getResearchSeed(), stepsDone);
        for (int i = 0; i < tasks.size(); i++) {
            ResearchTask task = tasks.get(i);
            int x = left + TASK_X;
            int y = top + TASK_Y + i * TASK_SPACING;
            int buttonX = x + task.getDisplayWidth() - 30;
            int buttonY = y + 9;
            if (tableContainer.isTaskCompleteClient(tasks, i)
                    && mouseX >= buttonX && mouseX < buttonX + 20 && mouseY >= buttonY && mouseY < buttonY + 13) {
                ModNetwork.CHANNEL.sendToServer(new ResearchActionPacket(i));
                return true;
            }
        }
        return false;
    }

    private boolean clickStamp(int mouseX, int mouseY) {
        Research research = getCurrentResearch();
        if (research == null || !isStampHovered(mouseX, mouseY)) {
            return false;
        }
        NBTTagCompound tag = inventorySlots.getSlot(ResearchTableTileEntity.SLOT_NOTES).getStack().getTagCompound();
        if (tag.getInteger("stepsDone") < research.getStars()
                || inventorySlots.getSlot(ResearchTableTileEntity.SLOT_SEAL).getStack().isEmpty()) {
            return false;
        }
        ModNetwork.CHANNEL.sendToServer(new ResearchActionPacket(ResearchActionPacket.ACTION_STAMP, -1));
        return true;
    }

    private boolean isStampHovered(int mouseX, int mouseY) {
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        return mouseX >= left + 75 && mouseX < left + 92 && mouseY >= top + 51 && mouseY < top + 65;
    }

    private Research getCurrentResearch() {
        ItemStack notes = inventorySlots.getSlot(ResearchTableTileEntity.SLOT_NOTES).getStack();
        if (notes.isEmpty() || notes.getItem() != ModItems.RESEARCH_NOTES || !notes.hasTagCompound()) {
            return null;
        }
        return Researches.find(new ResourceLocation(notes.getTagCompound().getString("research")));
    }
}
