package fr.dawan.shifumeunmi.discord.commands;

import fr.dawan.shifumeunmi.shifumi.enums.BotCommand;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Arrays;

public class CommandBinder extends ListenerAdapter {
    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getGuild().updateCommands().addCommands(
            Arrays.stream(BotCommand.values())
                .map(botCommand -> Commands.slash(botCommand.getActionName(),
                        botCommand.getDescription()))
                .toList()
        ).queue();
    }
}
