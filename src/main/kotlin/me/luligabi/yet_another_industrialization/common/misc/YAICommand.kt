package me.luligabi.yet_another_industrialization.common.misc

import com.mojang.brigadier.context.CommandContext
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent


object YAICommand {

    init {
        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent::class.java, { event ->
            event.dispatcher.register(
                literal("yai")
                    .requires({ source -> source.hasPermission(4) })
                    .then(
                        literal("data_maps")
                            .then(literal(ArboreousGreenhouseBlockEntity.ID)
                                .executes({ ctx ->
                                return@executes arboreousGreenhouseTiers(ctx)
                            })
                        )
                    )
            )
        })
    }

    private fun arboreousGreenhouseTiers(ctx: CommandContext<CommandSourceStack>): Int {
        ctx.source.sendSystemMessage(Component.literal("--- Arboreous Greenhouse Tiers ---"))
        for (tier in ArboreousGreenhouseBlockEntity.TIERS) {
            ctx.source.sendSystemMessage(Component.literal("--- ${tier.id} (${tier.translationKey}) ---"))
            ctx.source.sendSystemMessage(Component.literal("Soils: ${tier.validSoils.toIds()}"))
        }
        return 1
    }

    private fun Set<Block>.toIds(): String {
        return this.joinToString(", ") { block ->
            BuiltInRegistries.BLOCK.getKey(block).toString()
        }
    }

}