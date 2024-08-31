package f1cont.niki119.tinkersdisassemble.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("tinkersdisassembler")
public class TinkersDisassembler {
    public static final String MODID = "tinkersdisassembler";
    public static final String MODNAME = "Tinkers Disassembler";
    public static final String MODVERSION = "1.2.0";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);
    public TinkersDisassembler() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addGenericListener(Block.class, Register::registerBlocks);
        bus.addGenericListener(Item.class, Register::registerItems);
        bus.addGenericListener(MenuType.class, Register::registerContainers);
    }
    public static ResourceLocation prefix(String id){
        return  new ResourceLocation(MODID, id);
    }
}
