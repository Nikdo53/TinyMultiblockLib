package net.nikdo53.tinymultiblocklib.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.nikdo53.tinymultiblocklib.client.extensions.IStagedVertexBufferWrapExtension;
import net.nikdo53.tinymultiblocklib.client.TintedBufferSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer$Group")
public class RenderTypeFeatureRendererMixin {

    @Shadow
    @Final
    private StagedVertexBuffer stagedBuffer;

    @WrapMethod(method = "getVertexBuilder")
    public VertexConsumer getVertexBuilder(RenderType renderType, Operation<VertexConsumer> original) {
        IStagedVertexBufferWrapExtension extension = (IStagedVertexBufferWrapExtension) this.stagedBuffer;
        if (extension.hasVertexConsumerWrapper()) {
            RenderType renderType1 = TintedBufferSource.getTranslucent(renderType);
            return extension.getVertexConsumerWrapper().apply(original.call(renderType1));
        } else {
            return original.call(renderType);
        }
    }

}
