package elucent.eidolon.client.render;

import elucent.eidolon.Reference;
import elucent.eidolon.reagent.Reagent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;

final class ReagentRenderHelper {
    private static final int ANIMATION_FRAMES = 16;
    private static final float FRAME_HEIGHT = 1.0F / ANIMATION_FRAMES;

    private ReagentRenderHelper() {
    }

    static void bindTexture(Reagent reagent) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(getTexture(reagent));
    }

    static float getAnimationFrame(float partialTicks) {
        Minecraft minecraft = Minecraft.getMinecraft();
        float ticks = minecraft.world == null ? 0.0F : minecraft.world.getTotalWorldTime() + partialTicks;
        int frame = ((int) (ticks / 2.0F)) % ANIMATION_FRAMES;
        return frame * FRAME_HEIGHT;
    }

    static void addTexturedBox(BufferBuilder buffer, double x1, double y1, double z1,
                               double x2, double y2, double z2, float alpha, float frameV) {
        addTexturedBox(buffer, x1, y1, z1, x2, y2, z2, alpha, frameV,
                true, true, true, true, true, true);
    }

    static void addTexturedBox(BufferBuilder buffer, double x1, double y1, double z1,
                               double x2, double y2, double z2, float alpha, float frameV,
                               boolean west, boolean east, boolean down, boolean up,
                               boolean north, boolean south) {
        if (up) {
            texturedFace(buffer,
                    x1, y2, z1, z1, x1,
                    x1, y2, z2, z2, x1,
                    x2, y2, z2, z2, x2,
                    x2, y2, z1, z1, x2,
                    alpha, frameV, 1.0F);
        }
        if (down) {
            texturedFace(buffer,
                    x1, y1, z2, z1, x1,
                    x1, y1, z1, z2, x1,
                    x2, y1, z1, z2, x2,
                    x2, y1, z2, z1, x2,
                    alpha, frameV, 0.72F);
        }
        if (north) {
            texturedFace(buffer,
                    x2, y1, z1, x1, y1,
                    x1, y1, z1, x2, y1,
                    x1, y2, z1, x2, y2,
                    x2, y2, z1, x1, y2,
                    alpha, frameV, 0.86F);
        }
        if (south) {
            texturedFace(buffer,
                    x1, y1, z2, x1, y1,
                    x2, y1, z2, x2, y1,
                    x2, y2, z2, x2, y2,
                    x1, y2, z2, x1, y2,
                    alpha, frameV, 0.92F);
        }
        if (west) {
            texturedFace(buffer,
                    x1, y1, z1, z1, y1,
                    x1, y1, z2, z2, y1,
                    x1, y2, z2, z2, y2,
                    x1, y2, z1, z1, y2,
                    alpha, frameV, 0.82F);
        }
        if (east) {
            texturedFace(buffer,
                    x2, y1, z2, z1, y1,
                    x2, y1, z1, z2, y1,
                    x2, y2, z1, z2, y2,
                    x2, y2, z2, z1, y2,
                    alpha, frameV, 0.96F);
        }
    }

    static void addBox(BufferBuilder buffer, double x1, double y1, double z1,
                       double x2, double y2, double z2, int color, float alpha) {
        addBox(buffer, x1, y1, z1, x2, y2, z2, color, alpha, true, true, true, true, true, true);
    }

    static void addBox(BufferBuilder buffer, double x1, double y1, double z1,
                       double x2, double y2, double z2, int color, float alpha,
                       boolean west, boolean east, boolean down, boolean up, boolean north, boolean south) {
        float r = ((color >> 16) & 255) / 255.0F;
        float g = ((color >> 8) & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        if (down) {
            face(buffer, x1, y1, z1, x2, y1, z2, r, g, b, alpha);
        }
        if (up) {
            face(buffer, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1, r, g, b, alpha);
        }
        if (north) {
            face(buffer, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, r, g, b, alpha);
        }
        if (south) {
            face(buffer, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2, r, g, b, alpha);
        }
        if (west) {
            face(buffer, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1, r, g, b, alpha);
        }
        if (east) {
            face(buffer, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, r, g, b, alpha);
        }
    }

    private static void face(BufferBuilder buffer, double x1, double y, double z1,
                             double x2, double y2, double z2,
                             float r, float g, float b, float alpha) {
        face(buffer, x1, y, z1, x1, y2, z2, x2, y2, z2, x2, y, z1, r, g, b, alpha);
    }

    private static void face(BufferBuilder buffer, double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             double x3, double y3, double z3,
                             double x4, double y4, double z4,
                             float r, float g, float b, float alpha) {
        buffer.pos(x1, y1, z1).color(r, g, b, alpha).endVertex();
        buffer.pos(x2, y2, z2).color(r, g, b, alpha).endVertex();
        buffer.pos(x3, y3, z3).color(r, g, b, alpha).endVertex();
        buffer.pos(x4, y4, z4).color(r, g, b, alpha).endVertex();
    }

    private static ResourceLocation getTexture(Reagent reagent) {
        if (reagent == null || reagent.getRegistryName() == null) {
            return new ResourceLocation(Reference.MOD_ID, "textures/block/vapor.png");
        }
        String path = reagent.getRegistryName().getPath();
        if ("steam".equals(path)) {
            path = "vapor";
        }
        return new ResourceLocation(reagent.getRegistryName().getNamespace(), "textures/block/" + path + ".png");
    }

    private static void texturedFace(BufferBuilder buffer,
                                     double x1, double y1, double z1, double u1, double v1,
                                     double x2, double y2, double z2, double u2, double v2,
                                     double x3, double y3, double z3, double u3, double v3,
                                     double x4, double y4, double z4, double u4, double v4,
                                     float alpha, float frameV, float shade) {
        texturedVertex(buffer, x1, y1, z1, u1, v1, alpha, frameV, shade);
        texturedVertex(buffer, x2, y2, z2, u2, v2, alpha, frameV, shade);
        texturedVertex(buffer, x3, y3, z3, u3, v3, alpha, frameV, shade);
        texturedVertex(buffer, x4, y4, z4, u4, v4, alpha, frameV, shade);
    }

    private static void texturedVertex(BufferBuilder buffer, double x, double y, double z,
                                       double u, double v, float alpha, float frameV, float shade) {
        buffer.pos(x, y, z)
                .tex(u, frameV + v * FRAME_HEIGHT)
                .color(shade, shade, shade, alpha)
                .endVertex();
    }
}
