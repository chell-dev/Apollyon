package me.zeroeightsix.kami;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import club.minnced.discord.rpc.DiscordEventHandlers;
import net.minecraftforge.fml.common.FMLLog;
import club.minnced.discord.rpc.DiscordRichPresence;
import club.minnced.discord.rpc.DiscordRPC;

/***
 * @author snowmii
 * Updated by S-B99 on 15/12/19
 * modified by FINZ0 or something
 */
public class DiscordPresence {
    private static final String APP_ID = "624553665713274880";
    private static final DiscordRPC rpc;
    public static DiscordRichPresence presence;
    private static boolean hasStarted;
    public static final Minecraft mc = Minecraft.getMinecraft();
    private static String details;
    private static String state;
    private static int players;
    private static int maxPlayers;
    private static ServerData svr;
    private static String[] popInfo;
    private static int players2;
    private static int maxPlayers2;

    public static void start() {
        FMLLog.log.info("Starting Discord RPC");
        if (DiscordPresence.hasStarted) {
            return;
        }
        DiscordPresence.hasStarted = true;
        final DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.disconnected = ((var1, var2) -> System.out.println("Discord RPC disconnected, var1: " + String.valueOf(var1) + ", var2: " + var2));
        DiscordPresence.rpc.Discord_Initialize(APP_ID, handlers, true, "");
        DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;
        DiscordPresence.presence.details = "Main Menu";
        DiscordPresence.presence.state = "discord.gg/a6mZKQT";
        DiscordPresence.presence.largeImageKey = "logo";
        DiscordPresence.presence.largeImageText = "lol?";

        DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DiscordPresence.rpc.Discord_RunCallbacks();
                    details = "";
                    state = "";
                    players = 0;
                    maxPlayers = 0;
                    if (mc.isIntegratedServerRunning()) {
                        details = "Singleplayer";
                    } else if (mc.getCurrentServerData() != null) {
                        svr = mc.getCurrentServerData();
                        if (!svr.serverIP.equals("")) {
                            details = svr.serverIP;
                                state = "Biome: " + mc.player.getEntityWorld().getBiome(mc.player.getPosition()).getBiomeName();
                                if (svr.populationInfo != null) {
                                    popInfo = svr.populationInfo.split("/");
                                    if (popInfo.length > 2) {
                                        players2 = Integer.parseInt(popInfo[0]);
                                        maxPlayers2 = Integer.parseInt(popInfo[1]);
                                    }
                                }
                        }
                    } else {
                        details = "Main Menu";
                        state = "discord.gg/a6mZKQT";
                    }
                    if (!details.equals(DiscordPresence.presence.details) || !state.equals(DiscordPresence.presence.state)) {
                        DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;
                    }
                    DiscordPresence.presence.details = details;
                    DiscordPresence.presence.state = state;
                    DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
                try {
                    Thread.sleep(5000L);
                }
                catch (InterruptedException e3) {
                    e3.printStackTrace();
                }
            }
            return;
        }, "Discord-RPC-Callback-Handler").start();
        FMLLog.log.info("Discord RPC initialised succesfully");
    }

    private static /* synthetic */ void lambdastart1() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                DiscordPresence.rpc.Discord_RunCallbacks();
                String details = "";
                String state = "";
                int players = 0;
                int maxPlayers = 0;
                if (mc.isIntegratedServerRunning()) {
                    details = "Singleplayer";
                } else if (mc.getCurrentServerData() != null) {
                    final ServerData svr = mc.getCurrentServerData();
                    if (!svr.serverIP.equals("")) {
                        details = svr.serverIP;
                        state = "Biome: " + mc.player.getEntityWorld().getBiome(mc.player.getPosition()).getBiomeName();
                        if (svr.populationInfo != null) {
                            final String[] popInfo = svr.populationInfo.split("/");
                            if (popInfo.length > 2) {
                                players = Integer.parseInt(popInfo[0]);
                                maxPlayers = Integer.parseInt(popInfo[1]);
                            }
                        }
                    }
                } else {
                    details = "Main Menu";
                    state = "discord.gg/a6mZKQT";
                }
                if (!details.equals(DiscordPresence.presence.details) || !state.equals(DiscordPresence.presence.state)) {
                    DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;
                }
                DiscordPresence.presence.details = details;
                DiscordPresence.presence.state = state;
                DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException e3) {
                e3.printStackTrace();
            }
        }
    }

    private static /* synthetic */ void lambdastart0(final int var1, final String var2) {
        System.out.println("Discord RPC disconnected, var1: " + var1 + ", var2: " + var2);
    }

    static {
        rpc = DiscordRPC.INSTANCE;
        DiscordPresence.presence = new DiscordRichPresence();
        DiscordPresence.hasStarted = false;
    }
}