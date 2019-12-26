package me.zeroeightsix.kami.module.modules.chat;

import me.zeroeightsix.kami.module.Module;

import java.util.Random;

@Module.Info(name = "Kettu Linux Dupe", category = Module.Category.CHAT)
public class KettuLinuxDupe extends Module {

    public void onEnable(){
        if(mc.player != null){
            mc.player.sendChatMessage("I just used the Kettu Linux Dupe and got " + new Random().nextInt(15) + " shulkers! Powered by apollyon\u2122");
        }
    }

}
