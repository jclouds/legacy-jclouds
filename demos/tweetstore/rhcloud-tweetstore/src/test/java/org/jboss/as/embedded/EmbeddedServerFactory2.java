/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.as.embedded;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.logging.LogManager;

import org.jboss.modules.Module;
import org.jboss.modules.ModuleClassLoader;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.log.JDKModuleLogger;
import org.jclouds.demo.tweetstore.integration.util.ObjectFields;

/**
 * A variant of {@link EmbeddedServerFactory} that resets JDK logging for JBoss,
 * which requires its own settings in this area.
 * 
 * Needs to be in the org.jboss.as.embedded package to be able to use
 * {@code InitialModuleLoaderFactory}.
 * 
 * @author Andrew Phillips
 * @see EmbeddedServerFactory
 */
public class EmbeddedServerFactory2 {

    // mainly copied from EmbeddedServerFactory
    public static StandaloneServer create(final File jbossHomeDir, final Properties systemProps, final Map<String, String> systemEnv, String...systemPackages) {
        if (jbossHomeDir == null || jbossHomeDir.isDirectory() == false)
            throw new IllegalStateException("Invalid jboss.home.dir: " + jbossHomeDir);

        if (systemProps.getProperty(ServerEnvironment.HOME_DIR) == null)
            systemProps.setProperty(ServerEnvironment.HOME_DIR, jbossHomeDir.getAbsolutePath());

        File modulesDir = new File(jbossHomeDir + "/modules");
        final ModuleLoader moduleLoader = InitialModuleLoaderFactory.getModuleLoader(modulesDir, systemPackages);

        try {
            Module.registerURLStreamHandlerFactoryModule(moduleLoader.loadModule(ModuleIdentifier.create("org.jboss.vfs")));

            // Initialize the Logging system
            ModuleIdentifier logModuleId = ModuleIdentifier.create("org.jboss.logmanager");
            ModuleClassLoader logModuleClassLoader = moduleLoader.loadModule(logModuleId).getClassLoader();
            try {
                /*
                 * The original code simply sets the thread context classloader and lets LogManager
                 * load the class. This causes problems in tests because any other component that
                 * also happens to use JDK logging will have potentially loaded a *different*
                 * LogManager.
                 * If you force the JBoss LogManager to be loaded (by setting the 'java.util.logging.manager'
                 * system property) earlier in the test run, it will not be loaded by the correct
                 * classloader and cause ClassCastExceptions.
                 */
                LogManager jbossLogManager = (LogManager) logModuleClassLoader.loadClass("org.jboss.logmanager.LogManager").newInstance();
                ObjectFields.set("manager", null, jbossLogManager, LogManager.class);
                ObjectFields.set("readPrimordialConfiguration", jbossLogManager, false, LogManager.class); 

                if (LogManager.getLogManager().getClass() == LogManager.class) {
                    System.err.println("WARNING: Failed to load the specified logmodule " + logModuleId);
                } else {
                    Module.setModuleLogger(new JDKModuleLogger());
                }
            } catch (Exception exception) {
                // copied from LogManager
                System.err.println("Could not load Logmanager \"org.jboss.logmanager.LogManager\"");
                exception.printStackTrace();
            }

            __redirected.__JAXPRedirected.changeAll(ModuleIdentifier.fromString("javax.xml.jaxp-provider"), moduleLoader);

            return EmbeddedServerFactory.create(moduleLoader, jbossHomeDir, systemProps, systemEnv);
        }
        catch (ModuleLoadException e) {
            throw new RuntimeException(e);
        }
    }
}
