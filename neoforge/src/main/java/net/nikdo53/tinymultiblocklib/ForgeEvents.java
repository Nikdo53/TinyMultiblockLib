package net.nikdo53.tinymultiblocklib;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class ForgeEvents {
    public static void register(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(ForgeEvents::testRightClickEvent);
    }

    public static void testRightClickEvent(PlayerInteractEvent.RightClickBlock event) {
        CommonEvents.testRightClickBlock(event.getLevel(), event.getPos(), event.getEntity());
    }
}
