package com.github.yukkuritaku.modernwarpmenu.listeners;

import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;
import com.github.yukkuritaku.modernwarpmenu.state.GameState;
import io.netty.channel.ChannelHandler;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.azureaaron.hmapi.data.error.ErrorReason;
import net.azureaaron.hmapi.data.server.Environment;
import net.azureaaron.hmapi.events.HypixelPacketEvents;
import net.azureaaron.hmapi.network.HypixelNetworking;
import net.azureaaron.hmapi.network.packet.s2c.ErrorS2CPacket;
import net.azureaaron.hmapi.network.packet.s2c.HelloS2CPacket;
import net.azureaaron.hmapi.network.packet.s2c.HypixelS2CPacket;
import net.azureaaron.hmapi.network.packet.v1.s2c.LocationUpdateS2CPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Util;
import net.minecraft.world.scores.Scoreboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@ChannelHandler.Sharable
public class SkyBlockJoinListener {
    private static final String SERVER_BRAND_START = "Hypixel BungeeCord";
    private static final int SCOREBOARD_CHECK_TIME_OUT = 5000;

    private static final Logger LOGGER = LogManager.getLogger();
    private boolean serverBrandChecked;
    private boolean onHypixel;
    private boolean scoreboardChecked;
    private long lastWorldSwitchTime;

    public SkyBlockJoinListener(){
    }

    public void registerEvents(){
        HypixelPacketEvents.HELLO.register(packet -> {
            switch (packet){
                case HelloS2CPacket(Environment ignored) -> {
                    if (SettingsManager.get().general.useHypixelAPI) {
                        this.onHypixel = true;
                        LOGGER.info("Player joined Hypixel. (hm-api)");
                    }
                }
                case ErrorS2CPacket(CustomPacketPayload.Type<HypixelS2CPacket> id, ErrorReason reason) -> {
                    LOGGER.error("Hypixel Packet {} returned with error", id.id());
                }
                default -> {}
            }
        });
        HypixelPacketEvents.LOCATION_UPDATE.register(packet -> {
            switch (packet){
                case LocationUpdateS2CPacket(String serverName,
                                             Optional<String> serverType,
                                             Optional<String> lobbyName,
                                             Optional<String> mode,
                                             Optional<String> map
                ) -> {
                    if (SettingsManager.get().general.useHypixelAPI) {
                        if (serverType.isPresent()) {
                            boolean isSkyBlock = serverType.get().equals("SKYBLOCK");
                            if (isSkyBlock) {
                                LOGGER.info("Player joined SkyBlock. (hm-api)");
                            } else {
                                LOGGER.info("Player left SkyBlock. (hm-api)");
                            }
                            GameState.setOnSkyBlock(isSkyBlock);
                        }
                        this.onHypixel = true;
                    }
                }
                case ErrorS2CPacket(CustomPacketPayload.Type<HypixelS2CPacket> id, ErrorReason reason) -> {
                    LOGGER.error("Hypixel Packet {} returned with error", id.id());
                }
                default -> {}
            }
        });
        HypixelNetworking.registerToEvents(Util.make(new Object2IntOpenHashMap<>(), map -> {
            map.put(LocationUpdateS2CPacket.ID, 1);
        }));
        /*HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
        HypixelModAPI.getInstance().createHandler(ClientboundHelloPacket.class, packet -> {
            if (SettingsManager.get().general.useHypixelAPI) {
                this.onHypixel = true;
                LOGGER.info("Player joined Hypixel.");
            }
        });
        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, packet -> {

            if (SettingsManager.get().general.useHypixelAPI) {
                if (packet.getServerType().isPresent()) {
                    boolean isSkyBlock = packet.getServerType().get() == GameType.SKYBLOCK;
                    if (isSkyBlock) {
                        LOGGER.info("Player joined SkyBlock.");
                    } else {
                        LOGGER.info("Player left SkyBlock.");
                    }
                    GameState.setOnSkyBlock(isSkyBlock);
                }
                this.onHypixel = true;
            }
        });*/
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (this.onHypixel){
                this.serverBrandChecked = false;
                this.onHypixel = false;
                GameState.setOnSkyBlock(false);
                LOGGER.info("Disconnected from Hypixel. (ClientPlayConnectionEvents)");
            }
        });
        ClientLoginConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (this.onHypixel){
                this.serverBrandChecked = false;
                this.onHypixel = false;
                GameState.setOnSkyBlock(false);
                LOGGER.info("Disconnected from Hypixel. (ClientLoginConnectionEvents)");
            }
        });
        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, level) -> {

            if (!SettingsManager.get().general.useHypixelAPI) {
                this.lastWorldSwitchTime = Util.getMillis();
                this.serverBrandChecked = false;
                this.scoreboardChecked = false;
                GameState.setOnSkyBlock(false);
            }
        });
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!SettingsManager.get().general.useHypixelAPI) {
                if (!this.serverBrandChecked || this.onHypixel && !this.scoreboardChecked) {
                    LocalPlayer player = Minecraft.getInstance().player;
                    if (player == null) return;
                    String serverBrand = player.connection.serverBrand();
                    if (!this.serverBrandChecked) {
                        this.onHypixel = serverBrand != null && serverBrand.startsWith(SERVER_BRAND_START);
                        if (serverBrand != null) {
                            this.serverBrandChecked = true;
                        } else {
                            LOGGER.warn("Server brand is null, retrying...");
                        }
                        if (SettingsManager.get().debug.debugModeEnabled) {
                            LOGGER.info("Server Brand: {}", serverBrand);
                        }
                        if (this.onHypixel) {
                            LOGGER.info("Player joined Hypixel.");
                        }
                    }
                    if (this.onHypixel && !this.scoreboardChecked) {
                        Scoreboard scoreboard = player.level().getScoreboard();
                        boolean newSkyBlockState = scoreboard.getObjective("SBScoreboard") != null;
                        if (newSkyBlockState != GameState.isOnSkyBlock()) {
                            if (newSkyBlockState) {
                                LOGGER.info("Player joined SkyBlock.");
                            } else {
                                LOGGER.info("Player left SkyBlock.");
                            }
                            GameState.setOnSkyBlock(newSkyBlockState);
                            this.scoreboardChecked = true;
                        }
                        if (Util.getMillis() - this.lastWorldSwitchTime > SCOREBOARD_CHECK_TIME_OUT) {
                            LOGGER.warn("Scoreboard Check Time out.");
                            this.scoreboardChecked = true;
                        }
                    }
                }
            }
        });
    }
}
