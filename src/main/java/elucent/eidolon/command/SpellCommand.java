package elucent.eidolon.command;

import elucent.eidolon.Reference;
import elucent.eidolon.capability.Facts;
import elucent.eidolon.capability.ReputationData;
import elucent.eidolon.capability.SoulData;
import elucent.eidolon.deity.Deities;
import elucent.eidolon.deity.Deity;
import elucent.eidolon.entity.ChantCasterEntity;
import elucent.eidolon.network.ModNetwork;
import elucent.eidolon.network.SoulSyncPacket;
import elucent.eidolon.spell.Chanting;
import elucent.eidolon.spell.Rune;
import elucent.eidolon.spell.Runes;
import elucent.eidolon.spell.Sign;
import elucent.eidolon.spell.SignSequence;
import elucent.eidolon.spell.Signs;
import elucent.eidolon.spell.Spell;
import elucent.eidolon.spell.Spells;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpellCommand extends CommandBase {
    @Override
    public String getName() {
        return "eidolon_spell";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("eidolon_chant");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/eidolon_spell <list|grant_sign|grant_rune|grant_fact|grant_all|clear|cast_signs|cast_runes|cast_runes_delayed|rep|soul>";
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
        if ("list".equals(args[0])) {
            list(sender, args);
            return;
        }
        if ("grant_all".equals(args[0])) {
            grantAll(player);
            sender.sendMessage(new TextComponentString("Granted all Eidolon signs, runes and known facts."));
            return;
        }
        if ("clear".equals(args[0])) {
            KnowledgeUtil.clearSigns(player);
            KnowledgeUtil.clearRunes(player);
            KnowledgeUtil.clearFacts(player);
            sender.sendMessage(new TextComponentString("Cleared Eidolon signs, runes and facts."));
            return;
        }
        if ("grant_sign".equals(args[0])) {
            requireArgs(args, 2);
            Sign sign = requireSign(args[1]);
            KnowledgeUtil.grantSign(player, sign);
            sender.sendMessage(new TextComponentString("Granted sign: " + sign.getRegistryName()));
            return;
        }
        if ("grant_rune".equals(args[0])) {
            requireArgs(args, 2);
            Rune rune = requireRune(args[1]);
            KnowledgeUtil.grantRune(player, rune);
            sender.sendMessage(new TextComponentString("Granted rune: " + rune.getRegistryName()));
            return;
        }
        if ("grant_fact".equals(args[0])) {
            requireArgs(args, 2);
            ResourceLocation fact = id(args[1]);
            KnowledgeUtil.grantFact(player, fact);
            sender.sendMessage(new TextComponentString("Granted fact: " + fact));
            return;
        }
        if ("cast_signs".equals(args[0])) {
            requireArgs(args, 2);
            SignSequence sequence = new SignSequence(parseSigns(args, 1));
            cast(sender, player, sequence);
            return;
        }
        if ("cast_runes".equals(args[0])) {
            requireArgs(args, 2);
            List<Rune> runes = parseRunes(args, 1);
            Chanting.Result result = Chanting.castRunes(player.world, player.getPosition(), player, runes);
            replyCast(sender, result);
            return;
        }
        if ("cast_runes_delayed".equals(args[0])) {
            requireArgs(args, 2);
            List<Rune> runes = parseRunes(args, 1);
            ChantCasterEntity caster = new ChantCasterEntity(player.world, player, runes, player.getLookVec());
            player.world.spawnEntity(caster);
            sender.sendMessage(new TextComponentString("Spawned delayed chant caster with " + runes.size() + " runes."));
            return;
        }
        if ("rep".equals(args[0])) {
            reputation(sender, player, args);
            return;
        }
        if ("soul".equals(args[0])) {
            soul(sender, player, args);
            return;
        }

        throw new WrongUsageException(getUsage(sender));
    }

    private void list(ICommandSender sender, String[] args) throws CommandException {
        requireArgs(args, 2);
        if ("signs".equals(args[1])) {
            sender.sendMessage(new TextComponentString(joinSigns()));
        } else if ("runes".equals(args[1])) {
            sender.sendMessage(new TextComponentString(joinRunes()));
        } else if ("spells".equals(args[1])) {
            sender.sendMessage(new TextComponentString(joinSpells()));
        } else if ("deities".equals(args[1])) {
            sender.sendMessage(new TextComponentString(joinDeities()));
        } else {
            throw new WrongUsageException("/eidolon_spell list <signs|runes|spells|deities>");
        }
    }

    private void cast(ICommandSender sender, EntityPlayerMP player, SignSequence sequence) {
        Chanting.Result result = Chanting.castSigns(player.world, player.getPosition(), player, sequence);
        replyCast(sender, result);
    }

    private void replyCast(ICommandSender sender, Chanting.Result result) {
        sender.sendMessage(new TextComponentString((result.isSuccess() ? "Success: " : "Failed: ") + result.getMessage()));
    }

    private void reputation(ICommandSender sender, EntityPlayerMP player, String[] args) throws CommandException {
        if (args.length < 3) {
            throw new WrongUsageException("/eidolon_spell rep <get|set|add|lock|unlock> <deity> [amount|lock]");
        }
        ReputationData reputation = ReputationData.get(player.world);
        Deity deity = requireDeity(args[2]);
        if ("get".equals(args[1])) {
            sender.sendMessage(new TextComponentString("Reputation " + deity.getId() + ": "
                    + reputation.getReputation(player, deity.getId())
                    + (reputation.isLocked(player, deity.getId()) ? " (locked)" : "")));
            return;
        }
        if ("set".equals(args[1])) {
            requireArgs(args, 4);
            reputation.setReputation(player, deity.getId(), parseDoubleArg(args[3]));
            sender.sendMessage(new TextComponentString("Set reputation " + deity.getId() + ": "
                    + reputation.getReputation(player, deity.getId())));
            return;
        }
        if ("add".equals(args[1])) {
            requireArgs(args, 4);
            reputation.addReputation(player, deity.getId(), parseDoubleArg(args[3]));
            sender.sendMessage(new TextComponentString("Added reputation " + deity.getId() + ": "
                    + reputation.getReputation(player, deity.getId())));
            return;
        }
        if ("lock".equals(args[1])) {
            requireArgs(args, 4);
            ResourceLocation lock = id(args[3]);
            reputation.lock(player, deity.getId(), lock);
            sender.sendMessage(new TextComponentString("Locked " + deity.getId() + " at " + lock));
            return;
        }
        if ("unlock".equals(args[1])) {
            requireArgs(args, 4);
            ResourceLocation lock = id(args[3]);
            boolean unlocked = reputation.unlock(player, deity.getId(), lock);
            sender.sendMessage(new TextComponentString((unlocked ? "Unlocked " : "No matching lock for ")
                    + deity.getId() + " / " + lock));
            return;
        }
        throw new WrongUsageException("/eidolon_spell rep <get|set|add|lock|unlock> <deity> [amount|lock]");
    }

    private void soul(ICommandSender sender, EntityPlayerMP player, String[] args) throws CommandException {
        SoulData.ensureDefaults(player);
        if (args.length < 2 || "get".equals(args[1])) {
            sender.sendMessage(new TextComponentString("Soul magic: " + SoulData.getMagic(player) + "/"
                    + SoulData.getMaxMagic(player) + ", ethereal health: " + SoulData.getEtherealHealth(player)
                    + "/" + SoulData.getMaxEtherealHealth(player)));
            return;
        }
        requireArgs(args, 3);
        if ("set_magic".equals(args[1])) {
            SoulData.setMagic(player, parseFloatArg(args[2]));
        } else if ("give_magic".equals(args[1])) {
            SoulData.giveMagic(player, parseFloatArg(args[2]));
        } else if ("set_max_magic".equals(args[1])) {
            SoulData.setMaxMagic(player, parseFloatArg(args[2]));
        } else if ("set_ethereal".equals(args[1])) {
            SoulData.setEtherealHealth(player, parseFloatArg(args[2]));
        } else if ("set_max_ethereal".equals(args[1])) {
            SoulData.setMaxEtherealHealth(player, parseFloatArg(args[2]));
        } else {
            throw new WrongUsageException("/eidolon_spell soul <get|set_magic|give_magic|set_max_magic|set_ethereal|set_max_ethereal> [amount]");
        }
        ModNetwork.CHANNEL.sendTo(new SoulSyncPacket(player), player);
        sender.sendMessage(new TextComponentString("Updated soul magic: " + SoulData.getMagic(player)
                + "/" + SoulData.getMaxMagic(player)));
    }

    private void grantAll(EntityPlayerMP player) {
        for (Sign sign : Signs.getSigns()) {
            KnowledgeUtil.grantSign(player, sign);
        }
        for (Rune rune : Runes.getRunes()) {
            KnowledgeUtil.grantRune(player, rune);
        }
        KnowledgeUtil.grantFact(player, Facts.VILLAGER_SACRIFICE);
    }

    private List<Sign> parseSigns(String[] args, int start) throws CommandException {
        List<Sign> signs = new ArrayList<>();
        for (int i = start; i < args.length; i++) {
            signs.add(requireSign(args[i]));
        }
        return signs;
    }

    private List<Rune> parseRunes(String[] args, int start) throws CommandException {
        List<Rune> runes = new ArrayList<>();
        for (int i = start; i < args.length; i++) {
            runes.add(requireRune(args[i]));
        }
        return runes;
    }

    private Sign requireSign(String raw) throws CommandException {
        Sign sign = Signs.find(id(raw));
        if (sign == null) {
            throw new CommandException("Unknown Eidolon sign: " + raw);
        }
        return sign;
    }

    private Rune requireRune(String raw) throws CommandException {
        Rune rune = Runes.find(id(raw));
        if (rune == null) {
            throw new CommandException("Unknown Eidolon rune: " + raw);
        }
        return rune;
    }

    private Deity requireDeity(String raw) throws CommandException {
        Deity deity = Deities.find(id(raw));
        if (deity == null) {
            throw new CommandException("Unknown Eidolon deity: " + raw);
        }
        return deity;
    }

    private ResourceLocation id(String raw) {
        return raw.indexOf(':') >= 0 ? new ResourceLocation(raw) : new ResourceLocation(Reference.MOD_ID, raw);
    }

    private void requireArgs(String[] args, int count) throws WrongUsageException {
        if (args.length < count) {
            throw new WrongUsageException(getUsage(null));
        }
    }

    private double parseDoubleArg(String raw) throws CommandException {
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            throw new CommandException("Invalid number: " + raw);
        }
    }

    private float parseFloatArg(String raw) throws CommandException {
        try {
            return Float.parseFloat(raw);
        } catch (NumberFormatException e) {
            throw new CommandException("Invalid number: " + raw);
        }
    }

    private String joinSigns() {
        List<String> ids = new ArrayList<>();
        for (Sign sign : Signs.getSigns()) {
            ids.add(sign.getRegistryName().toString());
        }
        return "Signs: " + String.join(", ", ids);
    }

    private String joinRunes() {
        List<String> ids = new ArrayList<>();
        for (Rune rune : Runes.getRunes()) {
            ids.add(rune.getRegistryName().toString());
        }
        return "Runes: " + String.join(", ", ids);
    }

    private String joinSpells() {
        List<String> ids = new ArrayList<>();
        for (Spell spell : Spells.getSpells()) {
            ids.add(spell.getRegistryName().toString());
        }
        return "Spells: " + String.join(", ", ids);
    }

    private String joinDeities() {
        List<String> ids = new ArrayList<>();
        for (Deity deity : Deities.getDeities()) {
            ids.add(deity.getId().toString());
        }
        return "Deities: " + String.join(", ", ids);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "list", "grant_sign", "grant_rune", "grant_fact",
                    "grant_all", "clear", "cast_signs", "cast_runes", "cast_runes_delayed", "rep", "soul");
        }
        if (args.length == 2 && "list".equals(args[0])) {
            return getListOfStringsMatchingLastWord(args, "signs", "runes", "spells", "deities");
        }
        if (args.length >= 2 && ("grant_sign".equals(args[0]) || "cast_signs".equals(args[0]))) {
            return getListOfStringsMatchingLastWord(args, signIds());
        }
        if (args.length >= 2 && ("grant_rune".equals(args[0])
                || "cast_runes".equals(args[0]) || "cast_runes_delayed".equals(args[0]))) {
            return getListOfStringsMatchingLastWord(args, runeIds());
        }
        if (args.length == 2 && "rep".equals(args[0])) {
            return getListOfStringsMatchingLastWord(args, "get", "set", "add", "lock", "unlock");
        }
        if (args.length == 3 && "rep".equals(args[0])) {
            return getListOfStringsMatchingLastWord(args, deityIds());
        }
        if (args.length == 2 && "soul".equals(args[0])) {
            return getListOfStringsMatchingLastWord(args, "get", "set_magic", "give_magic", "set_max_magic",
                    "set_ethereal", "set_max_ethereal");
        }
        return Collections.emptyList();
    }

    private List<String> signIds() {
        List<String> ids = new ArrayList<>();
        for (Sign sign : Signs.getSigns()) {
            ids.add(sign.getRegistryName().toString());
        }
        return ids;
    }

    private List<String> runeIds() {
        List<String> ids = new ArrayList<>();
        for (Rune rune : Runes.getRunes()) {
            ids.add(rune.getRegistryName().toString());
        }
        return ids;
    }

    private List<String> deityIds() {
        List<String> ids = new ArrayList<>();
        for (Deity deity : Deities.getDeities()) {
            ids.add(deity.getId().toString());
        }
        return ids;
    }
}
