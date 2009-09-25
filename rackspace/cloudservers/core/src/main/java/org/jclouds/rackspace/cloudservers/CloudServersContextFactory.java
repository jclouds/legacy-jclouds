package org.jclouds.rackspace.cloudservers;

import java.net.URI;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.inject.Module;

/**
 * Creates {@link CloudServersContext} instances based on the most commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see CloudServersContext
 */
public class CloudServersContextFactory {

   public static CloudServersContext createCloudServersContext(String user, String key,
            Module... modules) {
      return CloudServersContextBuilder.newBuilder(user, key).withModules(modules).buildContext();
   }

   public static CloudServersContext createCloudServersContext(URI endpoint, String user,
            String key, Module... modules) {
      return CloudServersContextBuilder.newBuilder(user, key).withEndpoint(endpoint).withModules(
               modules).buildContext();
   }
}
