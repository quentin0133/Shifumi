package fr.dawan.shifumeunmi.discord.listeners;

import fr.dawan.shifumeunmi.discord.ShifumiBot;
import fr.dawan.shifumeunmi.shifumi.service.GameService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;

public class LifeCycle extends ListenerAdapter {
    @Override
    public void onGuildReady(GuildReadyEvent event) {
        Optional<TextChannel> optionalChannel = event.getGuild().getTextChannels().stream()
            .filter(c -> c.getName().equals(ShifumiBot.getNameChannel())).findFirst();
        Guild guild = event.getGuild();
        optionalChannel.ifPresentOrElse(channel -> init(guild, channel),
            () -> ShifumiBot.createChannel(guild).queue(channel -> init(guild, channel)));
    }

    private static void init(Guild guild, TextChannel channel) {
        ShifumiBot.getMessageChannelMapByGuild().put(guild, channel);
        channel.sendMessage(GameService.helpMessage()).queue();
    }
}
