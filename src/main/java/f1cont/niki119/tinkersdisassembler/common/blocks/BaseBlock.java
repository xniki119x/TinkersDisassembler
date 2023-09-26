package f1cont.niki119.tinkersdisassembler.common.blocks;

import f1cont.niki119.tinkersdisassembler.common.TinkersDisassembler;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BaseBlock extends Block {
    public BaseBlock(String id) {
        super(Material.WOOD);
        setRegistryName(TinkersDisassembler.prefix(id));
        setTranslationKey(TinkersDisassembler.prefix(id).toString());
        setHardness(0.5F);
        setSoundType(SoundType.WOOD);
    }
}
