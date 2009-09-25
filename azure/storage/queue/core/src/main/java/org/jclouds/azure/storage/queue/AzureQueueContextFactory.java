package org.jclouds.azure.storage.queue;

import java.net.URI;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.inject.Module;

/**
 * Creates {@link AzureQueueContext} instances based on the most commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see AzureQueueContext
 */
public class AzureQueueContextFactory {

   public static AzureQueueContext createAzureQueueContext(String account, String encodedKey,
            Module... modules) {
      return AzureQueueContextBuilder.newBuilder(account, encodedKey).withModules(modules)
               .buildContext();
   }

   public static AzureQueueContext createAzureQueueContext(URI endpoint, String account,
            String encodedKey, Module... modules) {
      return AzureQueueContextBuilder.newBuilder(account, encodedKey).withEndpoint(endpoint)
               .withModules(modules).buildContext();
   }
}
