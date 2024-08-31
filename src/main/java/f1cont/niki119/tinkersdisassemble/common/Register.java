package f1cont.niki119.tinkersdisassemble.common;

import f1cont.niki119.tinkersdisassemble.client.gui.DisassemblerContainer;
import f1cont.niki119.tinkersdisassemble.client.gui.DisassemblerGui;
import f1cont.niki119.tinkersdisassemble.common.blocks.DisassemblerBlock;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;

public class Register {

    public static DisassemblerBlock disassemblerBlock = new DisassemblerBlock();
    public static BlockItem disassemblerBlockItem = new BlockItem(disassemblerBlock,
            new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static MenuType<DisassemblerContainer> DISASSEMBLER_CONTAINER = IForgeMenuType.create(DisassemblerContainer::fromNetwork);

    public static void registerBlocks(RegistryEvent.Register<Block> event){
        event.getRegistry().register(disassemblerBlock);
    }
    public static void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().register(disassemblerBlockItem.setRegistryName(TinkersDisassembler.prefix(
                "disassembler")));
    }
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event){
        event.getRegistry().register(DISASSEMBLER_CONTAINER.setRegistryName("disassembler_container"));

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MenuScreens.register(DISASSEMBLER_CONTAINER, DisassemblerGui::new);
        });
    }
}
