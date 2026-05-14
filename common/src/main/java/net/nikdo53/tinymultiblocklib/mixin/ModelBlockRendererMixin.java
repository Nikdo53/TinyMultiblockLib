package net.nikdo53.tinymultiblocklib.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.nikdo53.tinymultiblocklib.client.MovingBlockRenderStateAdvanced;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {

    @Definition(id = "cull", field = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;cull:Z")
    @Expression("this.cull")
    @WrapOperation(method = "shouldRenderFace", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean shouldRenderFace(ModelBlockRenderer instance, Operation<Boolean> original, @Local(argsOnly = true) BlockAndTintGetter level) {
        Boolean call = original.call(instance);
        if (!call && level instanceof MovingBlockRenderStateAdvanced renderStateCull){
            return renderStateCull.cull;
        }
        return call;
    }

}
