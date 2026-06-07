package elucent.eidolon.spell;

import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public final class Chanting {
    private Chanting() {
    }

    public static Result castRunes(World world, BlockPos pos, EntityPlayer player, List<Rune> runes) {
        SignSequence sequence = new SignSequence();
        for (Rune rune : runes) {
            if (rune == null) {
                return Result.fail("Unknown rune.");
            }
            if (!player.capabilities.isCreativeMode && !KnowledgeUtil.knowsRune(player, rune)) {
                return Result.fail("Unknown to player: " + rune.getRegistryName());
            }
            if (rune.doEffect(sequence) == Rune.RuneResult.FAIL) {
                SpellHelper.playChantFail(world, pos);
                return Result.fail("Rune failed: " + rune.getRegistryName().getPath()
                        + " -> [" + sequence.describe() + "]");
            }
        }
        return castSigns(world, pos, player, sequence);
    }

    public static Result castSigns(World world, BlockPos pos, EntityPlayer player, SignSequence sequence) {
        for (Sign sign : sequence.getSigns()) {
            if (!player.capabilities.isCreativeMode && !KnowledgeUtil.knowsSign(player, sign)) {
                return Result.fail("Unknown to player: " + sign.getRegistryName());
            }
        }

        Spell spell = Spells.find(sequence);
        if (spell == null) {
            SpellHelper.playChantFail(world, pos);
            return Result.fail("No spell matches signs: [" + sequence.describe() + "]");
        }
        if (!spell.canCast(world, pos, player, sequence)) {
            SpellHelper.playChantFail(world, pos);
            return Result.fail("Cannot cast " + spell.getRegistryName() + " here.");
        }
        spell.cast(world, pos, player, sequence);
        return Result.success(spell, sequence);
    }

    public static final class Result {
        private final boolean success;
        private final Spell spell;
        private final SignSequence sequence;
        private final String message;

        private Result(boolean success, Spell spell, SignSequence sequence, String message) {
            this.success = success;
            this.spell = spell;
            this.sequence = sequence;
            this.message = message;
        }

        public static Result success(Spell spell, SignSequence sequence) {
            return new Result(true, spell, sequence, "Cast " + spell.getRegistryName()
                    + " with [" + sequence.describe() + "]");
        }

        public static Result fail(String message) {
            return new Result(false, null, null, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public Spell getSpell() {
            return spell;
        }

        public SignSequence getSequence() {
            return sequence;
        }

        public String getMessage() {
            return message;
        }
    }
}
