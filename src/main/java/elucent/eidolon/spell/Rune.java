package elucent.eidolon.spell;

import net.minecraft.util.ResourceLocation;

public abstract class Rune {
    private final ResourceLocation registryName;
    private final ResourceLocation sprite;

    protected Rune(ResourceLocation registryName) {
        this(registryName, new ResourceLocation(registryName.getNamespace(), "rune/" + registryName.getPath()));
    }

    protected Rune(ResourceLocation registryName, ResourceLocation sprite) {
        this.registryName = registryName;
        this.sprite = sprite;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public ResourceLocation getSprite() {
        return sprite;
    }

    public abstract RuneResult doEffect(SignSequence sequence);

    @Override
    public boolean equals(Object other) {
        return other instanceof Rune && ((Rune) other).registryName.equals(registryName);
    }

    @Override
    public int hashCode() {
        return registryName.hashCode();
    }

    public enum RuneResult {
        PASS,
        FAIL
    }
}
