package elucent.eidolon.spell;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

public class SignSequence {
    private final Deque<Sign> sequence = new ArrayDeque<>();
    private Sign last;

    public SignSequence() {
    }

    public SignSequence(Collection<Sign> signs) {
        sequence.addAll(signs);
    }

    public SignSequence(Sign... signs) {
        for (Sign sign : signs) {
            sequence.addLast(sign);
        }
    }

    public void addLeft(Sign sign) {
        sequence.addFirst(sign);
    }

    public void addRight(Sign sign) {
        sequence.addLast(sign);
    }

    public boolean removeLeftmost(Sign sign) {
        if (!sequence.contains(sign)) {
            return false;
        }
        last = sign;
        sequence.removeFirstOccurrence(sign);
        return true;
    }

    public boolean removeRightmost(Sign sign) {
        if (!sequence.contains(sign)) {
            return false;
        }
        last = sign;
        sequence.removeLastOccurrence(sign);
        return true;
    }

    public boolean removeLeftmostN(Sign sign, int count) {
        if (count == 0) {
            return true;
        }
        if (!containsN(sign, count)) {
            return false;
        }
        last = sign;
        for (int i = 0; i < count; i++) {
            sequence.removeFirstOccurrence(sign);
        }
        return true;
    }

    public boolean removeRightmostN(Sign sign, int count) {
        if (count == 0) {
            return true;
        }
        if (!containsN(sign, count)) {
            return false;
        }
        last = sign;
        for (int i = 0; i < count; i++) {
            sequence.removeLastOccurrence(sign);
        }
        return true;
    }

    public Sign getLast() {
        return last;
    }

    public int size() {
        return sequence.size();
    }

    public Deque<Sign> getSigns() {
        return new ArrayDeque<>(sequence);
    }

    public AverageColor getAverageColor() {
        float red = 1.0F;
        float green = 1.0F;
        float blue = 1.0F;
        for (Sign sign : sequence) {
            red += sign.getRed();
            green += sign.getGreen();
            blue += sign.getBlue();
        }
        float divisor = sequence.size() + 1.0F;
        return new AverageColor(red / divisor, green / divisor, blue / divisor);
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (Sign sign : sequence) {
            list.appendTag(new NBTTagString(sign.getRegistryName().toString()));
        }
        tag.setTag("seq", list);
        if (last != null) {
            tag.setString("last", last.getRegistryName().toString());
        }
        return tag;
    }

    public static SignSequence deserializeNBT(NBTTagCompound tag) {
        SignSequence sequence = new SignSequence();
        NBTTagList list = tag.getTagList("seq", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++) {
            Sign sign = Signs.find(new ResourceLocation(list.getStringTagAt(i)));
            if (sign != null) {
                sequence.sequence.addLast(sign);
            }
        }
        if (tag.hasKey("last")) {
            sequence.last = Signs.find(new ResourceLocation(tag.getString("last")));
        }
        return sequence;
    }

    public String describe() {
        StringBuilder builder = new StringBuilder();
        for (Sign sign : sequence) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(sign.getRegistryName().getPath());
        }
        return builder.toString();
    }

    private boolean containsN(Sign sign, int count) {
        int found = 0;
        for (Sign current : sequence) {
            if (sign.equals(current)) {
                found++;
                if (found >= count) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SignSequence)) {
            return false;
        }
        SignSequence that = (SignSequence) other;
        if (that.sequence.size() != sequence.size()) {
            return false;
        }
        Iterator<Sign> left = sequence.iterator();
        Iterator<Sign> right = that.sequence.iterator();
        while (left.hasNext() && right.hasNext()) {
            if (!left.next().equals(right.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return sequence.hashCode();
    }

    public static class AverageColor {
        public final float red;
        public final float green;
        public final float blue;

        public AverageColor(float red, float green, float blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
}
