package elucent.eidolon.command;

import elucent.eidolon.world.EidolonWorldGenerator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class LocateStructureCommand extends CommandBase {
    private static final String ANY = "any";
    private static final String SURFACE = "surface";
    private static final String LAB = "lab";
    private static final String STRAY_TOWER = "stray_tower";
    private static final String CATACOMB = "catacomb";
    private static final String LOAD = "load";

    @Override
    public String getName() {
        return "eidolon_locate_structure";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/eidolon_locate_structure <any|surface|lab|stray_tower|catacomb> [radius_chunks] [load]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1 || args.length > 3) {
            throw new WrongUsageException(getUsage(sender));
        }

        String target = args[0].toLowerCase(Locale.ROOT);
        if (!isValidTarget(target)) {
            throw new WrongUsageException(getUsage(sender));
        }

        int radiusChunks = 256;
        boolean loadChunk = false;
        if (args.length >= 2) {
            if (LOAD.equalsIgnoreCase(args[1])) {
                loadChunk = true;
            } else {
                radiusChunks = parseInt(args[1], 1, 1024);
            }
        }
        if (args.length == 3) {
            if (!LOAD.equalsIgnoreCase(args[2])) {
                throw new WrongUsageException(getUsage(sender));
            }
            loadChunk = true;
        }

        WorldServer world = getOverworld(server, sender);
        BlockPos origin = sender.getPosition();
        int originChunkX = Math.floorDiv(origin.getX(), 16);
        int originChunkZ = Math.floorDiv(origin.getZ(), 16);

        EidolonWorldGenerator.StructureCandidate nearest = findNearest(world, target, origin, originChunkX, originChunkZ, radiusChunks);
        if (nearest == null) {
            throw new CommandException("No Eidolon " + target + " structure candidate found within "
                    + radiusChunks + " chunks.");
        }

        if (loadChunk) {
            world.getChunkProvider().provideChunk(nearest.getChunkX(), nearest.getChunkZ());
        }

        String type = nearest.getType().getId();
        BlockPos pos = nearest.getPos();
        sender.sendMessage(new TextComponentString("Nearest Eidolon " + type + " candidate: "
                + pos.getX() + " " + pos.getY() + " " + pos.getZ()
                + " (chunk " + nearest.getChunkX() + ", " + nearest.getChunkZ() + ")."));
        sender.sendMessage(new TextComponentString("Teleport: /tp "
                + pos.getX() + " " + pos.getY() + " " + pos.getZ()));
        if (loadChunk) {
            sender.sendMessage(new TextComponentString("Loaded candidate chunk to trigger generation if it was new."));
        } else {
            sender.sendMessage(new TextComponentString("Add 'load' to this command to load the candidate chunk from here."));
        }
        sender.sendMessage(new TextComponentString("Note: chunks generated before this worldgen pass will not retro-generate the structure."));
    }

    private WorldServer getOverworld(MinecraftServer server, ICommandSender sender) {
        World senderWorld = sender.getEntityWorld();
        if (senderWorld instanceof WorldServer && senderWorld.provider.getDimension() == EidolonWorldGenerator.OVERWORLD) {
            return (WorldServer) senderWorld;
        }
        return server.getWorld(EidolonWorldGenerator.OVERWORLD);
    }

    private EidolonWorldGenerator.StructureCandidate findNearest(WorldServer world, String target, BlockPos origin,
                                                                 int originChunkX, int originChunkZ, int radiusChunks) {
        EidolonWorldGenerator.StructureCandidate best = null;
        long bestDistance = Long.MAX_VALUE;

        for (int dx = -radiusChunks; dx <= radiusChunks; dx++) {
            for (int dz = -radiusChunks; dz <= radiusChunks; dz++) {
                int chunkX = originChunkX + dx;
                int chunkZ = originChunkZ + dz;

                if (wantsSurface(target) && EidolonWorldGenerator.isSurfaceStructureChunk(world.getSeed(), chunkX, chunkZ)) {
                    EidolonWorldGenerator.StructureCandidate candidate = EidolonWorldGenerator.getSurfaceCandidate(world, chunkX, chunkZ);
                    if (candidate != null && matches(target, candidate.getType().getId())) {
                        long distance = distanceSq(origin, candidate.getPos());
                        if (distance < bestDistance) {
                            best = candidate;
                            bestDistance = distance;
                        }
                    }
                }

                if (wantsCatacomb(target) && EidolonWorldGenerator.isCatacombStructureChunk(world.getSeed(), chunkX, chunkZ)) {
                    EidolonWorldGenerator.StructureCandidate candidate = EidolonWorldGenerator.getCatacombCandidate(world, chunkX, chunkZ);
                    long distance = distanceSq(origin, candidate.getPos());
                    if (distance < bestDistance) {
                        best = candidate;
                        bestDistance = distance;
                    }
                }
            }
        }

        return best;
    }

    private boolean wantsSurface(String target) {
        return ANY.equals(target) || SURFACE.equals(target) || LAB.equals(target) || STRAY_TOWER.equals(target);
    }

    private boolean wantsCatacomb(String target) {
        return ANY.equals(target) || CATACOMB.equals(target);
    }

    private boolean matches(String target, String type) {
        return ANY.equals(target) || SURFACE.equals(target) && (LAB.equals(type) || STRAY_TOWER.equals(type)) || target.equals(type);
    }

    private boolean isValidTarget(String target) {
        return ANY.equals(target)
                || SURFACE.equals(target)
                || LAB.equals(target)
                || STRAY_TOWER.equals(target)
                || CATACOMB.equals(target);
    }

    private long distanceSq(BlockPos a, BlockPos b) {
        long dx = a.getX() - b.getX();
        long dz = a.getZ() - b.getZ();
        return dx * dx + dz * dz;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, ANY, SURFACE, LAB, STRAY_TOWER, CATACOMB);
        }
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, LOAD);
        }
        if (args.length == 3) {
            return getListOfStringsMatchingLastWord(args, LOAD);
        }
        return Collections.emptyList();
    }
}
