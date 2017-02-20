package eu.babkin.vk.bot.plugins;

import eu.babkin.vk.bot.BotCommands;
import eu.babkin.vk.bot.messages.IncomingMessage;
import eu.babkin.vk.bot.event.updates.UpdateListener;
import eu.babkin.vk.bot.messages.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class StickerPlugin implements UpdateListener<IncomingMessage> {

    private static int[] stickers = {66, 65, 64, 63}; //TODO: load from properties?

    private final MessageService messageService;
    private final Random rnd;

    @Autowired
    public StickerPlugin(MessageService messageService) {
        this.messageService = messageService;
        this.rnd = new Random();
    }

    @Override
    public void onUpdate(IncomingMessage incomingMessage) {
        if (BotCommands.STICKER.equals(incomingMessage.getMessage())) {
            int sticker = stickers[rnd.nextInt(stickers.length)];
            messageService.sendChatSticker(sticker, incomingMessage.getPeerId());
        }
    }
}
