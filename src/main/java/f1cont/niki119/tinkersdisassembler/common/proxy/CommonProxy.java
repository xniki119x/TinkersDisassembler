package f1cont.niki119.tinkersdisassembler.common.proxy;

import f1cont.niki119.tinkersdisassembler.common.registry.RegistryBlocks;
import f1cont.niki119.tinkersdisassembler.common.registry.RegistryCrafts;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        RegistryBlocks.register();
    }

    public void init(FMLInitializationEvent event)
    {
        RegistryCrafts.register();
    }

    public void postInit(FMLPostInitializationEvent event) {

    }

}