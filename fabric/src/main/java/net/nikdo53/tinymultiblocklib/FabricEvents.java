package net.nikdo53.tinymultiblocklib;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class FabricEvents {
    public static void register(){
        UseBlockCallback.EVENT.register(FabricEvents::testRightClickEvent);
    }

    public static InteractionResult testRightClickEvent(Player player, Level level, InteractionHand hand, BlockHitResult hitResult){
        CommonEvents.testRightClickBlock(level, hitResult.getBlockPos(), player);
        return InteractionResult.PASS;
    }
}
