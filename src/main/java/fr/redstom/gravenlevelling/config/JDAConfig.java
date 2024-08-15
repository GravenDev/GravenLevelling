package fr.redstom.gravenlevelling.config;

import fr.redstom.gravenlevelling.utils.CommandExecutor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;
import java.util.List;

@Configuration
@Slf4j
public class JDAConfig {

    @Bean
    JDA client(@Value("${bot.token}") String token,
               List<ListenerAdapter> eventListeners,
               List<CommandExecutor> commandExecutors) throws InterruptedException {
        JDA client = JDABuilder.create(token, EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)).build();

        log.info("Event listeners found: {}", String.join(", ", eventListeners.stream().map(Object::getClass).map(Class::getName).toList()));
        log.info("Commands found: {}", String.join(", ", commandExecutors.stream().map(Object::getClass).map(Class::getName).toList()));

        client.addEventListener(eventListeners.toArray());

        client.updateCommands()
                .addCommands(commandExecutors.stream().map(CommandExecutor::data).toList())
                .queue();

        return client.awaitReady();
    }

}
