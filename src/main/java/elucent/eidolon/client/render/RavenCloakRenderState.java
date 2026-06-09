package elucent.eidolon.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SideOnly(Side.CLIENT)
public final class RavenCloakRenderState {
    private static final Map<Integer, State> STATES = new HashMap<>();
    private static World lastWorld;

    private RavenCloakRenderState() {
    }

    public static void applySync(int entityId, boolean flying, int dashTicks, int flapCharges, boolean flapped) {
        if (entityId < 0) {
            return;
        }
        long tick = currentTick();
        State state = STATES.computeIfAbsent(entityId, id -> new State());
        state.flying = flying;
        state.dashTicks = Math.max(0, dashTicks);
        state.flapCharges = Math.max(0, flapCharges);
        state.syncTick = tick;
        if (flapped) {
            state.flapTick = tick;
        }
    }

    public static Snapshot get(EntityLivingBase entity, boolean fallbackFlying, int fallbackDashTicks,
                               int fallbackFlapCharges) {
        if (entity == null) {
            return new Snapshot(fallbackFlying, fallbackDashTicks, fallbackFlapCharges, -1);
        }
        long tick = currentTick();
        State state = STATES.get(entity.getEntityId());
        if (state == null) {
            return new Snapshot(fallbackFlying, fallbackDashTicks, fallbackFlapCharges, -1);
        }
        long age = Math.max(0L, tick - state.syncTick);
        if (age > 80L) {
            STATES.remove(entity.getEntityId());
            return new Snapshot(fallbackFlying, fallbackDashTicks, fallbackFlapCharges, -1);
        }
        int dashTicks = Math.max(0, state.dashTicks - (int)age);
        int flapAge = state.flapTick < 0L ? -1 : (int)Math.min(100L, Math.max(0L, tick - state.flapTick));
        return new Snapshot(state.flying, dashTicks, state.flapCharges, flapAge);
    }

    public static void tick() {
        long tick = currentTick();
        Iterator<Map.Entry<Integer, State>> iterator = STATES.entrySet().iterator();
        while (iterator.hasNext()) {
            State state = iterator.next().getValue();
            if (tick - state.syncTick > 120L) {
                iterator.remove();
            }
        }
    }

    private static long currentTick() {
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft.world;
        if (world != lastWorld) {
            STATES.clear();
            lastWorld = world;
        }
        return world == null ? 0L : world.getTotalWorldTime();
    }

    public static final class Snapshot {
        public final boolean flying;
        public final int dashTicks;
        public final int flapCharges;
        public final int flapAge;

        private Snapshot(boolean flying, int dashTicks, int flapCharges, int flapAge) {
            this.flying = flying;
            this.dashTicks = dashTicks;
            this.flapCharges = flapCharges;
            this.flapAge = flapAge;
        }
    }

    private static final class State {
        private boolean flying;
        private int dashTicks;
        private int flapCharges;
        private long syncTick;
        private long flapTick = -1L;
    }
}
