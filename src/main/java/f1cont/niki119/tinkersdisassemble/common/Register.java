package f1cont.niki119.tinkersdisassemble.common;

import f1cont.niki119.tinkersdisassemble.common.blocks.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TinkersDisassemble.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Register {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event){
        event.getRegistry().register(Blocks.disassemblerBlock);
    }
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().register(Blocks.disassemblerBlockItem.setRegistryName(TinkersDisassemble.prefix(
                "disassembler")));
    }
}
