package lucas.betterhomes.mixins.teleport.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.gui.GuiManager;
import lucas.betterhomes.teleport.TeleportCountdown;
import lucas.betterhomes.teleport.TpaManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mixin(Commands.class)
public class TpaCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    this.dispatcher.register(literal("tpa")
      .requires(_ -> Betterhomes.configs().enableTpa.get())
      .then(argument("player", EntityArgument.entity())
        .executes(ctx -> {
          ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
          if (TeleportCountdown.inCountdown(ctx.getSource().getPlayer())) {
            ctx.getSource().sendFailure(Component.literal("You are already in a teleport!"));
            return 0;
          } TpaManager.tpaRequests.put(
            Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID(),
            new Pair<>(player.getStringUUID(), true)
          ); ctx.getSource().sendSuccess(
            () -> Component.literal("Successfully sent a teleport request to ")
              .append(Component.literal(player.getStringUUID()).withStyle(ChatFormatting.BOLD))
              .append("."),
            false
          ); GuiManager.display(
            "tpa-request", player,
            new Pair<>("requester", ctx.getSource().getPlayer().getScoreboardName()),
            new Pair<>("request", "to teleport to you")
          ); return 0;
        })
      )
    );
  }
}