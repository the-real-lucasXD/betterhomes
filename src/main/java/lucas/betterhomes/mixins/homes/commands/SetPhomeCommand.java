package lucas.betterhomes.mixins.homes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.datafixers.util.Pair;
import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.gui.GuiManager;
import lucas.betterhomes.storage.TeleportManager;
import lucas.betterhomes.homes.Phome;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mixin(Commands.class)
public class SetPhomeCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;
  
  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    this.dispatcher.register(literal("setphome")
      .requires(source -> Betterhomes.configs().enablePhomes.get()
        || source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
      .then(argument("name", StringArgumentType.word())
        .executes(ctx -> {
          ConcurrentHashMap<String, Phome> homes = TeleportManager.INSTANCE.phomes;
          String name = StringArgumentType.getString(ctx, "name");
          
          if (homes.containsKey(name)) {
            GuiManager.display("override-home", ctx.getSource().getPlayer(), new Pair<>("name", name), new Pair<>("type", "phome"));
            return 0;
          }
          
          homes.put(name, new Phome(ctx.getSource().getPlayer(), name));
          ctx.getSource().sendSuccess(
            () -> Component.literal("Successfully created a new phome called ")
              .append(Component.literal(name).withStyle(ChatFormatting.BOLD))
              .append("."),
            false
          ); return 0;
        })
      )
    );
  }
}