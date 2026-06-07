package elucent.eidolon.client.render.shader;

import elucent.eidolon.client.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IReloadableResourceManager;
import org.lwjgl.opengl.GL20;

public final class LegacyShaders {
    private static final LegacyShaderProgram GLOWING_COLOR = new LegacyShaderProgram("glowing_color");
    private static final LegacyShaderProgram GLOWING_SPRITE = new LegacyShaderProgram("glowing_sprite");
    private static int activeDepth;
    private static boolean reloadListenerRegistered;

    private LegacyShaders() {
    }

    public static boolean beginColor(float red, float green, float blue, float alpha) {
        return begin(GLOWING_COLOR, red, green, blue, alpha, false);
    }

    public static boolean beginSprite(float red, float green, float blue, float alpha) {
        return begin(GLOWING_SPRITE, red, green, blue, alpha, true);
    }

    public static void end(boolean active) {
        if (!active) {
            return;
        }
        activeDepth--;
        if (activeDepth <= 0) {
            activeDepth = 0;
            GL20.glUseProgram(0);
        }
    }

    public static boolean isSupported() {
        return ClientConfig.visualEffectsEnabled() && OpenGlHelper.shadersSupported;
    }

    public static void reload() {
        GLOWING_COLOR.delete();
        GLOWING_SPRITE.delete();
        activeDepth = 0;
    }

    public static void registerReloadListener() {
        if (reloadListenerRegistered) {
            return;
        }
        if (Minecraft.getMinecraft().getResourceManager() instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(resourceManager -> reload());
            reloadListenerRegistered = true;
        }
    }

    private static boolean begin(LegacyShaderProgram shader, float red, float green, float blue, float alpha, boolean textured) {
        boolean bound = shader.bind(red, green, blue, alpha, textured);
        if (bound) {
            activeDepth++;
        }
        return bound;
    }
}
