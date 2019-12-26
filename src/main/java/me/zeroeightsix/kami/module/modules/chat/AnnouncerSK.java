package me.zeroeightsix.kami.module.modules.chat;

import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

import java.text.DecimalFormat;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import me.zeroeightsix.kami.module.modules.ClickGUI;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.item.ItemBlock;

@Module.Info(name = "AnnouncerSK", category = Module.Category.HIDDEN, description = "")
public class AnnouncerSK extends Module {

	public static int blockBrokeDelay = 0;
	static int blocksWalkerCounter;
	static int dropItemDelay = 0;
	static int itemPickUpDelay = 0;
	static int blockPlacedDelay = 0;
	static int chatDelay = 0;
	static int commandDelay = 0;
	static int pauseDelay = 0;
	static int inventoryDelay = 0;
	static int playerListDelay = 0;
	static int perspectivesDelay = 0;
	static int crouchedDelay = 0;
	static int jumpDelay = 0;
	static int attackDelay = 0;
	static int eattingDelay = 0;

	static long lastPositionUpdate;
	static double lastPositionX;
	static double lastPositionY;
	static double lastPositionZ;
	private static double speed;
	String heldItem = "";
	
	private long currentMS = 0L;
	private long lastMS = -1L;
	
	public boolean hasDelayRun(long time) {
		return (currentMS - lastMS) >= time;
	}
	
	private Setting<Boolean> walk = register(Settings.b("Walk", true));
	private Setting<Boolean> jump = register(Settings.b("Jump", false));
	private Setting<Boolean> eat = register(Settings.b("Eat", false));
	private Setting<Boolean> inv = register(Settings.b("Inventory", false));
	private Setting<Boolean> place = register(Settings.b("Block Place", false));
	public void onUpdate() {
		heldItem = mc.player.getHeldItemMainhand().getDisplayName();
		if (walk.getValue()) {
			if (this.lastPositionUpdate + 20000L < System.currentTimeMillis()) {

				double d0 = lastPositionX - mc.player.lastTickPosX;
				double d2 = lastPositionY - mc.player.lastTickPosY;
				double d3 = lastPositionZ - mc.player.lastTickPosZ;

				speed = Math.sqrt(d0 * d0 + d2 * d2 + d3 * d3);

				if (speed <= 0 || speed > 5000) {
				} else {

					mc.player.sendChatMessage("Prešiel som " + new DecimalFormat("#").format(speed) + " blockov!");
					this.lastPositionUpdate = System.currentTimeMillis();
					lastPositionX = mc.player.lastTickPosX;
					lastPositionY = mc.player.lastTickPosY;
					lastPositionZ = mc.player.lastTickPosZ;
				}
			}
		}
			
		if (jump.getValue() && mc.gameSettings.keyBindJump.isKeyDown()) {
			currentMS = System.nanoTime() / 1000000;
			if(hasDelayRun((long)(1000 / 0.05)))
			{
			mc.player.sendChatMessage("Práve som skoèil!");
			lastMS = System.nanoTime() / 1000000;
			}
		}
		
		if (eat.getValue()) {
			if (mc.player.isHandActive()) {
				if (mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || mc.player.getHeldItemMainhand().getItem() instanceof ItemAppleGold) {
					currentMS = System.nanoTime() / 1000000;
					if(hasDelayRun((long)(1000 / 0.2)))
					{
				     mc.player.sendChatMessage("Práve som zjedol " + mc.player.getHeldItemMainhand().getDisplayName() + "!");
					lastMS = System.nanoTime() / 1000000;
					}
				}
			}
		}
		
		if (inv.getValue()) {
			if (mc.gameSettings.keyBindInventory.isKeyDown()) {
				currentMS = System.nanoTime() / 1000000;
				if(hasDelayRun((long)(1000 / 0.2)))
				{
					mc.player.sendChatMessage("Práve som otvoril inventár!");
				lastMS = System.nanoTime() / 1000000;
				}
				
			}
		}
		
	}
		 @EventHandler
		    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
			if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock) {
				currentMS = System.nanoTime() / 1000000;
				if(hasDelayRun((long)(1000 / 0.1))) {				
					if (place.getValue()) {
						mc.player.sendChatMessage("Práve som položil " + mc.player.getHeldItemMainhand().getDisplayName() + "!");
						lastMS = System.nanoTime() / 1000000;
					}
				}
			}
		});
  
}