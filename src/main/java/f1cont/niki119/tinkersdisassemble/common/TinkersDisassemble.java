package f1cont.niki119.tinkersdisassemble.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("tinkersdisassemble")
public class TinkersDisassemble {
    public static final String MODID = "tinkersdisassemble";
    public static final String MODNAME = "Tinkers Disassemble";
    public static final String MODVERSION = "1.2.0";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);
    public TinkersDisassemble() {

    }
    public static ResourceLocation prefix(String id){
        return  new ResourceLocation(MODID, id);
    }
}
