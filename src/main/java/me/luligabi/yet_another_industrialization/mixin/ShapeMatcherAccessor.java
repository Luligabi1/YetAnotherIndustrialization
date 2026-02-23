package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ShapeMatcher.class, remap = false)
public interface ShapeMatcherAccessor {

    @Accessor("simpleMembers")
    Map<BlockPos, SimpleMember> getSimpleMembers();
}
