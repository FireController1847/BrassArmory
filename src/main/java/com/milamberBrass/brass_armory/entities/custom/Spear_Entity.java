package com.milamberBrass.brass_armory.entities.custom;

import com.milamberBrass.brass_armory.BrassArmory;
import com.milamberBrass.brass_armory.entities.ModEntityTypes;
import com.milamberBrass.brass_armory.items.ModItems;
import com.milamberBrass.brass_armory.items.custom.Spear;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class Spear_Entity extends AbstractArrowEntity {
    private ItemStack thrownStack;
    private boolean dealtDamage;
    public int returningTicks;
    public static ItemTier finalTier;
    private static final DataParameter<Byte> LOYALTY_LEVEL = EntityDataManager.createKey(TridentEntity.class, DataSerializers.BYTE);

    public Spear_Entity(EntityType<? extends Spear_Entity> type, World worldIn) {
        super(type, worldIn);
    }

    public Spear_Entity(World worldIn, LivingEntity thrower, ItemStack thrownStackIn, ItemTier tier) {
        super(ModEntityTypes.SPEAR.get(), thrower, worldIn);
        this.dataManager.set(LOYALTY_LEVEL, (byte)EnchantmentHelper.getLoyaltyModifier(thrownStackIn));
        this.thrownStack = thrownStackIn.copy();
        finalTier = tier;

    }

    public Spear_Entity(World worldIn, double x, double y, double z) {
        super(ModEntityTypes.SPEAR.get(), x, y, z, worldIn);
    }


    public ItemTier getTier(){
        return finalTier;
    }


    protected void registerData() {
        super.registerData();
        this.dataManager.register(LOYALTY_LEVEL, (byte)0);

    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        if (this.timeInGround > 4) {
            this.dealtDamage = true;
        }

        Entity entity = this.getShooter();
        if ((this.dealtDamage || this.getNoClip()) && entity != null) {
            int i = this.dataManager.get(LOYALTY_LEVEL);
            if (i > 0 && !this.shouldReturnToThrower()) {
                if (!this.world.isRemote && this.pickupStatus == AbstractArrowEntity.PickupStatus.ALLOWED) {
                    this.entityDropItem(this.getArrowStack(), 0.1F);
                }

                this.remove();
            } else if (i > 0) {
                this.setNoClip(true);
                Vector3d vector3d = new Vector3d(entity.getPosX() - this.getPosX(), entity.getPosYEye() - this.getPosY(), entity.getPosZ() - this.getPosZ());
                this.setRawPosition(this.getPosX(), this.getPosY() + vector3d.y * 0.015D * (double)i, this.getPosZ());
                if (this.world.isRemote) {
                    this.lastTickPosY = this.getPosY();
                }

                double d0 = 0.05D * (double)i;
                this.setMotion(this.getMotion().scale(0.95D).add(vector3d.normalize().scale(d0)));
                if (this.returningTicks == 0) {
                    this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.returningTicks;
            }
        }

        super.tick();
    }

    private boolean shouldReturnToThrower() {
        Entity entity = this.getShooter();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    protected ItemStack getArrowStack() {
        return this.thrownStack.copy();
    }

    /**
     * Gets the EntityRayTraceResult representing the entity hit
     */
    @Nullable
    protected EntityRayTraceResult rayTraceEntities(Vector3d startVec, Vector3d endVec) {
        return this.dealtDamage ? null : super.rayTraceEntities(startVec, endVec);
    }

    /**
     * Called when the arrow hits an entity
     */
    protected void onEntityHit(EntityRayTraceResult result) {
        Entity entity = result.getEntity();
        float f = 8.0F;
        if (entity instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity;
            f += EnchantmentHelper.getModifierForCreature(this.thrownStack, livingentity.getCreatureAttribute());
        }

        Entity entity1 = this.getShooter();
        DamageSource damagesource = DamageSource.causeTridentDamage(this, (Entity)(entity1 == null ? this : entity1));
        this.dealtDamage = true;
        SoundEvent soundevent = SoundEvents.ITEM_TRIDENT_HIT;
        if (entity.attackEntityFrom(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity)entity;
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.applyThornEnchantments(livingentity1, entity1);
                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity)entity1, livingentity1);
                }

                this.arrowHit(livingentity1);
            }
        }

        this.setMotion(this.getMotion().mul(-0.01D, -0.1D, -0.01D));
        float f1 = 1.0F;
        this.playSound(soundevent, f1, 1.0F);
    }

    /**
     * The sound made when an entity is hit by this projectile
     */
    protected SoundEvent getHitEntitySound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        Entity entity = this.getShooter();
        if (entity == null || entity.getUniqueID() == entityIn.getUniqueID()) {
            super.onCollideWithPlayer(entityIn);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("Spear", 10)) {
            this.thrownStack = ItemStack.read(compound.getCompound("Spear"));
        }

        this.dealtDamage = compound.getBoolean("DealtDamage");
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.put("Spear", this.thrownStack.write(new CompoundNBT()));
        compound.putBoolean("DealtDamage", this.dealtDamage);
    }

    public void func_225516_i_() {
        if (this.pickupStatus != AbstractArrowEntity.PickupStatus.ALLOWED) {
            super.func_225516_i_();
        }

    }

    protected float getWaterDrag() {
        return 0.99F;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }

    /*********************************************************** Networking ********************************************************/

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}


