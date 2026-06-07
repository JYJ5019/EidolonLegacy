package elucent.eidolon.client.render;

import elucent.eidolon.client.render.shader.LegacyShaders;
import elucent.eidolon.tile.CrucibleTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class CrucibleTileEntityRenderer extends TileEntitySpecialRenderer<CrucibleTileEntity> {
    @Override
    public void render(CrucibleTileEntity te, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        FluidStack fluid = te.getFluid();
        if (fluid == null) {
            renderSteamProgress(te, x, y, z);
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        long time = minecraft.world == null ? 0L : minecraft.world.getTotalWorldTime();
        float stirProgress = getStirProgress(time, te.getLastStirTime(), partialTicks);
        renderFluidSurface(fluid, te.getCompletedStepCount(), stirProgress, x, y, z);
        renderFloatingItems(te.getCurrentContents(), x, y, z, partialTicks, stirProgress);
    }

    private void renderSteamProgress(CrucibleTileEntity te, double x, double y, double z) {
        int steam = te.getSteamProgress() + (te.getTank().isEmpty() ? 0 : te.getTank().getContents().amount);
        if (steam <= 0) {
            return;
        }

        float fill = Math.min(1.0F, (float) steam / (float) Fluid.BUCKET_VOLUME);
        float alpha = 0.12F + fill * 0.28F;
        double inset = 0.29D - fill * 0.04D;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();

        boolean shader = LegacyShaders.beginColor(1.2F, 1.25F, 1.35F, 1.0F);
        try {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(inset, 0.84D, inset).color(0.81F, 0.85F, 0.87F, alpha).endVertex();
            buffer.pos(inset, 0.84D, 1.0D - inset).color(0.81F, 0.85F, 0.87F, alpha).endVertex();
            buffer.pos(1.0D - inset, 0.84D, 1.0D - inset).color(0.81F, 0.85F, 0.87F, alpha).endVertex();
            buffer.pos(1.0D - inset, 0.84D, inset).color(0.81F, 0.85F, 0.87F, alpha).endVertex();
            tessellator.draw();
        } finally {
            LegacyShaders.end(shader);
        }

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void renderFluidSurface(FluidStack fluid, int completedSteps, float stirProgress, double x, double y, double z) {
        int color = getFluidColor(fluid, completedSteps);
        float r = ((color >> 16) & 255) / 255.0F;
        float g = ((color >> 8) & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        float alpha = 0.72F + stirProgress * 0.12F;
        double inset = 0.22D - stirProgress * 0.015D;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();

        boolean shader = LegacyShaders.beginColor(1.25F + stirProgress * 0.25F, 1.15F, 1.35F + stirProgress * 0.25F, 1.0F);
        try {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(inset, 0.86D, inset).color(r, g, b, alpha).endVertex();
            buffer.pos(inset, 0.86D, 1.0D - inset).color(r, g, b, alpha).endVertex();
            buffer.pos(1.0D - inset, 0.86D, 1.0D - inset).color(r, g, b, alpha).endVertex();
            buffer.pos(1.0D - inset, 0.86D, inset).color(r, g, b, alpha).endVertex();
            tessellator.draw();
        } finally {
            LegacyShaders.end(shader);
        }

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private int getFluidColor(FluidStack fluid, int completedSteps) {
        if (fluid.getFluid() != FluidRegistry.WATER) {
            return completedSteps <= 0 ? 0x9a8cc0 : 0x7b4fc2;
        }
        if (completedSteps <= 0) {
            return 0x5f8fd8;
        }
        if (completedSteps == 1) {
            return 0x8f7bd8;
        }
        if (completedSteps == 2) {
            return 0x7e45c7;
        }
        return 0x4b2478;
    }

    private float getStirProgress(long time, long lastStirTime, float partialTicks) {
        float age = (time + partialTicks) - lastStirTime;
        if (age < 0.0F || age > 24.0F) {
            return 0.0F;
        }
        return 1.0F - age / 24.0F;
    }

    private void renderFloatingItems(List<ItemStack> stacks, double x, double y, double z, float partialTicks, float stirProgress) {
        if (stacks.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        long time = minecraft.world == null ? 0L : minecraft.world.getTotalWorldTime();
        int count = stacks.size();
        for (int i = 0; i < count; i++) {
            ItemStack stack = stacks.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            double angle = (Math.PI * 2.0D / count) * i + (time + partialTicks) * 0.004D;
            double radius = count == 1 ? stirProgress * 0.08D : 0.18D + stirProgress * 0.04D;

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D + Math.cos(angle) * radius,
                    y + 0.91D + Math.sin((time + partialTicks) * 0.12D + i) * (0.015D + stirProgress * 0.035D),
                    z + 0.5D + Math.sin(angle) * radius);
            GlStateManager.rotate((time + partialTicks) * 0.15F + i * 45.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(70.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(0.32F, 0.32F, 0.32F);
            RenderHelper.enableStandardItemLighting();
            minecraft.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }
}
