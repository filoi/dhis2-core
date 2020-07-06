package org.hisp.dhis.tracker.converter;

/*
 * Copyright (c) 2004-2020, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.google.common.collect.ImmutableMap;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionAssign;
import org.hisp.dhis.rules.models.RuleActionScheduleMessage;
import org.hisp.dhis.rules.models.RuleActionSendMessage;
import org.hisp.dhis.rules.models.RuleEffect;
import org.hisp.dhis.tracker.sideeffect.TrackerAssignValueSideEffect;
import org.hisp.dhis.tracker.sideeffect.TrackerScheduleMessageSideEffect;
import org.hisp.dhis.tracker.sideeffect.TrackerSendMessageSideEffect;
import org.hisp.dhis.tracker.sideeffect.TrackerSideEffect;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Zubair Asghar
 */

@Service
public class DefaultTrackerSideEffectConverterService implements TrackerSideEffectConverterService
{
    private final ImmutableMap<Class<? extends RuleAction>, Function<RuleEffect, TrackerSideEffect>> TO_SIDE_EFFECT = new
        ImmutableMap.Builder<Class<? extends RuleAction>, Function<RuleEffect, TrackerSideEffect>>()
        .put( RuleActionSendMessage.class, this::toTrackerSendMessageSideEffect )
        .put( RuleActionScheduleMessage.class, this::toTrackerScheduleMessageSideEffect )
        .put( RuleActionAssign.class, this::toTrackerAssignSideEffect )
        .build();

    private final ImmutableMap<Class<? extends TrackerSideEffect>, Function<TrackerSideEffect, RuleEffect>> TO_RULE_EFFECT = new
        ImmutableMap.Builder<Class<? extends TrackerSideEffect>, Function<TrackerSideEffect, RuleEffect>>()
        .put( TrackerAssignValueSideEffect.class, this::toAssignRuleEffect )
        .put( TrackerSendMessageSideEffect.class, this::toSendMessageRuleEffect )
        .put( TrackerScheduleMessageSideEffect.class, this::toScheduleMessageRuleEffect )
        .build();

    @Override
    public Map<String, List<TrackerSideEffect>> toTrackerSideEffects( Map<String, List<RuleEffect>> ruleEffects )
    {
        Map<String, List<TrackerSideEffect>> trackerSideEffects = new HashMap<>();

        for ( Map.Entry<String, List<RuleEffect>> entry : ruleEffects.entrySet() )
        {
            if ( entry.getValue() != null && !entry.getValue().isEmpty() )
            {
                List<RuleEffect> ruleEffectList = entry.getValue();

                trackerSideEffects.put( entry.getKey(), toTrackerSideEffectList( ruleEffectList ) );
            }
        }

        return trackerSideEffects;
    }

    @Override
    public Map<String, List<RuleEffect>> toRuleEffects( Map<String, List<TrackerSideEffect>> trackerSideEffects )
    {
        Map<String, List<RuleEffect>> ruleEffects = new HashMap<>();

        for ( Map.Entry<String, List<TrackerSideEffect>> entry : trackerSideEffects.entrySet() )
        {
            if ( entry.getValue() != null && !entry.getValue().isEmpty() )
            {
                List<TrackerSideEffect> trackerSideEffectsList = entry.getValue();

                ruleEffects.put( entry.getKey(), toRuleEffectList( trackerSideEffectsList ) );
            }
        }

        return ruleEffects;
    }

    private List<TrackerSideEffect> toTrackerSideEffectList (List<RuleEffect> ruleEffects )
    {
        List<TrackerSideEffect> trackerSideEffects = new ArrayList<>();

        for ( RuleEffect ruleEffect : ruleEffects )
        {
            if ( ruleEffect != null )
            {
                RuleAction action = ruleEffect.ruleAction();

                trackerSideEffects.add( TO_SIDE_EFFECT.get( action.getClass() ).apply( ruleEffect ) );
            }
        }

        return trackerSideEffects;
    }

    private List<RuleEffect> toRuleEffectList( List<TrackerSideEffect> trackerSideEffects )
    {
        List<RuleEffect> ruleEffects = new ArrayList<>();

        for ( TrackerSideEffect trackerSideEffect : trackerSideEffects )
        {
            if ( trackerSideEffect != null )
            {
                ruleEffects.add( TO_RULE_EFFECT.get( trackerSideEffect.getClass() ).apply( trackerSideEffect ) );
            }
        }

        return ruleEffects;
    }

    private TrackerSideEffect toTrackerSendMessageSideEffect( RuleEffect ruleEffect )
    {
        RuleActionSendMessage ruleActionSendMessage = (RuleActionSendMessage) ruleEffect.ruleAction();

        return TrackerSendMessageSideEffect.builder().notification( ruleActionSendMessage.notification() )
            .data( ruleActionSendMessage.data() )
            .build();
    }

    private TrackerSideEffect toTrackerScheduleMessageSideEffect( RuleEffect ruleEffect )
    {
        RuleActionScheduleMessage ruleActionScheduleMessage = (RuleActionScheduleMessage) ruleEffect.ruleAction();

        return TrackerScheduleMessageSideEffect.builder().notification( ruleActionScheduleMessage.notification() )
            .data( ruleActionScheduleMessage.data() )
            .build();
    }

    private TrackerSideEffect toTrackerAssignSideEffect( RuleEffect ruleEffect )
    {
        RuleActionAssign ruleActionAssign = (RuleActionAssign) ruleEffect.ruleAction();

        return TrackerAssignValueSideEffect
                .builder().content( ruleActionAssign.content() ).field( ruleActionAssign.field() ).data( ruleEffect.data() )
                .build();
    }

    private RuleEffect toAssignRuleEffect( TrackerSideEffect trackerSideEffect )
    {
        TrackerAssignValueSideEffect assignValueSideEffect = (TrackerAssignValueSideEffect) trackerSideEffect;

        return RuleEffect.create( RuleActionAssign.create( assignValueSideEffect.getContent(),
                assignValueSideEffect.getData(), assignValueSideEffect.getField() ), assignValueSideEffect.getData() );
    }

    private RuleEffect toSendMessageRuleEffect( TrackerSideEffect trackerSideEffect )
    {
        TrackerSendMessageSideEffect sendMessageSideEffect = (TrackerSendMessageSideEffect) trackerSideEffect;

        return RuleEffect.create( RuleActionSendMessage.create( sendMessageSideEffect.getNotification(),
                sendMessageSideEffect.getData() ), sendMessageSideEffect.getData() );
    }

    private RuleEffect toScheduleMessageRuleEffect( TrackerSideEffect trackerSideEffect )
    {
        TrackerScheduleMessageSideEffect scheduleMessageSideEffect = (TrackerScheduleMessageSideEffect) trackerSideEffect;

        return RuleEffect.create( RuleActionScheduleMessage.create( scheduleMessageSideEffect.getNotification(),
                scheduleMessageSideEffect.getData() ), scheduleMessageSideEffect.getData() );
    }
}
