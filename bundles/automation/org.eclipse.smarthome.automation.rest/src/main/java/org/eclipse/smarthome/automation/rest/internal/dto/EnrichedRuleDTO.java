package org.eclipse.smarthome.automation.rest.internal.dto;

import org.eclipse.smarthome.automation.Rule;
import org.eclipse.smarthome.automation.RuleStatusInfo;

public class EnrichedRuleDTO extends Rule {

    public boolean isEnabled;
    public RuleStatusInfo status;

    public EnrichedRuleDTO(Rule rule) {
        this.actions = rule.getActions();
        this.conditions = rule.getConditions();
        this.configDescriptions = rule.getConfigurationDescriptions();
        this.configurations = rule.getConfiguration();
        this.ruleTemplateUID = rule.getTemplateUID();
        this.triggers = rule.getTriggers();
        this.uid = rule.getUID();
        this.name = rule.getName();
        this.tags = rule.getTags();
        this.description = rule.getDescription();
    }
}
