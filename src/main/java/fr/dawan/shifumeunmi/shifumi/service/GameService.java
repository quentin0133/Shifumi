package fr.dawan.shifumeunmi.shifumi.service;

import fr.dawan.shifumeunmi.shifumi.entities.Game;
import fr.dawan.shifumeunmi.shifumi.enums.BotCommand;
import fr.dawan.shifumeunmi.shifumi.enums.ShifumiAction;
import net.dv8tion.jda.api.entities.Message;

import java.util.*;

public class GameService {
    private GameService() {}

    private static final int LIMIT_LIST = 5;

    private static final LinkedList<Game> historicGames = new LinkedList<>();
    private static final Map<Long, Game> gameSession = new HashMap<>();

    public static void createGame(long memberId, Message message) {
        gameSession.put(memberId, new Game("<@%s>".formatted(memberId), message));
    }

    public static Game startGame(long idUser, ShifumiAction playerAction) {
        Game game = gameSession.get(idUser).startGame(playerAction);

        historicGames.addLast(game);
        if (historicGames.size() > LIMIT_LIST)
            historicGames.removeFirst();

        gameSession.remove(idUser);
        return game;
    }

    public static List<Game> getHistoricGame() {
        return historicGames;
    }

    public static boolean isPlaying(long memberId) throws NullPointerException {
        return gameSession.containsKey(memberId);
    }

    public static Game getGameByIdMember(long memberId) {
        return gameSession.get(memberId);
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

    public static boolean isMessageGame(long messageId) {
        return gameSession.values().stream().anyMatch(game -> game.getMessageId() == messageId);
    }
}
