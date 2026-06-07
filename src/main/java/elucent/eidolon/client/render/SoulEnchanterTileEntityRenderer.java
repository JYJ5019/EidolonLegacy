package elucent.eidolon.client.render;

import elucent.eidolon.Reference;
import elucent.eidolon.tile.SoulEnchanterTileEntity;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SoulEnchanterTileEntityRenderer extends TileEntitySpecialRenderer<SoulEnchanterTileEntity> {
    private static final ResourceLocation BOOK_TEXTURE =
            new ResourceLocation(Reference.MOD_ID, "textures/entity/enchanter_book.png");

    private final ModelBook model = new ModelBook();

    @Override
    public void render(SoulEnchanterTileEntity te, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.75D, z + 0.5D);

        float ticks = te.tickCount + partialTicks;
        GlStateManager.translate(0.0D, 0.1F + MathHelper.sin(ticks * 0.1F) * 0.01F, 0.0D);

        float angleDelta;
        for (angleDelta = te.nextPageAngle - te.pageAngle; angleDelta >= (float) Math.PI; angleDelta -= (float) Math.PI * 2.0F) {
        }
        while (angleDelta < -(float) Math.PI) {
            angleDelta += (float) Math.PI * 2.0F;
        }

        float angle = te.pageAngle + angleDelta * partialTicks;
        GlStateManager.rotate(-angle * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);

        float flip = te.oFlip + (te.flip - te.oFlip) * partialTicks;
        float pageFlipLeft = (float) MathHelper.frac(flip + 0.25F) * 1.6F - 0.3F;
        float pageFlipRight = (float) MathHelper.frac(flip + 0.75F) * 1.6F - 0.3F;
        float open = te.pageTurningSpeed + (te.nextPageTurningSpeed - te.pageTurningSpeed) * partialTicks;

        bindTexture(BOOK_TEXTURE);
        model.render(null, ticks,
                MathHelper.clamp(pageFlipLeft, 0.0F, 1.0F),
                MathHelper.clamp(pageFlipRight, 0.0F, 1.0F),
                open, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
    }
}
