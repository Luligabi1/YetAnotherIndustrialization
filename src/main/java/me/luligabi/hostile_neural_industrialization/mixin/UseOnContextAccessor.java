package me.luligabi.hostile_neural_industrialization.mixin;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = UseOnContext.class)
public interface UseOnContextAccessor {

    @Accessor("hitResult")
    BlockHitResult getHitResult();

}