package f1cont.niki119.tinkersdisassembler.common;

import f1cont.niki119.tinkersdisassembler.common.blocks.Blocks;
import f1cont.niki119.tinkersdisassembler.common.menu.DisassemblerMenu;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TinkersDisassembler.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Register {

    public static final MenuType<DisassemblerMenu> DISASSEMBLER_MENU =
            new MenuType<>((id, inventory) -> new DisassemblerMenu(id, inventory, null, InteractionHand.MAIN_HAND));

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event){
        event.getRegistry().register(Blocks.disassemblerBlock);
    }
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().register(Blocks.disassemblerBlockItem.setRegistryName(TinkersDisassembler.prefix(
                "disassembler")));
    }
    @SubscribeEvent
    public static void registerMenus(RegistryEvent.Register<MenuType<?>> event){
        event.getRegistry().register(DISASSEMBLER_MENU.setRegistryName(TinkersDisassembler.prefix(
                "disassembler")));
    }
}
