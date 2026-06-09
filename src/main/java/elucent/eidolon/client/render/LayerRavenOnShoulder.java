package elucent.eidolon.client.render;

import elucent.eidolon.Reference;
import elucent.eidolon.entity.RavenEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class LayerRavenOnShoulder implements LayerRenderer<EntityPlayer> {
    private static final ResourceLocation RAVEN_ID = new ResourceLocation(Reference.MOD_ID, "raven");
    private final RenderLivingBase<?> renderer;
    private final RavenModel ravenModel = new RavenModel();
    private RavenEntity leftRaven;
    private RavenEntity rightRaven;

    public LayerRavenOnShoulder(RenderLivingBase<?> renderer) {
        this.renderer = renderer;
    }

    @Override
    public void doRenderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks,
                              float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        leftRaven = renderShoulderRaven(player, player.getLeftShoulderEntity(), leftRaven, limbSwing,
                limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, true);
        rightRaven = renderShoulderRaven(player, player.getRightShoulderEntity(), rightRaven, limbSwing,
                limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, false);
    }

    private RavenEntity renderShoulderRaven(EntityPlayer player, NBTTagCompound shoulderTag, RavenEntity raven,
                                            float limbSwing, float limbSwingAmount, float ageInTicks,
                                            float netHeadYaw, float headPitch, float scale, boolean left) {
        if (!isRaven(shoulderTag)) {
            return null;
        }
        if (raven == null) {
            Entity entity = EntityList.createEntityFromNBT(shoulderTag, player.world);
            if (!(entity instanceof RavenEntity)) {
                return null;
            }
            raven = (RavenEntity) entity;
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(RavenRenderer.TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate(left ? 0.4F : -0.4F, player.isSneaking() ? -1.35F : -1.5F, 0.0F);
        raven.onGround = true;
        ravenModel.render(raven, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.popMatrix();
        return raven;
    }

    private boolean isRaven(NBTTagCompound shoulderTag) {
        return shoulderTag != null && RAVEN_ID.toString().equals(shoulderTag.getString("id"));
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
