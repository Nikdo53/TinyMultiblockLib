package net.nikdo53.tinymultiblocklib.client.extensions;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jspecify.annotations.Nullable;

import java.util.function.UnaryOperator;

//implemented via mixin
public interface IStagedVertexBufferWrapExtension {
    @Nullable UnaryOperator<VertexConsumer> getVertexConsumerWrapper();

    void setVertexConsumerWrapper(@Nullable UnaryOperator<VertexConsumer> wrapper);

    boolean hasVertexConsumerWrapper();
}
