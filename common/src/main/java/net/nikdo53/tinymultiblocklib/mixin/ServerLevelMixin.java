package net.nikdo53.tinymultiblocklib.mixin;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.tinymultiblocklib.CommonEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "gameEvent", at = @At("TAIL"))
    public void gameEvent(Holder<GameEvent> gameEventHolder, Vec3 pos, GameEvent.Context context, CallbackInfo ci) {
        CommonEvents.onVanillaEvent((ServerLevel)((Object) this),  gameEventHolder, pos, context);
    }

}
