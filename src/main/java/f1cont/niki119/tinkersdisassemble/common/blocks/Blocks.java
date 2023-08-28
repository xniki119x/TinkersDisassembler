package f1cont.niki119.tinkersdisassemble.common.blocks;


import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class Blocks {
    public static DisassemblerBlock disassemblerBlock = new DisassemblerBlock();
    public static BlockItem disassemblerBlockItem = new BlockItem(disassemblerBlock,
            new Item.Properties().tab(CreativeModeTab.TAB_MISC));
}
