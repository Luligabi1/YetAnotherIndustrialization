package me.luligabi.yet_another_industrialization.mixin;

import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Screen.class)
public interface ScreenAccessor {

    @Invoker("rebuildWidgets")
    void invokeRebuildWidgets();

}