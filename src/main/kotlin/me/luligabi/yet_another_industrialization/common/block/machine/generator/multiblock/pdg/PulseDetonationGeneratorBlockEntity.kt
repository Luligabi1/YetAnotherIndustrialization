package me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg

import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.models.MachineCasings
import aztech.modern_industrialization.machines.multiblocks.*
import aztech.modern_industrialization.materials.MIMaterials
import aztech.modern_industrialization.materials.part.MIParts
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMultiblockHelper
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.YAIGeneratorMultiblockBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.chamber.DetonationChamberCasingBlockEntity
import me.luligabi.yet_another_industrialization.common.misc.YAIDamageTypes
import me.luligabi.yet_another_industrialization.mixin.ShapeMatcherAccessor
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.ExplosionDamageCalculator
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class PulseDetonationGeneratorBlockEntity(bep: BEP): YAIGeneratorMultiblockBlockEntity(
    bep,
    YAI.id(ID),
    arrayOf(SHAPE)
) {

    companion object : YAIMultiblockHelper {

        const val ID = "pulse_detonation_generator"
        const val NAME = "Pulse Detonation Generator"

        private val CASING = SimpleMember.forBlock { MIMaterials.TITANIUM.getPart(MIParts.MACHINE_CASING_SPECIAL).asBlock() }
        private val CASING_PIPE = SimpleMember.forBlock { MIMaterials.TITANIUM.getPart(MIParts.MACHINE_CASING_PIPE).asBlock() }
        private val CORE = SimpleMember.forBlock { YAIMachines.getMachineFromId(DetonationChamberCasingBlockEntity.ID) }

        private val LAYER_0 = listOf(
            "___#xxx#___",
            "__#_____#__",
            "_#_______#_",
            "#_________#",
            "x____@____x",
            "x___@_@___x",
            "x____@____x",
            "#_________#",
            "_#_______#_",
            "__#_____#__",
            "___#xxx#___"
        )

        private val LAYER_1 = listOf(
            "_____x_____",
            "___________",
            "___________",
            "___________",
            "___________",
            "x____@____x",
            "___________",
            "___________",
            "___________",
            "___________",
            "_____x_____"
        )

        private val LAYER_2 = listOf(
            "_____#_____",
            "___________",
            "___________",
            "___________",
            "___________",
            "#_________#",
            "___________",
            "___________",
            "___________",
            "___________",
            "_____#_____"
        )

        private val LAYER_3 = listOf(
            "___________",
            "_____#_____",
            "___________",
            "___________",
            "___________",
            "_#_______#_",
            "___________",
            "___________",
            "___________",
            "_____#_____",
            "___________"
        )

        private val LAYER_4 = listOf(
            "___________",
            "___________",
            "_____#_____",
            "___________",
            "___________",
            "__#_____#__",
            "___________",
            "___________",
            "_____#_____",
            "___________",
            "___________"
        )

        private val LAYER_5 = listOf(
            "___________",
            "___________",
            "___________",
            "_____#_____",
            "_____x_____",
            "___#xxx#___",
            "_____x_____",
            "_____#_____",
            "___________",
            "___________",
            "___________"
        )

        override val pattern = listOf(
            LAYER_5,
            LAYER_4,
            LAYER_3,
            LAYER_2,
            LAYER_1,
            LAYER_0,
            LAYER_1,
            LAYER_2,
            LAYER_3,
            LAYER_4,
            LAYER_5,
        )

        override val materialRules: Map<(Char, Int) -> Boolean, SimpleMember>
            get() = mapOf(
                { char: Char, _: Int -> char == '#'} to CASING,
                { char: Char, _: Int -> char == 'x' } to CASING_PIPE,
                { char: Char, _: Int -> char == '@' } to CORE
            )

        override val hatches: HatchFlags
            get() = HatchFlags.Builder()
                .with(
                    HatchTypes.ITEM_INPUT,
                    HatchTypes.FLUID_INPUT, HatchTypes.FLUID_OUTPUT,
                    HatchTypes.ENERGY_OUTPUT
                )
                .build()

        override val hatchPredicate: Map<(Char, Int) -> Boolean, HatchFlags>
            get() = mapOf(
                { char: Char, _: Int -> char == 'x' } to hatches
            )

        override val controllerXOffset = -5

        val SHAPE = ShapeTemplate.Builder(MachineCasings.TITANIUM_PIPE)
            .addLayer(-5, 0)
            .addLayer(-4, 1)
            .addLayer(-3, 2)
            .addLayer(-2, 3)
            .addLayer(-1, 4)
            .addLayer(0, 5)
            .addLayer(1, 6)
            .addLayer(2, 7)
            .addLayer(3, 8)
            .addLayer(4, 9)
            .addLayer(5, 10)
            .build()

        private val EXPLOSION_CALCULATOR: ExplosionDamageCalculator = object : ExplosionDamageCalculator() {

            override fun shouldBlockExplode(explosion: Explosion,
                                            reader: BlockGetter,
                                            pos: BlockPos,
                                            state: BlockState,
                                            power: Float) = false

            override fun shouldDamageEntity(explosion: Explosion,
                                            entity: Entity) = true

        }
    }

    private val chambers = mutableListOf<DetonationChamberCasingBlockEntity>()

    override fun onSuccessfulRematch(shapeMatcher: ShapeMatcher) {
        (shapeMatcher as ShapeMatcherAccessor).simpleMembers.forEach { (pos, _) ->
            (level?.getBlockEntity(pos) as? DetonationChamberCasingBlockEntity)?.let {
                it.controller = this
                chambers.add(it)
            }
        }
    }

    override fun onFailedRematch(shapeMatcher: ShapeMatcher) {
        chambers.forEach {
            it.controller = null
        }
        chambers.clear()
    }

    override fun recipeType() = YAIMachines.RecipeTypes.PULSE_DETONATION_GENERATOR



    override fun onInsert(hasInsertedEnergy: Boolean) {
        val pos = blockPos.relative(orientation.facingDirection.opposite, 5).above()
        (level as ServerLevel).explode(
            null,
            YAIDamageTypes.pdg(level!!),
            EXPLOSION_CALCULATOR,
            pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(),
            5.0F,
            false,
            Level.ExplosionInteraction.NONE,
            ParticleTypes.EXPLOSION_EMITTER,
            ParticleTypes.EXPLOSION_EMITTER,
            SoundEvents.GENERIC_EXPLODE
        )
    }

}