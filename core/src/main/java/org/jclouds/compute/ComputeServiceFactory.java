package org.jclouds.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Properties;

import javax.inject.Inject;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpPropertiesBuilder;
import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class ComputeServiceFactory {
   private final Properties properties;

   @Inject
   public ComputeServiceFactory(Properties properties) {
      this.properties = properties;
   }

   public ComputeService create(URI provider, Module... modules) {
      return create(provider, Credentials.parse(provider), modules);
   }

   @SuppressWarnings("unchecked")
   public ComputeService create(URI provider, Credentials creds, Module... modules) {
      String hint = checkNotNull(provider.getHost(), "host");
      String account = checkNotNull(creds.account, "account");
      String key = creds.key;
      String propertiesBuilderKey = String.format("%s.propertiesbuilder", hint);
      String propertiesBuilderClassName = checkNotNull(
               properties.getProperty(propertiesBuilderKey), propertiesBuilderKey);

      String contextBuilderKey = String.format("%s.contextbuilder", hint);
      String contextBuilderClassName = checkNotNull(properties.getProperty(contextBuilderKey),
               contextBuilderKey);

      try {
         Class<HttpPropertiesBuilder> propertiesBuilderClass = (Class<HttpPropertiesBuilder>) Class
                  .forName(propertiesBuilderClassName);
         Class<RestContextBuilder<?, ?>> contextBuilderClass = (Class<RestContextBuilder<?, ?>>) Class
                  .forName(contextBuilderClassName);

         HttpPropertiesBuilder builder = propertiesBuilderClass.getConstructor(String.class,
                  String.class).newInstance(account, key);
         return contextBuilderClass.getConstructor(Properties.class).newInstance(builder.build())
                  .withModules(modules).buildInjector().getInstance(ComputeService.class);
      } catch (Exception e) {
         throw new RuntimeException("error instantiating " + contextBuilderClassName, e);
      }
   }
}
