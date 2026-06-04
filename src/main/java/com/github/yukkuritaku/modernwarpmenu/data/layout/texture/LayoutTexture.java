package com.github.yukkuritaku.modernwarpmenu.data.layout.texture;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public record LayoutTexture(Identifier location, int width, int height) {
    public static final MapCodec<LayoutTexture> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("location").forGetter(LayoutTexture::location),
                    ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(LayoutTexture::width),
                    ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(LayoutTexture::height)
            ).apply(instance, LayoutTexture::new));
}
