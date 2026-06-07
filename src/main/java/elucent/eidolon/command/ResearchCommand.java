package elucent.eidolon.command;

import elucent.eidolon.research.Research;
import elucent.eidolon.research.Researches;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResearchCommand extends CommandBase {
    @Override
    public String getName() {
        return "eidolon_research";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/eidolon_research <clear|grant|grant_all|remove> [research_id]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException(getUsage(sender));
        }

        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        if ("clear".equals(args[0])) {
            clearResearch(player);
            sender.sendMessage(new TextComponentString("Cleared Eidolon research."));
            return;
        }
        if ("grant_all".equals(args[0])) {
            int count = grantAllResearch(player);
            sender.sendMessage(new TextComponentString("Granted all Eidolon research (" + count + ")."));
            return;
        }

        if (args.length < 2) {
            throw new WrongUsageException(getUsage(sender));
        }

        ResourceLocation id = new ResourceLocation(args[1]);
        Research research = Researches.find(id);
        if (research == null) {
            throw new CommandException("Unknown Eidolon research: " + id);
        }

        if ("grant".equals(args[0])) {
            KnowledgeUtil.grantResearch(player, id);
            sender.sendMessage(new TextComponentString("Granted Eidolon research: " + id));
        } else if ("remove".equals(args[0])) {
            KnowledgeUtil.removeResearch(player, id);
            sender.sendMessage(new TextComponentString("Removed Eidolon research: " + id));
        } else {
            throw new WrongUsageException(getUsage(sender));
        }
    }

    private void clearResearch(EntityPlayerMP player) {
        KnowledgeUtil.clearResearch(player);
    }

    private int grantAllResearch(EntityPlayerMP player) {
        int count = 0;
        for (Research research : Researches.getResearches()) {
            KnowledgeUtil.grantResearch(player, research.getId());
            count++;
        }
        return count;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, net.minecraft.util.math.BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "clear", "grant", "grant_all", "remove");
        }
        if (args.length == 2 && !"clear".equals(args[0]) && !"grant_all".equals(args[0])) {
            List<String> ids = new ArrayList<>();
            for (Research research : Researches.getResearches()) {
                ids.add(research.getId().toString());
            }
            return getListOfStringsMatchingLastWord(args, ids);
        }
        return Collections.emptyList();
    }
}
