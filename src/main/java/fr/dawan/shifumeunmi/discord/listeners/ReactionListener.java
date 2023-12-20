package fr.dawan.shifumeunmi.discord.listeners;

import fr.dawan.shifumeunmi.shifumi.enums.ShifumiAction;
import fr.dawan.shifumeunmi.shifumi.service.GameService;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
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
        Member authorReaction = event.getMember();

        // If the member doesn't exist or it's the bot, we return
        if (!GameService.isMessageGame(event.getMessageIdLong()) || authorReaction == null ||
                authorReaction.getUser().equals(event.getJDA().getSelfUser())) return;

        long idAuthorReaction = authorReaction.getIdLong();

        // If member doesn't play or it's not his message, remove the reaction and return
        if (!GameService.isPlaying(authorReaction.getUser().getIdLong()) ||
                GameService.getGameByIdMember(idAuthorReaction).getMessageId() != event.getMessageIdLong()) {
            event.getReaction().removeReaction(authorReaction.getUser()).queue();
            return;
        }

        try {
            // Get reaction difference to watch if there's more than one reaction
            MessageReaction reactionAdded = event.getReaction();
            // Filter wrong reaction, delete the reaction
            String reactionUnicode = reactionAdded.getEmoji().asUnicode().getAsCodepoints().toUpperCase();
            if (!ShifumiAction.hasReaction(reactionUnicode)) {
                event.getReaction().removeReaction(authorReaction.getUser()).queue();
                return;
            }

            event.getChannel()
                .sendMessage(
                    GameService.startGame(idAuthorReaction,
                        ShifumiAction.getActionFromUnicode(reactionUnicode)).toString()
                ).queue(messageResult -> event.retrieveMessage().queue(messageInteractive ->
                    messageInteractive.delete().queue(v -> InteractiveCommandListener.clean(idAuthorReaction))));
        }
        catch (Exception e) {
            event.getReaction().removeReaction(authorReaction.getUser()).queue();
        }
    }
    //endregion
}
