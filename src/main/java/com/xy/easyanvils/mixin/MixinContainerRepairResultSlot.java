package com.xy.easyanvils.mixin;

import com.xy.easyanvils.config.EasyAnvilsConfig;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets a <em>free</em> (0-level) anvil result be taken while every other result still needs its
 * levels.
 *
 * <p>Vanilla's output slot ({@code ContainerRepair$2}) answers
 * {@code (creative || level >= cost) && cost > 0 && hasStack}, so a result that costs 0 levels can
 * never be removed. The original Easy Anvils mod overrides this (its {@code m_6560_} uses
 * {@code cost >= 0}), which is what makes free renames usable. This writes that rule out in full.
 *
 * <p>Up to 1.1.0 this was a {@code @ModifyConstant(intValue = 0, expandZeroConditions =
 * GREATER_THAN_ZERO)} returning -1. Mixin's expanded zero match also takes the method's
 * {@code iconst_0}, which is the {@code return false}, so every refusal returned -1 (true) and a
 * level 0 player could take any result for free (GitHub issue #1, the green cost at level 0).
 *
 * <p>The cost is read from the player's open anvil, the container this slot belongs to whenever a
 * click or the screen asks. With {@code customAnvilCosts} off the vanilla method runs untouched.
 */
@Mixin(targets = "net.minecraft.inventory.ContainerRepair$2")
public abstract class MixinContainerRepairResultSlot {

    @Inject(method = "canTakeStack", at = @At("HEAD"), cancellable = true)
    private void easyanvils$canTakeStack(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (!EasyAnvilsConfig.customAnvilCosts || !(player.openContainer instanceof ContainerRepair)) {
            return;
        }
        int cost = ((ContainerRepair) player.openContainer).maximumCost;
        cir.setReturnValue((player.capabilities.isCreativeMode || player.experienceLevel >= cost)
                && cost >= 0
                && ((Slot) (Object) this).getHasStack());
    }
}
