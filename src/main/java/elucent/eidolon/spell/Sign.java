package elucent.eidolon.spell;

import net.minecraft.util.ResourceLocation;

public class Sign {
    private final ResourceLocation registryName;
    private final ResourceLocation sprite;
    private final int color;

    public Sign(ResourceLocation registryName, ResourceLocation sprite, int color) {
        this.registryName = registryName;
        this.sprite = sprite;
        this.color = color;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public ResourceLocation getSprite() {
        return sprite;
    }

    public int getColor() {
        return color;
    }

    public float getRed() {
        return ((color >> 16) & 255) / 255.0F;
    }

    public float getGreen() {
        return ((color >> 8) & 255) / 255.0F;
    }

    public float getBlue() {
        return (color & 255) / 255.0F;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Sign && ((Sign) other).registryName.equals(registryName);
    }

    @Override
    public int hashCode() {
        return registryName.hashCode();
    }
}
