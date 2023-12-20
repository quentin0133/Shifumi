package fr.dawan.shifumeunmi.discord.listeners;

import fr.dawan.shifumeunmi.discord.ShifumiBot;
import fr.dawan.shifumeunmi.shifumi.entities.Game;
import fr.dawan.shifumeunmi.shifumi.enums.BotCommand;
import fr.dawan.shifumeunmi.shifumi.enums.ShifumiAction;
import fr.dawan.shifumeunmi.shifumi.service.GameService;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log
public class InteractiveCommandListener extends ListenerAdapter {
    private static final Map<Long, Message> messageMapByIdMember = new HashMap<>();

    //region Slash Command Methods
    private static String listGameCommand(List<Game> listGame) {
        if (listGame.isEmpty())
            return "J'ai toujours pas fais de partie :sob:";

        // Exemple :
        // Game n°5 :
        // Affichage de la partie n°5
        // --------
        // Game n°6 :
        // Affichage de la partie n°6
        // --------
        return "--------\n" + IntStream.range(1, listGame.size() + 1).mapToObj(i ->
                        "Game n°%d :%n%s%n--------".formatted(i, listGame.get(i - 1)))
                .collect(Collectors.joining("\n"));
    }
    //endregion

    public static void clean(long idMember) {
        messageMapByIdMember.remove(idMember);
    }

    //region OnSlashCommandInteraction Event
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();

        if (guild == null || member == null) return;

        long idMember = member.getIdLong();

        // Check if the user use the command in the channel for the bot
        if (isMemberInWrongChannel(event)) return;

        try {
            String msg;
            Consumer<Message> msgPostTreatment = hook -> {};
            BotCommand action = BotCommand.valueOf(event.getName().toUpperCase());

            if (messageMapByIdMember.containsKey(idMember))
            {
                messageMapByIdMember.get(idMember).delete().queue(v -> {},e -> {});
            }

            switch (action) {
                case HELP -> msg = GameService.helpMessage();
                case PLAY -> {
                    if (isMemberPlaying(event, idMember)) return;

                    msg = "Jouons !%nAjoute une réaction pour choisir !".formatted();
                    msgPostTreatment = message -> {
                        GameService.createGame(idMember, message);

                        for (ShifumiAction shifumiAction : ShifumiAction.values())
                            message.addReaction(shifumiAction.getEmoji()).queue();
                    };
                }
                case LIST_ALL_GAME -> msg = listGameCommand(GameService.getHistoricGame());
                case LIST_GAME_ME -> msg = listGameCommand(GameService.getHistoricGame()
                        .stream()
                        .filter(game ->
                                game.getPlayerEffectiveName()
                                        .equals(member
                                                .getEffectiveName())).toList()
                );
                default -> throw new IllegalStateException("I'm a TeaPot !");
            }

            Consumer<Message> finalMsgPostTreatment = msgPostTreatment;
            event.reply(msg).queue(hook -> hook.retrieveOriginal().queue(message ->
                    {
                        messageMapByIdMember.put(idMember, message);
                        finalMsgPostTreatment.accept(message);
                    }
            ));
        } catch (Exception e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    private boolean isMemberInWrongChannel(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild != null && !event.getChannel().getName().equals(ShifumiBot.getNameChannel())) {
            // Check if the channel exist otherwise he will create a new one
            // For linking the channel to the user
            if (guild.getTextChannels().stream().noneMatch(channel ->
                    ShifumiBot.getNameChannel().equals(channel.getName())))
                ShifumiBot.createChannel(guild).queue(channel ->
                        channel.sendMessage(GameService.helpMessage()).queue(v -> wrongChannelFeedback(event, channel)));
            else
                guild.getTextChannelsByName(ShifumiBot.getNameChannel(), false).stream().findFirst()
                        .ifPresent(channel -> wrongChannelFeedback(event, channel));
            return true;
        }
        return false;
    }

    private static boolean isMemberPlaying(@NotNull SlashCommandInteractionEvent event, long idMember) {
        Game game = GameService.getGameByIdMember(idMember);

        if (game != null) {
            Guild guildCheck = ShifumiBot.findGuildById(game.getGuildId());
            // Guild still exist ?
            if (guildCheck != null) {
                TextChannel channelCheck = guildCheck.getTextChannelById(game.getChannelId());
                // Channel still exist ?
                if (channelCheck != null) {
                    try {
                        return isPresent(event, channelCheck, game).get();
                    } catch (Exception e) {return false;}
                    // The current game still exist (There's proof)
                }
            }
            // The message of the current game is deleted, the user is allowed to play a new game
        }
        return false;
    }

    private static CompletableFuture<Boolean> isPresent(@NotNull SlashCommandInteractionEvent event, TextChannel channelCheck, Game game) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        channelCheck.retrieveMessageById(game.getMessageId()).queue(message ->
            event.reply("Vous avez déjà une partie en cours : https://discord.com/channels/%s/%s/%d"
                .formatted(game.getGuildId(), game.getChannelId(),
                    game.getMessageId())
            ).setEphemeral(true).queue(v -> completableFuture.complete(true)),
            e -> completableFuture.complete(false));
        return completableFuture;
    }

    private void wrongChannelFeedback(SlashCommandInteractionEvent event, TextChannel channel) {
        event.reply("Il semblerez que tu ne sois pas dans le bon " +
                        "channel, je t'invite à utiliser mes commandes dans <#%s>"
                                .formatted(channel.getIdLong()))
                .setEphemeral(true).queue();
    }
    //endregion
}
