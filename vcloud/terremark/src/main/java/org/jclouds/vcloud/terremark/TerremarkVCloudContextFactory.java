package org.jclouds.vcloud.terremark;

import java.util.Properties;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.RestContext;

import com.google.inject.Module;

/**
 * Creates {@link TerremarkVCloudContext} instances based on the most commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see TerremarkVCloudContext
 */
public class TerremarkVCloudContextFactory {

   public static RestContext<TerremarkVCloudAsyncClient, TerremarkVCloudClient> createContext(
            String username, String password, Module... modules) {
      return new TerremarkVCloudContextBuilder(new TerremarkVCloudPropertiesBuilder(username,
               password).build()).withModules(modules).buildContext();
   }

   public static RestContext<TerremarkVCloudAsyncClient, TerremarkVCloudClient> createContext(
            Properties props, Module... modules) {
      return new TerremarkVCloudContextBuilder(new TerremarkVCloudPropertiesBuilder(props).build())
               .withModules(modules).buildContext();
   }

}
