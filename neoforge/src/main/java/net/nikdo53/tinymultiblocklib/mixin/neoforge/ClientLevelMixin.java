package net.nikdo53.tinymultiblocklib.mixin.neoforge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.nikdo53.tinymultiblocklib.client.FakeClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/neoforged/bus/api/IEventBus;post(Lnet/neoforged/bus/api/Event;)Lnet/neoforged/bus/api/Event;"))
    private <T extends Event> T afterLoad1(IEventBus instance, T t, Operation<T> original){
        if ((Object) this instanceof FakeClientLevel){
            return null;
        }
        return original.call(instance, t);
    }

}
