package elucent.eidolon.particle;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;

public class EidolonLineWispParticle extends EidolonParticle {
    private final double startX;
    private final double startY;
    private final double startZ;
    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public EidolonLineWispParticle(World world, TextureAtlasSprite sprite, double x, double y, double z,
                                   double targetX, double targetY, double targetZ,
                                   EidolonParticleData data) {
        super(world, sprite, x, y, z, 0.0D, 0.0D, 0.0D, data);
        this.startX = x;
        this.startY = y;
        this.startZ = z;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        super.onUpdate();
        float coeff = (float) this.particleAge / (float) this.particleMaxAge;
        float xCoeff = coeff * coeff;
        float yCoeff = 1.0F - (1.0F - coeff) * (1.0F - coeff);
        this.posX = lerp(startX, targetX, xCoeff);
        this.posY = lerp(startY, targetY, yCoeff);
        this.posZ = lerp(startZ, targetZ, xCoeff);
    }

    private double lerp(double start, double end, float coeff) {
        return start + (end - start) * coeff;
    }
}
