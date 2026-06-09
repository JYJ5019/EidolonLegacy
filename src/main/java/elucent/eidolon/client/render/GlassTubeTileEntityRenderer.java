package elucent.eidolon.client.render;

import elucent.eidolon.reagent.IReagentTankProvider;
import elucent.eidolon.reagent.ReagentStack;
import elucent.eidolon.tile.GlassTubeTileEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
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
        float reagentAlpha = contents.reagent.isGas()
                ? Math.min(0.72F, 0.28F + 0.44F * fill)
                : Math.min(0.88F, 0.46F + 0.42F * fill);
        boolean nx = te.getInput() == EnumFacing.WEST || te.getOutput() == EnumFacing.WEST;
        boolean px = te.getInput() == EnumFacing.EAST || te.getOutput() == EnumFacing.EAST;
        boolean ny = te.getInput() == EnumFacing.DOWN || te.getOutput() == EnumFacing.DOWN;
        boolean py = te.getInput() == EnumFacing.UP || te.getOutput() == EnumFacing.UP;
        boolean nz = te.getInput() == EnumFacing.NORTH || te.getOutput() == EnumFacing.NORTH;
        boolean pz = te.getInput() == EnumFacing.SOUTH || te.getOutput() == EnumFacing.SOUTH;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableTexture2D();
        ReagentRenderHelper.bindTexture(contents.reagent);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        float frameV = ReagentRenderHelper.getAnimationFrame(partialTicks);
        addCenter(buffer, fill, nx, px, ny, py, nz, pz, reagentAlpha, frameV);
        addArm(buffer, te, te.getInput(), fill, reagentAlpha, frameV);
        addArm(buffer, te, te.getOutput(), fill, reagentAlpha, frameV);
        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void addCenter(BufferBuilder buffer, float fill, boolean nx, boolean px, boolean ny, boolean py,
                           boolean nz, boolean pz, float alpha, float frameV) {
        if (py && ny) {
            ReagentRenderHelper.addTexturedBox(buffer, L, L, L, U, U, U, alpha, frameV,
                    !nx, !px, !ny, !py, !nz, !pz);
        } else if (py) {
            if (fill < 1.0F) {
                ReagentRenderHelper.addTexturedBox(buffer, L, L + fill * W, L, U, U, U, alpha * 0.55F, frameV,
                        true, true, !ny, !py, true, true);
            }
            ReagentRenderHelper.addTexturedBox(buffer, L, L, L, U, L + fill * W, U, alpha, frameV,
                    !nx, !px, !ny, false, !nz, !pz);
        } else {
            ReagentRenderHelper.addTexturedBox(buffer, L, L, L, U, L + fill * W, U, alpha, frameV,
                    !nx, !px, !ny, true, !nz, !pz);
        }
    }

    private void addArm(BufferBuilder buffer, GlassTubeTileEntity te, EnumFacing facing,
                        float fill, float alpha, float frameV) {
        boolean cap = !attached(te, facing);
        if (facing == EnumFacing.WEST) {
            ReagentRenderHelper.addTexturedBox(buffer, 0.0D, L, L, L, L + W * fill, U, alpha, frameV,
                    cap, false, true, true, true, true);
        } else if (facing == EnumFacing.EAST) {
            ReagentRenderHelper.addTexturedBox(buffer, U, L, L, 1.0D, L + W * fill, U, alpha, frameV,
                    false, cap, true, true, true, true);
        } else if (facing == EnumFacing.DOWN) {
            ReagentRenderHelper.addTexturedBox(buffer, L, 0.0D, L, U, L, U, alpha, frameV,
                    true, true, cap, false, true, true);
        } else if (facing == EnumFacing.UP) {
            ReagentRenderHelper.addTexturedBox(buffer, L, U, L, U, 1.0D, U, alpha, frameV,
                    true, true, false, cap, true, true);
        } else if (facing == EnumFacing.NORTH) {
            ReagentRenderHelper.addTexturedBox(buffer, L, L, 0.0D, U, L + W * fill, L, alpha, frameV,
                    true, true, true, true, cap, false);
        } else if (facing == EnumFacing.SOUTH) {
            ReagentRenderHelper.addTexturedBox(buffer, L, L, U, U, L + W * fill, 1.0D, alpha, frameV,
                    true, true, true, true, false, cap);
        }
    }

    private boolean attached(GlassTubeTileEntity te, EnumFacing facing) {
        if (te.getWorld() == null) {
            return false;
        }
        TileEntity adjacent = te.getWorld().getTileEntity(te.getPos().offset(facing));
        return adjacent instanceof IReagentTankProvider;
    }
}
