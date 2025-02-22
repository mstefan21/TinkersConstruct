package slimeknights.tconstruct.library.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import slimeknights.tconstruct.common.Sounds;

import java.util.IdentityHashMap;

/** Logic for entities bouncing */
public class SlimeBounceHandler {
  private SlimeBounceHandler() {}
  private static final IdentityHashMap<Entity, BounceInfo> BOUNCING_ENTITIES = new IdentityHashMap<>();

  /** Registers event handlers */
  public static void init() {
    MinecraftForge.EVENT_BUS.addListener(SlimeBounceHandler::onLivingTick);
    MinecraftForge.EVENT_BUS.addListener(SlimeBounceHandler::serverStopping);
  }

  /**
   * Preserves entity air momentum
   * @param entity  Entity to bounce
   */
  public static void addBounceHandler(LivingEntity entity) {
    addBounceHandler(entity, 0d);
  }

  /**
   * Causes the entity to bounce, needed because the fall event will reset motion afterwards
   * @param entity  Entity to bounce
   * @param bounce  Bounce amount
   */
  public static void addBounceHandler(LivingEntity entity, double bounce) {
    // no fake players PlayerTick event
    if (entity instanceof FakePlayer) {
      return;
    }
    // update bounce info
    BounceInfo info = BOUNCING_ENTITIES.get(entity);
    if (info == null) {
      BOUNCING_ENTITIES.put(entity, new BounceInfo(entity, bounce));
    } else if (bounce != 0) {
      // updated bounce if needed
      info.bounce = bounce;
      // add one to the tick as there is a 1 tick delay between falling and ticking for many entities
      info.bounceTick = entity.ticksExisted + 1;
      Vector3d motion = entity.getMotion();
      info.lastMagSq = motion.x * motion.x + motion.z * motion.z;
      info.lastAngle = MathHelper.atan2(motion.z, motion.x);
    }
  }

  /** Called on living tick to preserve momentum and bounce */
  private static void onLivingTick(LivingUpdateEvent event) {
    LivingEntity entity = event.getEntityLiving();
    BounceInfo info = BOUNCING_ENTITIES.get(entity);

    // if we have info for this entity, time to work
    if (info != null) {
      // if flying, nothing to do
      if (entity.isElytraFlying() || entity.isSpectator()) {
        BOUNCING_ENTITIES.remove(entity);
        return;
      }

      // if its the bounce tick, time to bounce. This is to circumvent the logic that resets y motion after landing
      if (entity.ticksExisted == info.bounceTick) {
        Vector3d motion = entity.getMotion();
        entity.setMotion(motion.x, info.bounce, motion.z);
        info.bounceTick = 0;
      }

      boolean isInAir = !entity.isOnGround() && !entity.isInWater() && !entity.isOnLadder();

      // preserve motion
      if (isInAir && info.lastMagSq > 0) {
        // figure out how much motion has reduced
        Vector3d motion = entity.getMotion();
        double motionSq = motion.x * motion.x + motion.z * motion.z;
        // if not moving, cancel velocity preserving in 5 ticks
        if (motionSq == 0) {
          if (info.stopMagTick == 0) {
            info.stopMagTick = entity.ticksExisted + 5;
          } else if (entity.ticksExisted > info.stopMagTick) {
            info.lastMagSq = 0;
          }
        } else if (motionSq < info.lastMagSq) {
          info.stopMagTick = 0;
          // preserve 95% of former speed
          double boost = Math.sqrt(info.lastMagSq / motionSq) * 0.95f;
          if (boost > 1) {
            entity.setMotion(motion.x * boost, motion.y, motion.z * boost);
            entity.isAirBorne = true;
            info.lastMagSq = info.lastMagSq * 0.95f * 0.95f;
            // play sound if we had a big angle change
            double newAngle = MathHelper.atan2(motion.z, motion.x);
            if (Math.abs(newAngle - info.lastAngle) > 1) {
              entity.playSound(Sounds.SLIMY_BOUNCE.getSound(), 1.0f, 1.0f);
            }
            info.lastAngle = newAngle;
          } else {
            info.lastMagSq = motionSq;
            info.lastAngle = MathHelper.atan2(motion.z, motion.x);
          }
        }
      }

      // timing the effect out
      if (info.wasInAir && !isInAir) {
        if (info.endHandler == 0) {
          info.endHandler = entity.ticksExisted + 5;
        } else if (entity.ticksExisted > info.endHandler) {
          BOUNCING_ENTITIES.remove(entity);
        }
      } else {
        info.endHandler = 0;
        info.wasInAir = true;
      }
    }
  }

  /** Called on server shutdown to prevent memory leaks */
  private static void serverStopping(FMLServerStoppingEvent event) {
    BOUNCING_ENTITIES.clear();
  }

  /** Data class to keep track of bouncing info for an entity */
  private static class BounceInfo {
    /** Velocity the entity should have, unused if 0 */
    private double bounce;
    /** Time to update the entities velocity */
    private int bounceTick;
    /** Tick to stop entity magnitude changes */
    private int stopMagTick;
    /** Magnitude of the X/Z motion last tick */
    private double lastMagSq;
    /** If true, the entity was in air last tick */
    private boolean wasInAir = false;
    /** Time when motion should stop */
    private int endHandler = 0;
    /** Last angle of motion, used for sound effects */
    private double lastAngle;

    public BounceInfo(LivingEntity entity, double bounce) {
      this.bounce = bounce;
      if (bounce != 0) {
        // add one to the tick as there is a 1 tick delay between falling and ticking for many entities
        this.bounceTick = entity.ticksExisted + 1;
      } else {
        this.bounceTick = 0;
      }
      Vector3d motion = entity.getMotion();
      this.lastMagSq = motion.x * motion.x + motion.z * motion.z;
    }
  }
}
