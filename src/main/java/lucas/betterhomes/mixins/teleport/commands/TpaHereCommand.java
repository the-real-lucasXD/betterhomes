package lucas.betterhomes.mixins.teleport.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.gui.GuiManager;
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
public class TpaHereCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    this.dispatcher.register(literal("tpahere")
      .requires(_ -> Betterhomes.configs().enableTpa.get())
      .then(argument("player", EntityArgument.entity())
        .executes(ctx -> {
          ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
          if (Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID().equals(player.getStringUUID())) {
            ctx.getSource().sendFailure(Component.literal("You cannot send a teleport request to yourself!"));
            return 0;
          } if (TpaManager.tpaRequests.containsKey(ctx.getSource().getPlayer().getStringUUID())) {
            ctx.getSource().sendFailure(Component.literal("You already have a teleport request!"));
            return 0;
          } Pair<String, Boolean> toAdd = new Pair<>(player.getStringUUID(), false);
          TpaManager.ticks.put(Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID(), Betterhomes.configs().tpaExpiryTicks.get());
          TpaManager.tpaRequests.put(Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID(), toAdd);
          ctx.getSource().sendSuccess(
            () -> Component.literal("Successfully sent a teleport request to ")
              .append(Component.literal(player.getScoreboardName()).withStyle(ChatFormatting.BOLD))
              .append("."),
            false
          ); GuiManager.display(
            "tpa-request", player,
            new Pair<>("requester", ctx.getSource().getPlayer().getScoreboardName()),
            new Pair<>("request", "that you teleport to them")
          ); return 0;
        })
      )
    );
  }
}