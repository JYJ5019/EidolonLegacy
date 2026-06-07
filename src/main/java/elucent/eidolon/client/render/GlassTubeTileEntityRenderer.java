package elucent.eidolon.client.render;

import elucent.eidolon.reagent.ReagentStack;
import elucent.eidolon.tile.GlassTubeTileEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

public class GlassTubeTileEntityRenderer extends TileEntitySpecialRenderer<GlassTubeTileEntity> {
    private static final double L = 0.40625D;
    private static final double U = 0.59375D;
    private static final double W = U - L;

    @Override
    public void render(GlassTubeTileEntity te, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        ReagentStack contents = te.getTank().getContents();
        if (contents == null || contents.isEmpty()) {
            return;
        }

        float fill = Math.min(1.0F, te.getTank().getPressure());
        float reagentAlpha = Math.min(0.62F, 0.24F + 0.38F * fill);
        boolean nx = te.getInput() == EnumFacing.WEST || te.getOutput() == EnumFacing.WEST;
        boolean px = te.getInput() == EnumFacing.EAST || te.getOutput() == EnumFacing.EAST;
        boolean ny = te.getInput() == EnumFacing.DOWN || te.getOutput() == EnumFacing.DOWN;
        boolean py = te.getInput() == EnumFacing.UP || te.getOutput() == EnumFacing.UP;
        boolean nz = te.getInput() == EnumFacing.NORTH || te.getOutput() == EnumFacing.NORTH;
        boolean pz = te.getInput() == EnumFacing.SOUTH || te.getOutput() == EnumFacing.SOUTH;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        addCenter(buffer, fill, nx, px, ny, py, nz, pz, contents.reagent.getColor(), reagentAlpha);
        addArm(buffer, te.getInput(), fill, contents.reagent.getColor(), reagentAlpha);
        addArm(buffer, te.getOutput(), fill, contents.reagent.getColor(), reagentAlpha);
        tessellator.draw();

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void addCenter(BufferBuilder buffer, float fill, boolean nx, boolean px, boolean ny, boolean py,
                           boolean nz, boolean pz, int color, float alpha) {
        if (py && ny) {
            ReagentRenderHelper.addBox(buffer, L, L, L, U, U, U, color, alpha,
                    !nx, !px, !ny, !py, !nz, !pz);
        } else if (py) {
            if (fill < 1.0F) {
                ReagentRenderHelper.addBox(buffer, L, L + fill * W, L, U, U, U, color, alpha * 0.55F,
                        true, true, !ny, !py, true, true);
            }
            ReagentRenderHelper.addBox(buffer, L, L, L, U, L + fill * W, U, color, alpha,
                    !nx, !px, !ny, false, !nz, !pz);
        } else {
            ReagentRenderHelper.addBox(buffer, L, L, L, U, L + fill * W, U, color, alpha,
                    !nx, !px, !ny, true, !nz, !pz);
        }
    }

    private void addArm(BufferBuilder buffer, EnumFacing facing, float fill, int color, float alpha) {
        if (facing == EnumFacing.WEST) {
            ReagentRenderHelper.addBox(buffer, 0.0D, L, L, L, L + W * fill, U, color, alpha,
                    true, false, true, true, true, true);
        } else if (facing == EnumFacing.EAST) {
            ReagentRenderHelper.addBox(buffer, U, L, L, 1.0D, L + W * fill, U, color, alpha,
                    false, true, true, true, true, true);
        } else if (facing == EnumFacing.DOWN) {
            ReagentRenderHelper.addBox(buffer, L, 0.0D, L, U, L, U, color, alpha,
                    true, true, true, false, true, true);
        } else if (facing == EnumFacing.UP) {
            ReagentRenderHelper.addBox(buffer, L, U, L, U, 1.0D, U, color, alpha,
                    true, true, false, true, true, true);
        } else if (facing == EnumFacing.NORTH) {
            ReagentRenderHelper.addBox(buffer, L, L, 0.0D, U, L + W * fill, L, color, alpha,
                    true, true, true, true, true, false);
        } else if (facing == EnumFacing.SOUTH) {
            ReagentRenderHelper.addBox(buffer, L, L, U, U, L + W * fill, 1.0D, color, alpha,
                    true, true, true, true, false, true);
        }
    }
}
