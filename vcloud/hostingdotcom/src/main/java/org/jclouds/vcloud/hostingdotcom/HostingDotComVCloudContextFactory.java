package org.jclouds.vcloud.hostingdotcom;

import java.util.Properties;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.RestContext;

import com.google.inject.Module;

/**
 * Creates {@link HostingDotComVCloudContext} instances based on the most commonly requested
 * arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see HostingDotComVCloudContext
 */
public class HostingDotComVCloudContextFactory {

   public static RestContext<HostingDotComVCloudAsyncClient, HostingDotComVCloudClient> createContext(
            String username, String password, Module... modules) {
      return new HostingDotComVCloudContextBuilder(new HostingDotComVCloudPropertiesBuilder(
               username, password).build()).withModules(modules).buildContext();
   }

   public static RestContext<HostingDotComVCloudAsyncClient, HostingDotComVCloudClient> createContext(
            Properties props, Module... modules) {
      return new HostingDotComVCloudContextBuilder(new HostingDotComVCloudPropertiesBuilder(props)
               .build()).withModules(modules).buildContext();
   }

}