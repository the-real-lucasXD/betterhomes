package lucas.betterhomes.mixins.teleport.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.gui.GuiManager;
import lucas.betterhomes.teleport.DynamicLocationData;
import lucas.betterhomes.teleport.TeleportCountdown;
import lucas.betterhomes.teleport.TpaManager;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Objects;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mixin(Commands.class)
public class TpAcceptCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;
  
  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    this.dispatcher.register(literal("tpaccept")
      .requires(_ -> Betterhomes.configs().enableTpa.get())
      .then(argument("player", EntityArgument.player())
        .executes(ctx -> {
          ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
          ArrayList<Pair<String, Boolean>> requests = TpaManager.getSender(
            Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID()
          );
          for (Pair<String, Boolean> request : requests) {
            if (request.getFirst().equals(player.getStringUUID())) {
              if (request.getSecond()) {
                new DynamicLocationData(ctx.getSource().getPlayer()).teleport(player);
                TpaManager.tpaRequests.remove(player.getStringUUID());
                TpaManager.ticks.remove(player.getStringUUID());
              } else if (!TeleportCountdown.inCountdown(ctx.getSource().getPlayer())) {
                new DynamicLocationData(player).teleport(ctx.getSource().getPlayer());
                TpaManager.tpaRequests.remove(player.getStringUUID());
                TpaManager.ticks.remove(player.getStringUUID());
              } else {
                ctx.getSource().sendFailure(Component.literal("You are currently in a teleport!"));
                return 0;
              }
              ctx.getSource().sendSuccess(
                () -> Component.literal("Successfully accepted " + player.getScoreboardName() + "'s TPA request."),
                false
              );
              player.sendSystemMessage(
                Component.literal(ctx.getSource().getTextName() + " accepted your TPA request!")
                  .withColor(TextColor.GREEN),
                false
              );
              return 0;
            }
          }
          ctx.getSource().sendFailure(Component.literal("This player has not sent you an active request!"));
          return 0;
        })
      ).executes(ctx -> {
        ArrayList<Pair<String, Boolean>> requests = TpaManager.getSender(
          Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID()
        );
        if (requests.isEmpty()) {
          ctx.getSource().sendFailure(Component.literal("You don't have any incoming TPA requests!"));
          return 0;
        } else if (requests.size() == 1) {
          dispatcher.execute("tpaccept " + Betterhomes.getPlayer(requests.getFirst().getFirst()).getScoreboardName(),
            ctx.getSource()
          );
          return 0;
        } else {
          GuiManager.display(
            "select-tpa", ctx.getSource().getPlayer(),
            new Pair<>("action", "accept"),
            new Pair<>("options", GuiManager.loop(
              "select-tpa-option", TpaManager::getVars, new ArrayList<>(requests))
            )
          ); return 0;
        }
      })
    );
  }
}