package f1cont.niki119.tinkersdisassembler.client;

import f1cont.niki119.tinkersdisassembler.client.screen.DisassemblerScreen;
import f1cont.niki119.tinkersdisassembler.common.Register;
import f1cont.niki119.tinkersdisassembler.common.TinkersDisassembler;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TinkersDisassembler.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(Register.DISASSEMBLER_MENU, DisassemblerScreen::new));
    }
}
