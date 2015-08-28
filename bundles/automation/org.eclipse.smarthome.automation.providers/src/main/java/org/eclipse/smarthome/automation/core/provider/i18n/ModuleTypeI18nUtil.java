/**
 * Copyright (c) 1997, 2015 by ProSyst Software GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.automation.core.provider.i18n;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.smarthome.automation.type.Input;
import org.eclipse.smarthome.automation.type.Output;
import org.eclipse.smarthome.core.i18n.I18nProvider;
import org.eclipse.smarthome.core.i18n.I18nUtil;
import org.osgi.framework.Bundle;

/**
 * This class is used as utility for resolving the localized {@link ModuleTypes}s. It automatically infers the key if
 * the default text is not a constant with the assistance of {@link I18nProvider}.
 *
 * @author Ana Dimova - Initial Contribution
 *
 */
public class ModuleTypeI18nUtil {

    public static final String MODULE_TYPE = "module-type.";

    public static String getLocalizedModuleTypeLabel(I18nProvider i18nProvider, Bundle bundle, String moduleTypeUID,
            String defaultLabel, Locale locale) {
        String key = I18nUtil.isConstant(defaultLabel) ? I18nUtil.stripConstant(defaultLabel)
                : inferModuleTypeKey(moduleTypeUID, "label");
        return i18nProvider.getText(bundle, key, defaultLabel, locale);
    }

    public static String getLocalizedModuleTypeDescription(I18nProvider i18nProvider, Bundle bundle,
            String moduleTypeUID, String defaultDescription, Locale locale) {
        String key = I18nUtil.isConstant(defaultDescription) ? I18nUtil.stripConstant(defaultDescription)
                : inferModuleTypeKey(moduleTypeUID, "description");
        return i18nProvider.getText(bundle, key, defaultDescription, locale);
    }

    public static Set<Input> getLocalizedInputs(I18nProvider i18nProvider, Set<Input> inputs, Bundle bundle, String uid,
            Locale locale) {
        Set<Input> linputs = new HashSet<>();
        if (inputs != null)
            for (Input input : inputs) {
                String inputName = input.getName();
                String ilabel = ModuleTypeI18nUtil.getInputLabel(i18nProvider, bundle, uid, inputName, input.getLabel(),
                        locale);
                String idescription = ModuleTypeI18nUtil.getInputDescription(i18nProvider, bundle, uid, inputName,
                        input.getDescription(), locale);
                linputs.add(new Input(inputName, input.getType(), ilabel, idescription, input.getTags(),
                        input.isRequired(), input.getReference(), input.getDefaultValue()));
            }
        return linputs;
    }

    public static Set<Output> getLocalizedOutputs(I18nProvider i18nProvider, Set<Output> outputs, Bundle bundle,
            String uid, Locale locale) {
        Set<Output> loutputs = new HashSet<>();
        if (outputs != null)
            for (Output output : outputs) {
                String outputName = output.getName();
                String olabel = ModuleTypeI18nUtil.getOutputLabel(i18nProvider, bundle, uid, outputName,
                        output.getLabel(), locale);
                String odescription = ModuleTypeI18nUtil.getOutputDescription(i18nProvider, bundle, uid, outputName,
                        output.getDescription(), locale);
                loutputs.add(new Output(outputName, output.getType(), olabel, odescription, output.getTags(),
                        output.getReference(), output.getDefaultValue()));
            }
        return loutputs;
    }

    private static String getInputLabel(I18nProvider i18nProvider, Bundle bundle, String moduleTypeUID,
            String inputName, String defaultLabel, Locale locale) {
        String key = I18nUtil.isConstant(defaultLabel) ? I18nUtil.stripConstant(defaultLabel)
                : inferInputKey(moduleTypeUID, inputName, "label");
        return i18nProvider.getText(bundle, key, defaultLabel, locale);
    }

    private static String getInputDescription(I18nProvider i18nProvider, Bundle bundle, String moduleTypeUID,
            String inputName, String defaultDescription, Locale locale) {
        String key = I18nUtil.isConstant(defaultDescription) ? I18nUtil.stripConstant(defaultDescription)
                : inferInputKey(moduleTypeUID, inputName, "description");
        return i18nProvider.getText(bundle, key, defaultDescription, locale);
    }

    private static String getOutputLabel(I18nProvider i18nProvider, Bundle bundle, String ruleTemplateUID,
            String outputName, String defaultLabel, Locale locale) {
        String key = I18nUtil.isConstant(defaultLabel) ? I18nUtil.stripConstant(defaultLabel)
                : inferOutputKey(ruleTemplateUID, outputName, "label");
        return i18nProvider.getText(bundle, key, defaultLabel, locale);
    }

    public static String getOutputDescription(I18nProvider i18nProvider, Bundle bundle, String moduleTypeUID,
            String outputName, String defaultDescription, Locale locale) {
        String key = I18nUtil.isConstant(defaultDescription) ? I18nUtil.stripConstant(defaultDescription)
                : inferOutputKey(moduleTypeUID, outputName, "description");
        return i18nProvider.getText(bundle, key, defaultDescription, locale);
    }

    private static String inferModuleTypeKey(String moduleTypeUID, String lastSegment) {
        return MODULE_TYPE + moduleTypeUID + "." + lastSegment;
    }

    private static String inferInputKey(String moduleTypeUID, String inputName, String lastSegment) {
        return MODULE_TYPE + moduleTypeUID + ".input." + inputName + "." + lastSegment;
    }

    private static String inferOutputKey(String moduleTypeUID, String outputName, String lastSegment) {
        return MODULE_TYPE + moduleTypeUID + ".output." + outputName + "." + lastSegment;
    }
}
