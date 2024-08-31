package f1cont.niki119.tinkersdisassemble.common.containers;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DisassemblerToolSlot implements Container {
    private final NonNullList<ItemStack> toolSlot;
    public DisassemblerToolSlot(){
        toolSlot = NonNullList.withSize(1, ItemStack.EMPTY);
    }
    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return toolSlot.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        return toolSlot.get(0);
    }

    @Override
    public ItemStack removeItem(int i, int i1) {
        return ContainerHelper.takeItem(toolSlot, 0);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(toolSlot, 0);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        toolSlot.set(0, itemStack);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        toolSlot.clear();
    }
}
