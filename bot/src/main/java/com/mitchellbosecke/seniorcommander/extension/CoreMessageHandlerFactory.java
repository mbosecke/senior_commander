package com.mitchellbosecke.seniorcommander.extension;

import com.mitchellbosecke.seniorcommander.Configuration;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.handler.*;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.repository.CommunityService;
import com.mitchellbosecke.seniorcommander.repository.CommunityServiceImpl;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class CoreMessageHandlerFactory implements MessageHandlerFactory {

    @Override
    public List<MessageHandler> build(SessionFactory sessionFactory, MessageQueue messageQueue,
                                      Configuration configuration, List<Channel> channels) {

        List<MessageHandler> messageHandlers = new ArrayList<>();

        CommunityService communityService = new CommunityServiceImpl(sessionFactory);

        messageHandlers.add(new LoggingHandler());
        messageHandlers.add(new DiceHandler(messageQueue));
        messageHandlers.add(new OutputHandler(configuration, channels));
        messageHandlers.add(new CommandsHandler(communityService, messageQueue));
        messageHandlers.add(new ConversationalHandler(messageQueue));
        messageHandlers.add(new UserChatHandler(communityService));
        messageHandlers.add(new RouletteHandler(messageQueue));
        messageHandlers.add(new AdviceHandler(messageQueue));
        messageHandlers.add(new JoinPartHandler(communityService));
        messageHandlers.add(new NamesHandler(communityService));
        messageHandlers.add(new CustomCommandHandler(communityService, messageQueue));
        return messageHandlers;
    }

}
