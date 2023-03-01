package f1cont.niki119.tinkersdisassemble.common;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("tinkersdisassemble")
public class TinkersDisassemble {
    public static final String MODID = "tinkersdisassemble";
    public static final String MODNAME = "Tinkers Disassemble";
    public static final String MODVERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);
    public TinkersDisassemble() {

    }
    public static ResourceLocation prefix(String id){
        return  new ResourceLocation(MODID, id);
    }
}
