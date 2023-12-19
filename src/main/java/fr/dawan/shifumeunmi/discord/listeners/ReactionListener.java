package fr.dawan.shifumeunmi.discord.listeners;

import fr.dawan.shifumeunmi.shifumi.enums.ShifumiAction;
import fr.dawan.shifumeunmi.shifumi.service.GameService;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

@Log
public class ReactionListener extends ListenerAdapter {
    //region OnMessageReactionAdd Event
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        //log.info("onMessageReactionAdd");
        User authorReaction = event.getMember().getUser();
        if (!GameService.isPlaying(authorReaction)) return;

        // Is the message retrieve equals to the message
        // (Indirectly, it will check if the author that reacted is the author that
        // ask the command)
        long idMessageInteractive = GameService.getIdMessage(authorReaction);
        if (idMessageInteractive != event.getMessageIdLong()) return;


        // Retrieve the message with the author name
        //Message message = event.retrieveMessage().complete();


        // Fetch the message to know the reaction added
        // Message message = event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete();

        // Update reaction list link to this user
        // For the next time, we can see if there's a difference
        // GameService.updateMessage(authorReaction, message);

        try {
            // Get reaction difference to watch if there's more than one reaction
            /*MessageReaction reactionAdded = getFirstDifferenceReaction(interactiveMessage.getReactions(),
                message.getReactions());*/
            MessageReaction reactionAdded = event.getReaction();
            // Filter wrong reaction, delete the reaction
            String reactionUnicode = reactionAdded.getEmoji().asUnicode().getAsCodepoints().toUpperCase();
            if (!ShifumiAction.hasReaction(reactionUnicode)) {
                event.retrieveMessage().queue(message ->
                    message.removeReaction(reactionAdded.getEmoji()).queue());
                return;
            }

            event.getChannel()
                .sendMessage(
                    GameService.startGame(authorReaction,
                        ShifumiAction.getActionFromUnicode(reactionUnicode)).toString()
                ).queue(messageResult -> event.retrieveMessage().queue(messageInteractive ->
                    messageInteractive.delete().queue()));
        }
        catch (NoSuchElementException e) {
            event.getChannel().sendMessage(e.getMessage()).queue();
        }
    }
    //endregion

    //region Message Reaction Add Methods
/*    private MessageReaction getFirstDifferenceReaction(List<MessageReaction> previousMessages,
                                                       List<MessageReaction> newMessages)
        throws NoSuchElementException {
        Optional<MessageReaction> messageReaction = newMessages.stream()
            .filter(newMessage -> !previousMessages.contains(newMessage) ||
            previousMessages.get(previousMessages.indexOf(newMessage)).getCount() != newMessage.getCount())
            .findFirst();
        return messageReaction.orElseThrow();
    }*/
    //endregion
}
