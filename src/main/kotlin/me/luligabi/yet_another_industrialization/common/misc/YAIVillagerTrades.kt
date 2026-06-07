package me.luligabi.yet_another_industrialization.common.misc

import aztech.modern_industrialization.MIRegistries
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.entity.npc.VillagerTrades
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.village.VillagerTradesEvent

object YAIVillagerTrades {

    init {
        NeoForge.EVENT_BUS.addListener(VillagerTradesEvent::class.java, ::init)
    }

    private fun init(event: VillagerTradesEvent) {
        if (!YAI.CONFIG.villagerTrades()) return

        when (event.type) {
            MIRegistries.INDUSTRIALIST.value() -> {
                event.trades.let {
                    it[2].add(VillagerTrades.ItemsForEmeralds(YAIItems.MACHINE_DIAGNOSER.asItem(), 6, 1, 2, 8))

                    it[5].add(VillagerTrades.ItemsForEmeralds(YAIItems.STORAGE_SLOT_LOCKER.asItem(), 12, 1, 2, 24))
                    it[5].add(VillagerTrades.ItemsForEmeralds(YAIItems.MACHINE_REMOVER.asItem(), 48, 1, 1, 48))
                }
            }
            VillagerProfession.FARMER -> {
                event.trades.let {
                    it[5].add(VillagerTrades.EmeraldForItems(YAIItems.CACHACA.asItem(), 3, 4, 50, 24))
                }
            }
        }
    }

}