package fr.dawan.shifumeunmi.discord;

import fr.dawan.shifumeunmi.discord.commands.CommandBinder;
import fr.dawan.shifumeunmi.discord.listeners.InteractiveCommandListener;
import fr.dawan.shifumeunmi.discord.listeners.LifeCycle;
import fr.dawan.shifumeunmi.discord.listeners.ReactionListener;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
public class ShifumiBot {
    private final ShardManager shardManager;

    @Getter
    // private static final Map<Guild, TextChannel> messageChannelMapByGuild = new HashMap<>();
    private static final Map<Guild, TextChannel> messageChannelMapByGuild = new HashMap<>();
    @Getter
    private static final String nameChannel = "shifumi";

    public ShifumiBot(String token) {
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL)
            .enableIntents(GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
            )
            .setChunkingFilter(ChunkingFilter.ALL)
            .setBulkDeleteSplittingEnabled(false)
            .setActivity(Activity.playing("Pierre / Papier / Ciseaux"))
            .addEventListeners(
                new LifeCycle(),
                new CommandBinder(),
                new InteractiveCommandListener(),
                new ReactionListener()
            );
        shardManager = builder.build();
    }

    public static ChannelAction<TextChannel> createChannel(Guild guild) {
        return guild.createTextChannel(nameChannel);
    }
}
