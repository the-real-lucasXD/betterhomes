package lucas.betterhomes.mixins;

import com.mojang.brigadier.CommandDispatcher;
import lucas.betterhomes.teleport.LocationData;
import lucas.betterhomes.teleport.TeleportCountdown;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static net.minecraft.commands.Commands.literal;

@Mixin(Commands.class)
public class TeleportCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;
  
  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    this.dispatcher.register(literal("tpaToMyself")
      .executes(ctx -> {
        try {
          new TeleportCountdown(
            new LocationData(Objects.requireNonNull(ctx.getSource().getPlayer())),
            Objects.requireNonNull(ctx.getSource().getPlayer())
          );
        } catch (Exception e) {
          throw new RuntimeException(e);
        } return 0;
      })
    );
  }
}