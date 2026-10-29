package f1cont.niki119.tinkersdisassembler.common.menu;

import f1cont.niki119.tinkersdisassembler.common.DisassemblerLogic;
import f1cont.niki119.tinkersdisassembler.common.Register;
import f1cont.niki119.tinkersdisassembler.common.blocks.Blocks;
import f1cont.niki119.tinkersdisassembler.common.blocks.DisassemblerBlock;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;

/**
 * Меню Разборщика. Слот при открытии забирает предмет из руки игрока и служит только
 * витриной: забрать его мышкой нельзя, но наведение показывает обычный тултип.
 * Ячейки результата рисуются на экране как полупрозрачный предпросмотр и слотами не являются.
 */
public class DisassemblerMenu extends AbstractContainerMenu {

    public static final int DISASSEMBLE_BUTTON = 0;

    private final Player player;
    private final ContainerLevelAccess access;
    private final InteractionHand hand;
    private final SimpleContainer toolContainer = new SimpleContainer(1);
    private int cost;
    private int enough;

    private final DataSlot costData = new DataSlot() {
        @Override
        public int get() {
            return cost;
        }

        @Override
        public void set(int value) {
            cost = value;
        }
    };

    private final DataSlot enoughData = new DataSlot() {
        @Override
        public int get() {
            return enough;
        }

        @Override
        public void set(int value) {
            enough = value;
        }
    };

    public DisassemblerMenu(int id, Inventory playerInventory, ContainerLevelAccess access, InteractionHand hand) {
        super(Register.DISASSEMBLER_MENU, id);
        this.player = playerInventory.player;
        this.access = access;
        this.hand = hand;
        // слот-квадрат в текстуре 26x26 от (75,9): предмет 16x16 рисуется от координаты слота,
        // поэтому точку сдвигаем в центр квадрата
        this.addSlot(new Slot(toolContainer, 0, 80, 14) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                return false;
            }
        });
        this.addDataSlot(this.costData);
        this.addDataSlot(this.enoughData);
        if (!this.player.level.isClientSide) {
            ItemStack held = this.player.getItemInHand(hand);
            if (!held.isEmpty()) {
                this.toolContainer.setItem(0, held.copy());
                //this.player.setItemInHand(hand, ItemStack.EMPTY);
            }
        }
    }

    public int getXPCost() {
        return this.cost;
    }

    public boolean isEnoughXP() {
        return this.enough == 1;
    }

    @Override
    public void broadcastChanges() {
        if (!this.player.level.isClientSide) {
            ItemStack tool = this.toolContainer.getItem(0);
            this.cost = DisassemblerLogic.calculateCost(this.player.level, tool);
            // разбираются только тинкеровские инструменты: прочие предметы кнопку не активируют
            this.enough = !tool.isEmpty() && tool.getItem() instanceof IModifiableDisplay
                    && this.player.experienceLevel >= this.cost ? 1 : 0;
        }
        super.broadcastChanges();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id != DISASSEMBLE_BUTTON || this.access != null && !Boolean.TRUE.equals(this.access.evaluate(
                (level, pos) -> level.getBlockState(pos).getBlock() instanceof DisassemblerBlock, Boolean.FALSE))) {
            return false;
        }
        ItemStack tool = this.toolContainer.getItem(0);
        if (tool.isEmpty()) {
            return false;
        }
        if (DisassemblerLogic.disassemble(player, this.player.level, tool) < 0) {
            return false;
        }
        this.toolContainer.setItem(0, ItemStack.EMPTY);
        this.player.setItemInHand(hand, ItemStack.EMPTY);
        player.closeContainer();
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // единственный слот неинтерактивен — шифт-клик ничего не делает
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.access == null || stillValid(this.access, player, Blocks.disassemblerBlock);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        //ItemStack tool = this.toolContainer.getItem(0);
        //if (!tool.isEmpty()) {

            // вернуть в ту же руку, если она свободна, иначе в инвентарь, иначе на пол
            //if (player.getItemInHand(this.hand).isEmpty()) {
            //    player.setItemInHand(this.hand, tool);
            //} else if (!player.getInventory().add(tool)) {
            //    player.drop(tool, false);
            //}
            //this.toolContainer.removeItem(0, tool.getCount());
        //}
    }
}
