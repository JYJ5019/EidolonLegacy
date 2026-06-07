package elucent.eidolon.potion;

import elucent.eidolon.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EidolonPotion extends Potion {
    private final ResourceLocation icon;

    protected EidolonPotion(boolean badEffect, int liquidColor, String iconName) {
        super(badEffect, liquidColor);
        this.icon = new ResourceLocation(Reference.MOD_ID, "textures/mob_effect/" + iconName + ".png");
    }

    @Override
    public boolean hasStatusIcon() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        renderIcon(mc, x + 6, y + 7, 1.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        renderIcon(mc, x + 3, y + 3, alpha);
    }

    @SideOnly(Side.CLIENT)
    private void renderIcon(Minecraft mc, int x, int y, float alpha) {
        mc.getTextureManager().bindTexture(icon);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
