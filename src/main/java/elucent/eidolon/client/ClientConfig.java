package elucent.eidolon.client;

import elucent.eidolon.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.CLIENT)
public final class ClientConfig {
    public static final String POSITION_BOTTOM_LEFT = "bottomLeft";
    public static final String POSITION_LEFT = "left";
    public static final String POSITION_TOP_LEFT = "topLeft";
    public static final String POSITION_TOP = "top";
    public static final String POSITION_TOP_RIGHT = "topRight";
    public static final String POSITION_RIGHT = "right";
    public static final String POSITION_BOTTOM_RIGHT = "bottomRight";

    public static final String ORIENTATION_HORIZONTAL = "horizontal";
    public static final String ORIENTATION_VERTICAL = "vertical";
    public static final String ORIENTATION_DEFAULT = "default";

    private static Configuration configuration;

    private static boolean visualEffects = true;
    private static double particleDensity = 1.0D;
    private static boolean betterLayering = true;
    private static String magicBarPosition = POSITION_TOP;
    private static String magicBarOrientation = ORIENTATION_DEFAULT;

    private ClientConfig() {
    }

    public static void init(File suggestedConfigFile) {
        configuration = new Configuration(new File(suggestedConfigFile.getParentFile(), Reference.MOD_ID + "-client.cfg"));
        sync();
    }

    public static boolean visualEffectsEnabled() {
        return visualEffects;
    }

    public static double particleDensity() {
        return particleDensity;
    }

    public static boolean betterLayering() {
        return betterLayering;
    }

    public static String magicBarPosition() {
        return magicBarPosition;
    }

    public static String magicBarOrientation() {
        return magicBarOrientation;
    }

    private static void sync() {
        if (configuration == null) {
            return;
        }
        String graphics = "graphics";
        configuration.addCustomCategoryComment(graphics, "Client-only Eidolon graphics and visual effect settings.");
        visualEffects = configuration.getBoolean("visualEffects", graphics, true,
                "Enable Eidolon client-side visual effect particles and sounds.");
        particleDensity = configuration.getFloat("particleDensity", graphics, 1.0F, 0.0F, 1.0F,
                "Scales Eidolon visual effect particle density. Set to 0 for minimal particles.");
        betterLayering = configuration.getBoolean("betterLayering", graphics, true,
                "Reserved for improved particle/effect layering parity with newer Eidolon versions.");

        String ui = "ui";
        configuration.addCustomCategoryComment(ui, "Client-only Eidolon HUD placement settings.");
        magicBarPosition = configuration.getString("magicBarPosition", ui, POSITION_TOP,
                "Onscreen positioning of the magic power meter.",
                new String[] {
                        POSITION_BOTTOM_LEFT, POSITION_LEFT, POSITION_TOP_LEFT, POSITION_TOP,
                        POSITION_TOP_RIGHT, POSITION_RIGHT, POSITION_BOTTOM_RIGHT
                });
        magicBarOrientation = configuration.getString("magicBarOrientation", ui, ORIENTATION_DEFAULT,
                "Orientation of the magic power meter.",
                new String[] {ORIENTATION_HORIZONTAL, ORIENTATION_VERTICAL, ORIENTATION_DEFAULT});

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Reference.MOD_ID.equals(event.getModID())) {
            sync();
        }
    }
}
