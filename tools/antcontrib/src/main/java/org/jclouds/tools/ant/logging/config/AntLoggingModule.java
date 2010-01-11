/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.tools.ant.logging.config;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.tools.ant.Project;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.tools.ant.logging.AntLogger;

/**
 * Configures logging of type {@link AntLogger}
 * 
 * @author Adrian Cole
 * 
 */
public class AntLoggingModule extends LoggingModule {

   private final Project project;
   private final String[] upgrades;

   public AntLoggingModule(Project project, String ... upgrades) {
      this.project = project;
      this.upgrades = upgrades;
   }

   @Override
   public LoggerFactory createLoggerFactory() {
      return new AntLogger.AntLoggerFactory(checkNotNull(project, "project"), upgrades);
   }
}
