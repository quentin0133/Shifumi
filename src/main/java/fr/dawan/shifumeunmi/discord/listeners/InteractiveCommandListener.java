package fr.dawan.shifumeunmi.discord.listeners;

import fr.dawan.shifumeunmi.discord.ShifumiBot;
import fr.dawan.shifumeunmi.shifumi.entities.Game;
import fr.dawan.shifumeunmi.shifumi.enums.BotCommand;
import fr.dawan.shifumeunmi.shifumi.enums.ShifumiAction;
import fr.dawan.shifumeunmi.shifumi.service.GameService;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log
public class InteractiveCommandListener extends ListenerAdapter {
    private final Map<User, Message> messageMapByUser = new HashMap<>();

    //region Slash Command Methods
    private static String listGameCommand(@NotNull SlashCommandInteractionEvent event, List<Game> listGame) {
        if (listGame.isEmpty())
            return "Aucune partie n'a été lancé avec moi :sob:";

        // Exemple :
        // Game n°5 :
        // Affichage de la partie n°5
        // --------
        // Game n°6 :
        // Affichage de la partie n°6
        // --------
        return IntStream.range(0, listGame.size()).mapToObj(i ->
                "Game n°%d :%n%s%n--------".formatted(i, listGame.get(i)))
            .collect(Collectors.joining("%n"));
    }
    //endregion

    //region OnSlashCommandInteraction Event
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        User user = event.getUser();

        if (guild == null) return;

        // log.info("onSlashCommandInteraction");

        // Check if the user use the command in the channel for the bot
        //if (!ShifumiBot.getMessageChannelMapByGuild().get(guild).equals(event.getChannel())) {
        if (!event.getChannel().getName().equals(ShifumiBot.getNameChannel())) {
            // Check if the channel exist otherwise he will create a new one
            // For linking the channel to the user
            if (guild.getTextChannels().stream().noneMatch(channel ->
                ShifumiBot.getNameChannel().equals(channel.getName())))
                ShifumiBot.createChannel(guild).queue(channel -> {
                    channel.sendMessage(GameService.helpMessage()).queue( v -> wrongChannelFeedback(event, channel));
                });
            else
                guild.getTextChannelsByName(ShifumiBot.getNameChannel(), false).stream().findFirst()
                    .ifPresent(channel -> wrongChannelFeedback(event, channel));
            return;
        }

        if (GameService.isPlaying(user)) {
            guild.getTextChannelsByName(ShifumiBot.getNameChannel(), false).stream().findFirst()
                .ifPresent(channel ->
                    event.reply("Vous avez déjà une partie en cours : https://discord.com/channels/%s/%s/%d"
                    .formatted(guild.getId(), channel.getId(),
                        GameService.getIdMessage(user))).setEphemeral(true).queue());
            return;
        }

        try {
            String msg;
            Consumer<InteractionHook> msgPostTreatment = hook -> {};
            BotCommand action = BotCommand.valueOf(event.getName().toUpperCase());

            if (messageMapByUser.containsKey(user) && action != BotCommand.PLAY)
                messageMapByUser.get(user).delete().queue();

            switch (action) {
                case HELP -> msg = GameService.helpMessage();
                case PLAY -> {
                    msg = "Jouons !%nAjoute une réaction pour choisir !".formatted();
                    msgPostTreatment = hook -> hook.retrieveOriginal().queue(message -> {
                        GameService.createGame(event.getInteraction().getUser(), message.getIdLong());

                        for (ShifumiAction shifumiAction : ShifumiAction.values())
                            message.addReaction(shifumiAction.getEmoji()).queue();
                    });
                }
                case LIST_ALL_GAME -> msg = listGameCommand(event, GameService.getHistoricGame());
                case LIST_GAME_ME -> msg = listGameCommand(event, GameService.getHistoricGame()
                    .stream()
                    .filter(game ->
                        game.getPlayerEffectiveName()
                            .equals(event.getUser()
                                .getEffectiveName())).toList()
                );
                default -> throw new IllegalStateException("I'm a TeaPot !");
            }

            Consumer<InteractionHook> finalMsgPostTreatment = msgPostTreatment;
            event.reply(msg).queue(hook -> hook.retrieveOriginal().queue(message ->
                {
                    messageMapByUser.put(user, message);
                    finalMsgPostTreatment.accept(hook);
                }
            ));
        } catch (Exception e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    private void wrongChannelFeedback(SlashCommandInteractionEvent event, TextChannel channel) {
        event.reply("Il semblerez que tu ne sois pas dans le bon " +
                "channel, je t'invite à utiliser mes commandes dans <#%s>"
                    .formatted(channel.getIdLong()))
            .setEphemeral(true).queue();
    }
    //endregion
}
