package fr.dawan.shifumeunmi.shifumi.service;

import fr.dawan.shifumeunmi.shifumi.entities.Game;
import fr.dawan.shifumeunmi.shifumi.enums.BotCommand;
import fr.dawan.shifumeunmi.shifumi.enums.ShifumiAction;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.*;

public class GameService {
    private GameService() {}

    private static final int LIMIT_LIST = 20;

    // private static final Map<Long, LinkedList<Game>> historicGames = new LinkedList<>();
    private static final LinkedList<Game> historicGames = new LinkedList<>();
    private static final Map<User, Game> gameSession = new HashMap<>();

    public static void createGame(User user, long message) {
        gameSession.put(user, new Game("<@%s>".formatted(user.getIdLong()), message));
    }

    public static Game startGame(User user, ShifumiAction playerAction) {
        Game game = gameSession.get(user).startGame(playerAction);

        historicGames.addLast(game);
        if (historicGames.size() > LIMIT_LIST)
            historicGames.removeFirst();

        gameSession.remove(user);
        return game;
    }

    public static List<Game> getHistoricGame() {
        return historicGames;
    }

    public static boolean isPlaying(User user) throws NullPointerException {
        return gameSession.containsKey(user);
    }

    public static void updateMessage(User authorReaction, Message message) {
        gameSession.get(authorReaction).setMessageId(message.getIdLong());
    }

    public static void updateMessage(long idMessage) {
        gameSession.entrySet().stream()
                .filter(entry -> entry.getValue().getMessageId() == idMessage)
                .findFirst()
                .ifPresent(entry -> entry.getValue()
                .setMessageId(idMessage));
    }

    public static long getIdMessage(User authorReaction) {
        return gameSession.get(authorReaction).getMessageId();
    }

    public static String helpMessage() {
        StringBuilder helpMessage = new StringBuilder()
            .append("Je suis le bot ShiFumeMi.\nVoici mes commandes :\n");
        for (BotCommand command : BotCommand.values()) {
            helpMessage.append("➣ **/").append(command.getActionName()).append("** : ")
                .append(command.getDescription()).append('\n');
        }
        return helpMessage.toString();
    }
}
