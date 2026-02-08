package net.nikdo53.tinymultiblocklib.mixin;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Level.class)
public interface LevelAccessorMixin {

    @Accessor(value = "isClientSide")
    @Mutable
    void tmbl$setClientSide(boolean clientSided);
}
