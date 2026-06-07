package elucent.eidolon.client.render;

import elucent.eidolon.client.render.shader.LegacyShaders;
import elucent.eidolon.reagent.ReagentStack;
import elucent.eidolon.tile.AltarTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class AltarTileEntityRenderer extends TileEntitySpecialRenderer<AltarTileEntity> {
    @Override
    public void render(AltarTileEntity te, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        renderReagent(te, x, y, z);

        ItemStack stack = te.getOffering();
        if (stack.isEmpty()) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.02D, z + 0.5D);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.45F, 0.45F, 0.45F);
        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void renderReagent(AltarTileEntity te, double x, double y, double z) {
        ReagentStack contents = te.getTank().getContents();
        if (contents == null || contents.isEmpty()) {
            return;
        }

        float fill = Math.min(1.0F, te.getTank().getPressure());
        float reagentAlpha = contents.reagent.isGas() ? 0.18F : 0.36F;
        double inset = 0.25D;
        double y1 = 0.70D;
        double y2 = y1 + 0.16D * fill;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();

        boolean shader = LegacyShaders.beginColor(1.35F, 1.35F, 1.45F, 1.0F);
        try {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            ReagentRenderHelper.addBox(buffer, inset, y1, inset,
                    1.0D - inset, y2, 1.0D - inset, contents.reagent.getColor(), reagentAlpha);
            tessellator.draw();
        } finally {
            LegacyShaders.end(shader);
        }

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}
