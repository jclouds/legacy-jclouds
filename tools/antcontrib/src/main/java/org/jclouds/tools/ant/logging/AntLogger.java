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
package org.jclouds.tools.ant.logging;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.apache.tools.ant.Project;
import org.jclouds.logging.BaseLogger;
import org.jclouds.logging.Logger;

import com.google.common.collect.Sets;

/**
 * {@link org.apache.tools.ant.Project} implementation of {@link Logger}.
 * 
 * @author Adrian Cole
 * 
 */
public class AntLogger extends BaseLogger {
   private final Project project;
   private final String category;
   private boolean alwaysLog;

   public static class AntLoggerFactory implements LoggerFactory {
      private final Project project;
      private final Set<String> upgrades;

      public AntLoggerFactory(Project project, String... upgrades) {
         this.project = checkNotNull(project, "project");
         this.upgrades = Sets.newHashSet(upgrades);
      }

      public Logger getLogger(String category) {
         return new AntLogger(project, category, upgrades.contains(category));
      }
   }

   public AntLogger(Project project, String category, boolean alwaysLog) {
      this.project = checkNotNull(project, "project");
      this.category = category;
      this.alwaysLog = alwaysLog;
   }

   @Override
   protected void logTrace(String message) {
   }

   public boolean isTraceEnabled() {
      return false;
   }

   @Override
   protected void logDebug(String message) {
      project.log("      " + message, alwaysLog ? Project.MSG_INFO : Project.MSG_DEBUG);
   }

   public boolean isDebugEnabled() {
      return true;
   }

   @Override
   protected void logInfo(String message) {
      project.log("      " + message);
   }

   public boolean isInfoEnabled() {
      return true;
   }

   @Override
   protected void logWarn(String message) {
      project.log("      " + message, Project.MSG_WARN);
   }

   @Override
   protected void logWarn(String message, Throwable e) {
      project.log("      " + message, e, Project.MSG_WARN);
   }

   public boolean isWarnEnabled() {
      return true;
   }

   @Override
   protected void logError(String message) {
      project.log("      " + message, Project.MSG_ERR);
   }

   @Override
   protected void logError(String message, Throwable e) {
      project.log("      " + message, e, Project.MSG_ERR);
   }

   public boolean isErrorEnabled() {
      return true;
   }

   public String getCategory() {
      return category;
   }
}