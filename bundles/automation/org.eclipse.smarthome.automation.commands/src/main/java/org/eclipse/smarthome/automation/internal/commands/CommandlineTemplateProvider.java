/**
 * Copyright (c) 1997, 2015 by ProSyst Software GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.automation.internal.commands;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.smarthome.automation.core.util.ConnectionValidator;
import org.eclipse.smarthome.automation.parser.Parser;
import org.eclipse.smarthome.automation.parser.ParsingException;
import org.eclipse.smarthome.automation.parser.ParsingNestedException;
import org.eclipse.smarthome.automation.template.RuleTemplate;
import org.eclipse.smarthome.automation.template.Template;
import org.eclipse.smarthome.automation.template.TemplateProvider;
import org.eclipse.smarthome.automation.type.ModuleType;
import org.eclipse.smarthome.automation.type.ModuleTypeProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * This class is implementation of {@link TemplateProvider}. It extends functionality of {@link AbstractCommandProvider}
 * <p>
 * It is responsible for execution of Automation {@link PluggableCommands}, corresponding to the {@link RuleTemplate}s:
 * <ul>
 * <li>imports the {@link RuleTemplate}s from local files or from URL resources
 * <li>provides functionality for persistence of the {@link RuleTemplate}s
 * <li>removes the {@link RuleTemplate}s and their persistence
 * </ul>
 *
 * @author Ana Dimova - Initial Contribution
 * @author Kai Kreuzer - refactored (managed) provider and registry implementation
 * @author Ana Dimova - refactor Parser interface.
 *
 */
public class CommandlineTemplateProvider extends AbstractCommandProvider<RuleTemplate>implements TemplateProvider {

    /**
     * This field holds a reference to the {@link ModuleTypeProvider} service registration.
     */
    @SuppressWarnings("rawtypes")
    protected ServiceRegistration tpReg;

    /**
     * This constructor creates instances of this particular implementation of {@link TemplateProvider}. It does not add
     * any new functionality to the constructors of the providers. Only provides consistency by invoking the parent's
     * constructor.
     *
     * @param context is the {@link BundleContext}, used for creating a tracker for {@link Parser} services.
     */
    public CommandlineTemplateProvider(BundleContext context) {
        super(context);
    }

    /**
     * This method differentiates what type of {@link Parser}s is tracked by the tracker.
     * For this concrete provider, this type is a {@link RuleTemplate} {@link Parser}.
     *
     * @see AbstractCommandProvider#addingService(org.osgi.framework.ServiceReference)
     */
    @Override
    public Object addingService(@SuppressWarnings("rawtypes") ServiceReference reference) {
        if (reference.getProperty(Parser.PARSER_TYPE).equals(Parser.PARSER_TEMPLATE)) {
            return super.addingService(reference);
        }
        return null;
    }

    /**
     * This method is responsible for exporting a set of RuleTemplates in a specified file.
     *
     * @param parserType is relevant to the format that you need for conversion of the RuleTemplates in text.
     * @param set a set of RuleTemplates to export.
     * @param file a specified file for export.
     * @throws Exception when I/O operation has failed or has been interrupted or generating of the text fails
     *             for some reasons.
     * @see AutomationCommandsPluggable#exportTemplates(String, Set, File)
     */
    public void exportTemplates(String parserType, Set<RuleTemplate> set, File file) throws Exception {
        super.exportData(parserType, set, file);
    }

