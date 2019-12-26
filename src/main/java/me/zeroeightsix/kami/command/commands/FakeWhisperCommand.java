package me.zeroeightsix.kami.command.commands;

import me.zeroeightsix.kami.command.syntax.ChunkBuilder;
import me.zeroeightsix.kami.command.Command;


public class FakeWhisperCommand extends Command{
	
	   public FakeWhisperCommand() {
	        super("fakewhisper", new ChunkBuilder()
	        		.append("name")
	        		.append("message")	        		
	        		.build()
	        	  );
    }

   
    public void call(String[] args) {
      if (args.length == 0) {
            Command.sendChatMessage("missing arguments");
            return;
        }
   
    
    {
    	sendRawChatMessage("\u00A7d" + args[0] + " whispers: " + args[1]);
    }
    
   }
}
