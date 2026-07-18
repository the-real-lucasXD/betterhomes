package lucas.betterhomes.mixins.teleport.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.gui.GuiManager;
import lucas.betterhomes.teleport.TpaManager;
import net.minecraft.commands.*;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Objects;

import static net.minecraft.commands.Commands.literal;

@Mixin(Commands.class)
public class TpAcceptCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    this.dispatcher.register(literal("tpaccept")
      .requires(_ -> Betterhomes.configs().enableTpa.get())
      .executes(ctx -> {
        ArrayList<Pair<String, Boolean>> requests = TpaManager.getSender(
          Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID()
        ); if (requests.isEmpty()) {
          ctx.getSource().sendFailure(Component.literal("You don't have any incoming TPA requests!"));
          return 0;
        } else if (requests.size() == 1) {
          dispatcher.execute("tpaccept " + Betterhomes.getPlayer(requests.getFirst().getFirst()),
            ctx.getSource()
          ); return 0;
        } else {
          GuiManager.display(
            "select-tpa", ctx.getSource().getPlayer(),
            new Pair<>("action", "accept"),
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