package f1cont.niki119.tinkersdisassemble.client.gui;

import f1cont.niki119.tinkersdisassemble.common.Register;
import f1cont.niki119.tinkersdisassemble.common.containers.DisassemblerOutputContainer;
import f1cont.niki119.tinkersdisassemble.common.containers.DisassemblerResult;
import f1cont.niki119.tinkersdisassemble.common.containers.DisassemblerToolSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class DisassemblerContainer extends AbstractContainerMenu {

    public static DisassemblerContainer fromNetwork(int windows_id, Inventory inventory, FriendlyByteBuf buf){
        return new DisassemblerContainer(windows_id, inventory);
    }

    public DisassemblerContainer(int window_id, Inventory inventory) {
        super(Register.DISASSEMBLER_CONTAINER, window_id);
        DisassemblerToolSlot toolSlot = new DisassemblerToolSlot();
        DisassemblerOutputContainer output = new DisassemblerOutputContainer();
        this.addSlot(new Slot(toolSlot, 0,80,62)); // Main Slot

        for(int i = 0; i < 2; ++i){
            for(int k = 0; k < 9; ++k){
                this.addSlot(new Slot(output, k + i * 9,8 + k*18, 8 + i*18));
            }
        }

        for(int i = 0; i < 3; ++i) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inventory, 1+k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory,1+ i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
