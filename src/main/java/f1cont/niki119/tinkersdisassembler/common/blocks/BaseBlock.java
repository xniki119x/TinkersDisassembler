package f1cont.niki119.tinkersdisassembler.common.blocks;

import f1cont.niki119.tinkersdisassembler.common.TinkersDisassembler;
import net.minecraft.world.level.block.Block;

public class BaseBlock extends Block
{
    public BaseBlock(String id, Properties properties) {
        super(properties);
        setRegistryName(TinkersDisassembler.prefix(id));
    }
}
