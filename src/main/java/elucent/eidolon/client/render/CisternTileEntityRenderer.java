package elucent.eidolon.client.render;

import elucent.eidolon.reagent.ReagentStack;
import elucent.eidolon.tile.CisternTileEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class CisternTileEntityRenderer extends TileEntitySpecialRenderer<CisternTileEntity> {
    @Override
    public void render(CisternTileEntity te, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        ReagentStack contents = te.getLocalTank().getContents();
        if (contents == null || contents.isEmpty()) {
            return;
        }

        boolean top = te.getWorld().getBlockState(te.getPos().up()).getBlock()
                == te.getWorld().getBlockState(te.getPos()).getBlock();
        boolean bottom = te.getWorld().getBlockState(te.getPos().down()).getBlock()
                == te.getWorld().getBlockState(te.getPos()).getBlock();
        if (bottom && te.getDownPressure() < 1.0F && te.getLocalTank().getPressure() < 1.0F / 64.0F) {
            return;
        }

        float fill = Math.min(1.0F, te.getLocalTank().getPressure());
        double y1 = bottom ? 0.0D : 0.1875D;
        double height = 0.625D + (bottom ? 0.1875D : 0.0D) + (top ? 0.1875D : 0.0D);
        double y2 = y1 + height * fill;
        float reagentAlpha = contents.reagent.isGas() ? 0.34F : 0.58F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        ReagentRenderHelper.addBox(buffer, 0.125D, y1, 0.125D,
                0.875D, y2, 0.875D, contents.reagent.getColor(), reagentAlpha,
                true, true,
                !bottom,
                fill < 1.0F || !top || te.getUpPressure() <= 0.0F,
                true, true);
        tessellator.draw();

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}
