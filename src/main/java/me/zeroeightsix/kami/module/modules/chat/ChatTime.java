package me.zeroeightsix.kami.module.modules.chat;

import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraft.util.text.TextComponentString;
import me.zeroeightsix.kami.module.Module;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

// Made by FINZ0
@Module.Info(name = "ChatTimeStamps", category = Module.Category.CHAT)
public class ChatTime extends Module {

    private Setting<Mode> mode = register(Settings.e("Mode", Mode.TwentyFour));

    String date;

   @SubscribeEvent
   public void onChat(ClientChatReceivedEvent event){
            if(mode.getValue().equals(Mode.TwentyFour)) date = new SimpleDateFormat("k:mm").format(new Date());
            if(mode.getValue().equals(Mode.Twelve)) date = new SimpleDateFormat("h:mm a").format(new Date());
            TextComponentString newMsg = new TextComponentString("\2477<" + date + ">\247r ");
            event.setMessage(newMsg.appendSibling(event.getMessage()));
  }

   public void onEnable(){
       MinecraftForge.EVENT_BUS.register(this);
   }

   public void onDisable(){
       MinecraftForge.EVENT_BUS.unregister(this);
   }

    private enum Mode{
        TwentyFour, Twelve
    }
}