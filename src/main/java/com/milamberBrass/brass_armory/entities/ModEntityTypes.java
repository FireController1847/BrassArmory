package com.milamberBrass.brass_armory.entities;

import com.milamberBrass.brass_armory.BrassArmory;
import com.milamberBrass.brass_armory.entities.custom.BAArrowEntity;
import com.milamberBrass.brass_armory.entities.custom.Spear_Entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES
            = DeferredRegister.create(ForgeRegistries.ENTITIES, BrassArmory.MOD_ID);


    public static final RegistryObject<EntityType<Spear_Entity>> SPEAR =
            ENTITY_TYPES.register("wood_spear", () -> EntityType.Builder
                    .<Spear_Entity>create(Spear_Entity::new, EntityClassification.MISC).size(1.0f, 2.0f)
                    .trackingRange(64).updateInterval(20)
                    .build(new ResourceLocation(BrassArmory.MOD_ID, "item/wood_spear").toString()));
    
    public static final RegistryObject<EntityType<BAArrowEntity>> BA_ARROW = registerEntityType("ba_arrow", EntityType.Builder.<BAArrowEntity>create(BAArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).trackingRange(4).updateInterval(20));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    /**
     * Helper method for registering all EntityTypes
     */
    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(String registryName, EntityType.Builder<T> builder) {
    	return ENTITY_TYPES.register(registryName, () -> builder.build(registryName));
    }
}

