package f1cont.niki119.tinkersdisassembler.common;

import f1cont.niki119.tinkersdisassembler.common.proxy.CommonProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid="tinkersdisassembler",version = "1.2.1")
public class TinkersDisassembler {
    public static final String MODID = "tinkersdisassembler";
    public static final String MODNAME = "TinkersDisassembler";
    public static final String MODVERSION = "1.2.1";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);
    public static boolean constructArmoryLoaded = false;
    @SidedProxy(clientSide = "f1cont.niki119.tinkersdisassembler.common.proxy.ClientProxy", serverSide = "f1cont.niki119.tinkersdisassembler.common.proxy.CommonProxy")
    public static CommonProxy PROXY;
    public TinkersDisassembler()
    {
        constructArmoryLoaded = Loader.isModLoaded("conarm");
    }
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PROXY.preInit(event);
    }
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init(event);
    }
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit(event);
    }
    public static ResourceLocation prefix(String id){
        return  new ResourceLocation(MODID, id);
    }
}