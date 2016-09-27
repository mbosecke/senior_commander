package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class ModAuditFactory extends AbstractTimerFactory<ModAudit> {

    private final UserService userService;

    public ModAuditFactory(SessionFactory sessionFactory, List<Channel> channels, UserService userService) {
        super(sessionFactory, channels);
        this.userService = userService;
    }

    @Override
    protected ModAudit constructTimerFromModel(TimerModel timerModel, SessionFactory sessionFactory,
                                               Map<Long, Channel> channels) {
        return new ModAudit(timerModel.getId(), timerModel.getInterval(), (TwitchChannel) channels
                .get(timerModel.getChannelModel().getId()), userService, sessionFactory);
    }

    @Override
    protected Class<ModAudit> getTimerClass() {
        return ModAudit.class;
    }
}
