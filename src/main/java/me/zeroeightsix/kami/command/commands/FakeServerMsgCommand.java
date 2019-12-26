package me.zeroeightsix.kami.command.commands;

import me.zeroeightsix.kami.command.syntax.ChunkBuilder;
import me.zeroeightsix.kami.command.Command;


public class FakeServerMsgCommand extends Command{
	
	   public FakeServerMsgCommand() {
	        super("servermsg", new ChunkBuilder().append("message").build());
	    }

    
    public void call(String[] args) {
        if (args.length == 0) {
            Command.sendChatMessage("you need to type the message retard");
            return;
        }
     
    
    {
    	sendRawChatMessage("\u00A7e" + "[SERVER] " + args[0]);
    }
    
    }
}
