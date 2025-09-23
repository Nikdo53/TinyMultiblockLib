package net.nikdo53.tinymultiblocklib.mixin.carryon;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.common.carry.PickupHandler;

import java.util.function.BiFunction;

@Mixin(PickupHandler.class)
public class PickupHandlerMixin {

    @Inject(method = "tryPickUpBlock", at = @At(value = "INVOKE", target = "Ltschipp/carryon/common/carry/CarryOnDataManager;getCarryData(Lnet/minecraft/world/entity/player/Player;)Ltschipp/carryon/common/carry/CarryOnData;", shift = At.Shift.AFTER), remap = false)
    private static void tryPickUpBlock(ServerPlayer player, BlockPos pos, Level level, BiFunction<BlockState, BlockPos, Boolean> pickupCallback, CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) LocalRef<BlockPos> posLocalRef) {
        if (IMultiBlock.isMultiblock(level, pos)) {
            posLocalRef.set(IMultiBlock.getCenter(level, pos));
        }
    }

}
