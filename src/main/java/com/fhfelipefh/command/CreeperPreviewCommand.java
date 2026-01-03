package com.fhfelipefh.command;

import com.fhfelipefh.preview.CreeperPreviewManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public final class CreeperPreviewCommand {
    private CreeperPreviewCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("creeperpreview")
                        .executes(context -> toggle(context.getSource()))
        );
    }

    private static int toggle(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        boolean enabled = CreeperPreviewManager.toggle(player);
        source.sendFeedback(
                () -> Text.literal(enabled
                        ? "Preview ligado: creepers visiveis mostram a area da explosao."
                        : "Preview desligado."),
                false
        );
        return 1;
    }
}
