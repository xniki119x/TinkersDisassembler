package f1cont.niki119.tinkersdisassemble.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static f1cont.niki119.tinkersdisassemble.common.blocks.Blocks.disassemblerBlock;
import static f1cont.niki119.tinkersdisassemble.common.blocks.Blocks.disassemblerBlockItem;

@Mod.EventBusSubscriber(modid = TinkersDisassemble.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Register {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event){
        event.getRegistry().register(disassemblerBlock);
    }
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().register(disassemblerBlockItem.setRegistryName(TinkersDisassemble.prefix("disassembler")));
    }
}
