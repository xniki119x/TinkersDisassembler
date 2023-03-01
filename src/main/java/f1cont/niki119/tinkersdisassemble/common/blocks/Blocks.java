package f1cont.niki119.tinkersdisassemble.common.blocks;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class Blocks {
    public static DisassemblerBlock disassemblerBlock = new DisassemblerBlock();
    public static BlockItem disassemblerBlockItem = new BlockItem(disassemblerBlock, new Item.Properties().group(ItemGroup.MISC));
}
