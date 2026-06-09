package elucent.eidolon.client.render.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.inventory.EntityEquipmentSlot;

public abstract class EidolonArmorModel extends ModelBiped {
    protected EntityEquipmentSlot armorSlot = EntityEquipmentSlot.CHEST;

    protected EidolonArmorModel(int textureWidth, int textureHeight) {
        super(0.0F, 0.0F, textureWidth, textureHeight);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        replaceBipedParts();
    }

    public void setArmorSlot(EntityEquipmentSlot armorSlot) {
        this.armorSlot = armorSlot;
    }

    protected boolean isArmorSlot(EntityEquipmentSlot slot) {
        return armorSlot == slot;
    }

    private void replaceBipedParts() {
        bipedHead = new ModelRenderer(this);
        bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHeadwear = new ModelRenderer(this);
        bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedBody = new ModelRenderer(this);
        bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedRightArm = new ModelRenderer(this);
        bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        bipedLeftArm = new ModelRenderer(this);
        bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        bipedRightLeg = new ModelRenderer(this);
        bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        bipedLeftLeg = new ModelRenderer(this);
        bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
    }

    protected ModelRenderer child(ModelRenderer parent, int texU, int texV,
                                  float x, float y, float z,
                                  float rotX, float rotY, float rotZ) {
        ModelRenderer renderer = new ModelRenderer(this, texU, texV);
        renderer.setRotationPoint(x, y, z);
        setRotation(renderer, rotX, rotY, rotZ);
        parent.addChild(renderer);
        return renderer;
    }

    protected ModelRenderer childBox(ModelRenderer parent, int texU, int texV, boolean mirror,
                                     float x, float y, float z,
                                     float rotX, float rotY, float rotZ,
                                     float boxX, float boxY, float boxZ,
                                     int width, int height, int depth) {
        return childBox(parent, texU, texV, mirror, x, y, z, rotX, rotY, rotZ,
                boxX, boxY, boxZ, width, height, depth, 0.0F);
    }

    protected ModelRenderer childBox(ModelRenderer parent, int texU, int texV, boolean mirror,
                                     float x, float y, float z,
                                     float rotX, float rotY, float rotZ,
                                     float boxX, float boxY, float boxZ,
                                     int width, int height, int depth, float scale) {
        ModelRenderer renderer = child(parent, texU, texV, x, y, z, rotX, rotY, rotZ);
        addBox(renderer, texU, texV, mirror, boxX, boxY, boxZ, width, height, depth, scale);
        return renderer;
    }

    protected void addBox(ModelRenderer renderer, int texU, int texV, boolean mirror,
                          float boxX, float boxY, float boxZ,
                          int width, int height, int depth) {
        addBox(renderer, texU, texV, mirror, boxX, boxY, boxZ, width, height, depth, 0.0F);
    }

    protected void addBox(ModelRenderer renderer, int texU, int texV, boolean mirror,
                          float boxX, float boxY, float boxZ,
                          int width, int height, int depth, float scale) {
        renderer.mirror = mirror;
        renderer.setTextureOffset(texU, texV).addBox(boxX, boxY, boxZ, width, height, depth, scale);
        renderer.mirror = false;
    }

    protected static void setRotation(ModelRenderer renderer, float x, float y, float z) {
        renderer.rotateAngleX = x;
        renderer.rotateAngleY = y;
        renderer.rotateAngleZ = z;
    }
}
