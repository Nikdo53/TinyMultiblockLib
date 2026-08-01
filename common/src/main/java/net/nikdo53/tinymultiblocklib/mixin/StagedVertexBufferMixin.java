package net.nikdo53.tinymultiblocklib.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.nikdo53.tinymultiblocklib.client.extensions.IStagedVertexBufferWrapExtension;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.UnaryOperator;

@Mixin(StagedVertexBuffer.class)
public class StagedVertexBufferMixin implements IStagedVertexBufferWrapExtension {
    @Unique
    @Nullable UnaryOperator<VertexConsumer> tinyMultiblockLib$vertexConsumerWrapper = null;

    @Override
    public UnaryOperator<VertexConsumer> getVertexConsumerWrapper() {
        return tinyMultiblockLib$vertexConsumerWrapper;
    }

    @Override
    public void setVertexConsumerWrapper(UnaryOperator<VertexConsumer> wrapper) {
        this.tinyMultiblockLib$vertexConsumerWrapper = wrapper;
    }

    @Override
    public boolean hasVertexConsumerWrapper() {
        return tinyMultiblockLib$vertexConsumerWrapper != null;
    }
}
