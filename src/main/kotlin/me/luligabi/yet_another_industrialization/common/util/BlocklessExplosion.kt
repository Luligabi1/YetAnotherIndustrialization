package me.luligabi.yet_another_industrialization.common.util

import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundExplodePacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level

/**
 * Causes damage but doesn't break any blocks
 * Can't believe vanilla doesn't support this...
 */
class BlocklessExplosion private constructor(
    level: Level?, source: Entity?,
    x: Double, y: Double, z: Double,
    radius: Float
): Explosion(level, source, x, y, z, radius, false, BlockInteraction.DESTROY) {

    override fun finalizeExplosion(spawnParticles: Boolean) {
        toBlow.clear()
        super.finalizeExplosion(spawnParticles)
    }

    companion object {

        fun create(
            level: Level, source: Entity?,
            x: Double, y: Double, z: Double,
            radius: Float = 4f
        ) {
            val explosion = BlocklessExplosion(level, source, x, y, z, radius)
            explosion.explode()
            explosion.finalizeExplosion(true)

            if (level.isClientSide) return
            (level as? ServerLevel)?.let {
                for (serverPlayer in it.players) {
                    if (serverPlayer.distanceToSqr(x, y, z) < 4096.0) {
                        serverPlayer.connection.send(
                            ClientboundExplodePacket(
                                x, y, z,
                                radius,
                                explosion.toBlow, explosion.hitPlayers[serverPlayer],
                                explosion.blockInteraction,
                                explosion.smallExplosionParticles, explosion.largeExplosionParticles,
                                explosion.explosionSound
                            )
                        )
                    }
                }
            }
        }

        fun create(
            level: Level, source: Entity?,
            pos: BlockPos,
            radius: Float = 4f
        ) {
            create(
                level,
                source,
                pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(),
                radius
            )
        }

    }
}