package org.eclipse.smarthome.core.thing.type;

import java.util.List;
import java.util.Locale;

import org.eclipse.smarthome.core.thing.ChannelUID;

public class ThingTypeUtil {

    public static ChannelType getChannelType(ThingType thingType, ChannelUID channelUID) {
        return getChannelType(thingType, channelUID, null);
    }

    public static ChannelType getChannelType(ThingType thingType, ChannelUID channelUID, Locale locale) {
        if (thingType != null) {
            if (!channelUID.isInGroup()) {
                for (ChannelDefinition channelDefinition : thingType.getChannelDefinitions()) {
                    if (channelDefinition.getId().equals(channelUID.getId())) {
                        return channelDefinition.getType();
                    }
                }
            } else {
                List<ChannelGroupDefinition> channelGroupDefinitions = thingType.getChannelGroupDefinitions();
                for (ChannelGroupDefinition channelGroupDefinition : channelGroupDefinitions) {
                    if (channelGroupDefinition.getId().equals(channelUID.getGroupId())) {
                        for (ChannelDefinition channelDefinition : channelGroupDefinition.getType()
                                .getChannelDefinitions()) {
                            if (channelDefinition.getId().equals(channelUID.getIdWithoutGroup())) {
                                return channelDefinition.getType();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
