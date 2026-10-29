package f1cont.niki119.tinkersdisassembler.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("tinkersdisassembler")
public class TinkersDisassembler {
    public static final String MODID = "tinkersdisassembler";
    public static final String MODNAME = "Tinkers Disassembler";
    public static final String MODVERSION = "2.0.0";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);
    public TinkersDisassembler() {

    }
    public static ResourceLocation prefix(String id){
        return  new ResourceLocation(MODID, id);
    }
}
