package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import me.luligabi.yet_another_industrialization.common.YAI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MachineBlockEntity.class, remap = false)
public abstract class MachineBlockEntityMixin {

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void getDisplayName(CallbackInfoReturnable<Component> callback) {
        MachineBlockEntity machine = (MachineBlockEntity) (Object) this;

        var blockId = BuiltInRegistries.BLOCK.getKey(machine.getBlockState().getBlock());
        if (blockId.getNamespace().equals(YAI.ID)) {
            callback.setReturnValue(machine.getBlockState().getBlock().getName());
        }
    }

}