    /**
     * This method is responsible for importing a set of RuleTemplates from a specified file or URL resource.
     *
     * @param parserType is relevant to the format that you need for conversion of the RuleTemplates in text.
     * @param url a specified URL for import.
     * @throws IOException when I/O operation has failed or has been interrupted.
     * @throws ParsingException when parsing of the text fails for some reasons.
     * @see AutomationCommandsPluggable#importTemplates(String, URL)
     */
    public Set<RuleTemplate> importTemplates(String parserType, URL url) throws IOException, ParsingException {
        InputStreamReader inputStreamReader = null;
        Parser<RuleTemplate> parser = parsers.get(parserType);
        if (parser == null) {
            inputStreamReader = new InputStreamReader(new BufferedInputStream(url.openStream()));
            return importData(url, parser, inputStreamReader);
        } else {
            throw new ParsingException(new ParsingNestedException(ParsingNestedException.TEMPLATE, null,
                    new Exception("Parser " + parserType + " not available")));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public RuleTemplate getTemplate(String UID, Locale locale) {
        synchronized (providerPortfolio) {
            return providedObjectsHolder.get(UID);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<RuleTemplate> getTemplates(Locale locale) {
        synchronized (providedObjectsHolder) {
            return providedObjectsHolder.values();
        }
    }

    @Override
    public void close() {
        if (tpReg != null) {
            tpReg.unregister();
            tpReg = null;
        }
        super.close();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Set<RuleTemplate> importData(URL url, Parser<RuleTemplate> parser, InputStreamReader inputStreamReader)
            throws ParsingException {
        Set<RuleTemplate> providedObjects = parser.parse(inputStreamReader);
        if (providedObjects != null && !providedObjects.isEmpty()) {
            List<String> portfolio = new ArrayList<String>();
            synchronized (providerPortfolio) {
                providerPortfolio.put(url, portfolio);
            }
            List<ParsingNestedException> importDataExceptions = new ArrayList<ParsingNestedException>();
            for (RuleTemplate ruleT : providedObjects) {
                List<ParsingNestedException> exceptions = new ArrayList<ParsingNestedException>();
                String uid = ruleT.getUID();
                try {
                    ConnectionValidator.validateConnections(ruleT.getTriggers(), ruleT.getConditions(),
                            ruleT.getActions());
                } catch (Exception e) {
                    exceptions.add(new ParsingNestedException(ParsingNestedException.TEMPLATE, uid,
                            "Failed to validate connections of RuleTemplate with UID \"" + uid + "\"!", e));
                }
                checkExistence(uid, exceptions);
                if (exceptions.isEmpty()) {
                    portfolio.add(uid);
                    synchronized (providedObjectsHolder) {
                        providedObjectsHolder.put(uid, ruleT);
                    }
                } else
                    importDataExceptions.addAll(exceptions);
            }
            if (importDataExceptions.isEmpty()) {
                Dictionary<String, Object> properties = new Hashtable<String, Object>();
                properties.put(REG_PROPERTY_RULE_TEMPLATES, providedObjectsHolder.keySet());
                if (tpReg == null)
                    tpReg = bc.registerService(TemplateProvider.class.getName(), this, properties);
                else {
                    tpReg.setProperties(properties);
                }
            } else {
                throw new ParsingException(importDataExceptions);
            }
        }
        return providedObjects;
    }

    /**
     * This method is responsible for checking the existence of {@link Template}s with the same
     * UIDs before these objects to be added in the system.
     *
     * @param uid UID of the newly created {@link Template}, which to be checked.
     * @param exceptions accumulates exceptions if {@link ModuleType} with the same UID exists.
     */
    protected void checkExistence(String uid, List<ParsingNestedException> exceptions) {
        if (AutomationCommandsPluggable.templateRegistry == null) {
            exceptions.add(new ParsingNestedException(ParsingNestedException.TEMPLATE, uid,
                    new IllegalArgumentException("Failed to create Rule Template with UID \"" + uid
                            + "\"! Can't guarantee yet that other Rule Template with the same UID does not exist.")));
        }
        if (AutomationCommandsPluggable.templateRegistry.get(uid) != null) {
            exceptions.add(new ParsingNestedException(ParsingNestedException.TEMPLATE, uid,
                    new IllegalArgumentException("Rule Template with UID \"" + uid
                            + "\" already exists! Failed to create a second with the same UID!")));
        }
    }

}
