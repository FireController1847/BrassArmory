package com.milamberBrass.brass_armory.entities.render;

import com.milamberBrass.brass_armory.BrassArmory;
import com.milamberBrass.brass_armory.entities.custom.Spear_Entity;
import com.milamberBrass.brass_armory.entities.model.Spear_Model;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TridentRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class Spear_Entity_Renderer extends EntityRenderer<Spear_Entity> {
    public static final ResourceLocation RES_WOOD_SPEAR = new ResourceLocation(BrassArmory.MOD_ID,"textures/item/wood/wood_spear.png");
    public static final ResourceLocation RES_STONE_SPEAR = new ResourceLocation(BrassArmory.MOD_ID,"textures/item/stone/stone_spear.png");
    public static final ResourceLocation RES_GOLD_SPEAR = new ResourceLocation(BrassArmory.MOD_ID,"textures/item/gold/gold_spear.png");
    public static final ResourceLocation RES_IRON_SPEAR = new ResourceLocation(BrassArmory.MOD_ID,"textures/item/iron/iron_spear.png");
    public static final ResourceLocation RES_DIAMOND_SPEAR = new ResourceLocation(BrassArmory.MOD_ID,"textures/item/diamond/diamond_spear.png");
    public static final ResourceLocation RES_NETHERITE_SPEAR = new ResourceLocation(BrassArmory.MOD_ID,"textures/item/netherite/netherite_spear.png");

    public static Spear_Model spear_model;

    public Spear_Entity_Renderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }


    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(Spear_Entity entity) {
        ItemTier tier = entity.finalTier;
        switch (tier) {
            default:
            case WOOD:
                return RES_WOOD_SPEAR;
            case STONE:
                return RES_STONE_SPEAR;
            case GOLD:
                return RES_GOLD_SPEAR;
            case IRON:
                return RES_IRON_SPEAR;
            case DIAMOND:
                return RES_DIAMOND_SPEAR;
            case NETHERITE:
                return RES_NETHERITE_SPEAR;

        }
    }
}
