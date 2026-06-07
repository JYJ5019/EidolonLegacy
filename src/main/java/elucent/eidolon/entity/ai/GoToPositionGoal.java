package elucent.eidolon.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class GoToPositionGoal extends EntityAIBase {
    private final BlockPos dest;
    private final EntityCreature creature;
    private final double speed;
    private boolean running;

    public GoToPositionGoal(EntityCreature creature, BlockPos pos, double speedIn) {
        this.creature = creature;
        this.dest = pos.toImmutable();
        this.speed = speedIn;
        this.running = true;
        setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        return running;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return running;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void updateTask() {
        if (!running) {
            return;
        }
        creature.getNavigator().tryMoveToXYZ(dest.getX(), dest.getY(), dest.getZ(), speed);
        if (creature.getDistanceSq(dest.getX(), dest.getY(), dest.getZ()) < 8.0D * 8.0D) {
            running = false;
        }
    }
}
