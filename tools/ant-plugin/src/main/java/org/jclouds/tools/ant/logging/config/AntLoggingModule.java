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

   public AntLoggingModule(Project project) {
      this.project = project;
   }

   @Override
   public LoggerFactory createLoggerFactory() {
      return new AntLogger.AntLoggerFactory(checkNotNull(project, "project"));
   }
}
