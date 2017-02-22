package eu.babkin.vk.bot.plugins;

import eu.babkin.vk.bot.BotCommands;
import eu.babkin.vk.bot.messages.IncomingMessage;
import eu.babkin.vk.bot.event.updates.UpdateListener;
import eu.babkin.vk.bot.messages.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelpPlugin implements UpdateListener<IncomingMessage> {

    private final MessageService messageService;

    @Autowired
    public HelpPlugin(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void onUpdate(IncomingMessage incomingMessage) {
        if (BotCommands.HELP.equals(incomingMessage.getMessage())) {
            messageService.sendChatMessage("TODO: add --help", incomingMessage.getPeerId());
        }
    }
}
