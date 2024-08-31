package f1cont.niki119.tinkersdisassemble.common.containers;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DisassemblerOutputContainer implements Container {
    private final NonNullList<ItemStack> output;

    public DisassemblerOutputContainer(){
        output = NonNullList.withSize(18, ItemStack.EMPTY);
    }
    @Override
    public int getContainerSize() {
        return 18;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int i) {
        return output.get(i);
    }

    @Override
    public ItemStack removeItem(int i, int i1) {
        return ContainerHelper.takeItem(output, i);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(output, i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        output.set(i, itemStack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        output.clear();
    }
}
