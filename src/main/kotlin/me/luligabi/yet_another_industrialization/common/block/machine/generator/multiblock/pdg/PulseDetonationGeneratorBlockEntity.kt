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
import me.luligabi.yet_another_industrialization.common.util.BlocklessExplosion
import me.luligabi.yet_another_industrialization.mixin.ShapeMatcherAccessor

class PulseDetonationGeneratorBlockEntity(bep: BEP): YAIGeneratorMultiblockBlockEntity(
    bep,
    YAI.Companion.id(ID),
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
                { char: Char, y: Int -> char == '#'} to CASING,
                { char: Char, y: Int -> char == 'x' } to CASING_PIPE,
                { char: Char, y: Int -> char == '@' } to CORE
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
    }

    private val chambers = mutableListOf<DetonationChamberCasingBlockEntity>()

    override fun onSuccessfulRematch(shapeMatcher: ShapeMatcher) {
        (shapeMatcher as ShapeMatcherAccessor).simpleMembers.forEach { pos, _ ->
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
        BlocklessExplosion.create(level!!, null, pos, 15f)

//        val f2: Float = this.radius * 2.0f
//        val k1 = Mth.floor(this.x - f2.toDouble() - 1.0)
//        val l1 = Mth.floor(this.x + f2.toDouble() + 1.0)
//        val i2 = Mth.floor(this.y - f2.toDouble() - 1.0)
//        val i1 = Mth.floor(this.y + f2.toDouble() + 1.0)
//        val j2 = Mth.floor(this.z - f2.toDouble() - 1.0)
//        val j1 = Mth.floor(this.z + f2.toDouble() + 1.0)
//        val list = this.level!!.getEntities(
//            this.source,
//            AABB(k1.toDouble(), i2.toDouble(), j2.toDouble(), l1.toDouble(), i1.toDouble(), j1.toDouble())
//        )
//        EventHooks.onExplosionDetonate(this.level, this, list, f2.toDouble())
//        val vec3 = Vec3(this.x, this.y, this.z)
//
//        for (entity in list) {
//            if (!entity.ignoreExplosion(this)) {
//                val d11 = sqrt(entity.distanceToSqr(vec3)) / f2.toDouble()
//                if (d11 <= 1.0) {
//                    var d5: Double = entity.getX() - this.x
//                    var d7: Double = (if (entity is PrimedTnt) entity.getY() else entity.getEyeY()) - this.y
//                    var d9: Double = entity.getZ() - this.z
//                    val d12 = sqrt(d5 * d5 + d7 * d7 + d9 * d9)
//                    if (d12 != 0.0) {
//                        d5 /= d12
//                        d7 /= d12
//                        d9 /= d12
//                        if (this.damageCalculator.shouldDamageEntity(this, entity)) {
//                            entity.hurt(this.damageSource, this.damageCalculator.getEntityDamageAmount(this, entity))
//                        }
//
//                        val d13 = (1.0 - d11) * Explosion.getSeenPercent(vec3, entity)
//                            .toDouble() * this.damageCalculator.getKnockbackMultiplier(entity).toDouble()
//                        val d10: Double
//                        if (entity is LivingEntity) {
//                            val livingentity = entity
//                            d10 =
//                                d13 * (1.0 - livingentity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE))
//                        } else {
//                            d10 = d13
//                        }
//
//                        d5 *= d10
//                        d7 *= d10
//                        d9 *= d10
//                        var vec31 = Vec3(d5, d7, d9)
//                        entity.deltaMovement = entity.deltaMovement.add(vec31)
//                        if (entity is Player) {
//                            val player = entity
//                            if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
//                                this.hitPlayers.put(player, vec31)
//                            }
//                        }
//
//                        entity.onExplosionHit(null)
//                    }
//                }
//            }
//        }



        // FIXME
//        if (this.level!!.isClientSide) {
//            this.level!!.playLocalSound(
//                this.x,
//                this.y,
//                this.z,
//                this.explosionSound.value() as SoundEvent,
//                SoundSource.BLOCKS,
//                4.0f,
//                (1.0f + (this.level!!.random.nextFloat() - this.level!!.random.nextFloat()) * 0.2f) * 0.7f,
//                false
//            )
//        }


    //        level!!.addParticle(ParticleTypes.EXPLOSION, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 1.0, .0, .0)
    }

}