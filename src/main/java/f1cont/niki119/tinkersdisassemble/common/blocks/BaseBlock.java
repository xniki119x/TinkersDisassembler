package f1cont.niki119.tinkersdisassemble.common.blocks;

import f1cont.niki119.tinkersdisassemble.common.TinkersDisassemble;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;

public class BaseBlock extends Block
{
    public BaseBlock(String id, Properties properties) {
        super(properties);
        setRegistryName(TinkersDisassemble.prefix(id));
    }
}
