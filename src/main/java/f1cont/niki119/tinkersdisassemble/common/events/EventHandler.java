package f1cont.niki119.tinkersdisassemble.common.events;

import f1cont.niki119.tinkersdisassemble.common.TinkersDisassemble;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TinkersDisassemble.MODID)
public class EventHandler {

    @SubscribeEvent
    public void rightClickOnBlock(PlayerInteractEvent.RightClickBlock event){
       // event.getUseBlock()
    }
}
