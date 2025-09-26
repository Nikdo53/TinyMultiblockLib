package net.nikdo53.tinymultiblocklib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.tinymultiblocklib.blockentities.IMultiBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ShutUpWarnsMixin implements ServerPlayerConnection, TickablePacketListener, ServerGamePacketListener {

    /**
     * Minecraft loves spamming errors when the player interacts with a VoxelShape larger than 1 block, this shuts it up
     * */
    @ModifyVariable(method = "handleUseItemOn", at = @At(value = "STORE"), ordinal = 1)
    public Vec3 useAllower(Vec3 vec3, @Local ServerLevel level, @Local BlockPos pos) {
        if(level.getBlockEntity(pos) instanceof IMultiBlockEntity){
            return new Vec3(0, 0 , 0);
        }
        return vec3;
    }

}
