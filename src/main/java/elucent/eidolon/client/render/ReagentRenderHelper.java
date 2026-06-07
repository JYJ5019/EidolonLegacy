package elucent.eidolon.client.render;

import net.minecraft.client.renderer.BufferBuilder;

final class ReagentRenderHelper {
    private ReagentRenderHelper() {
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
}
