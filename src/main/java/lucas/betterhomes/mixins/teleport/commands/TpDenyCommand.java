package lucas.betterhomes.mixins.teleport.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.gui.GuiManager;
import lucas.betterhomes.teleport.TpaManager;
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

import java.util.ArrayList;
import java.util.Objects;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mixin(Commands.class)
public class TpDenyCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;
  
  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    this.dispatcher.register(literal("tpdeny")
      .requires(_ -> Betterhomes.configs().enableTpa.get())
      .then(argument("player", EntityArgument.player())
        .executes(ctx -> {
          ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
          ArrayList<Pair<String, Boolean>> requests = TpaManager.getSender(
            Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID()
          ); for (Pair<String, Boolean> request : requests) {
            if (request.getFirst().equals(player.getStringUUID())) {
              player.sendSystemMessage(
                Component.literal(ctx.getSource().getPlayer().getScoreboardName() + " has denied your TPA request!"),
                false
              ); TpaManager.tpaRequests.remove(player.getStringUUID());
              return 0;
            }
          } ctx.getSource().sendFailure(Component.literal("This player has not sent you an active request!"));
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
          dispatcher.execute("tpdeny " + Betterhomes.getPlayer(requests.getFirst().getFirst()).getScoreboardName(),
            ctx.getSource()
          );
          return 0;
        } else {
          GuiManager.display(
            "select-tpa", ctx.getSource().getPlayer(),
            new Pair<>("action", "deny"),
            new Pair<>("options", GuiManager.loop(
              "select-tpa-option", TpaManager::getVars, new ArrayList<>(requests))
            )
          );
          return 0;
        }
      })
    );
  }
}