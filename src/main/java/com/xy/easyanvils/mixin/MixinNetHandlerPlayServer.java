package com.xy.easyanvils.mixin;

import com.xy.easyanvils.config.EasyAnvilsConfig;

import net.minecraft.network.NetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Lets the server accept the 50-character anvil names the screen allows. Vanilla's "MC|ItemName"
 * handler ignores any name longer than 35 characters, so without this a long name typed in the
 * anvil never reached the item. 35 is the only such constant in processCustomPayload.
 */
@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {

    // require = 0: a mod that already rewrote this check keeps its own limit instead of a crash.
    @ModifyConstant(method = "processCustomPayload", constant = @Constant(intValue = 35), require = 0)
    private int easyanvils$itemNameLength(int vanilla) {
        return EasyAnvilsConfig.longerItemNames ? 50 : vanilla;
    }
}
