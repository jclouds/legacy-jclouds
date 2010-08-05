package org.jclouds.chef;

import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.jclouds.util.Utils.propagateAuthorizationOrOriginalException;

import java.util.Properties;

import javax.annotation.Nullable;

import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;

import com.google.inject.Module;

/**
 * Helper class to instantiate {@code ChefContext} instances.
 * 
 * @author Adrian Cole
 */
public class ChefContextFactory {

   private final RestContextFactory contextFactory;

   /**
    * Initializes with the default properties built-in to jclouds. This is
    * typically stored in the classpath resource {@code rest.properties}
    * 
    * @see RestContextFactory#getPropertiesFromResource
    */
   public ChefContextFactory() {
      this(new RestContextFactory());
   }

   /**
    * Finds definitions in the specified properties.
    */
   public ChefContextFactory(Properties properties) {
      this(new RestContextFactory(properties));
   }

   /**
    * 
    * Uses the supplied RestContextFactory to create {@link ChefContext}s
    */
   public ChefContextFactory(RestContextFactory restContextFactory) {
      this.contextFactory = restContextFactory;
   }

   public static <S, A> ChefContext buildContextUnwrappingExceptions(RestContextBuilder<S, A> builder) {
      try {
         return (ChefContext) builder.buildContext();
      } catch (Exception e) {
         return propagateAuthorizationOrOriginalException(e);
      }
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String)
    */
   public ChefContext createContext(String identity, String credential) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(contextFactory.createContextBuilder("chef",
            identity, credential));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(Properties)
    */
   public ChefContext createContext(Properties overrides) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(contextFactory.createContextBuilder("chef",
            overrides));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(Iterable)
    */
   public ChefContext createContext(Iterable<? extends Module> modules, Properties overrides) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(contextFactory.createContextBuilder("chef",
            modules, overrides));
      return buildContextUnwrappingExceptions(builder);

   }

   /**
    * @see RestContextFactory#createContextBuilder(String,String, Iterable)
    */
   public ChefContext createContext(@Nullable String identity, @Nullable String credential,
         Iterable<? extends Module> modules) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(contextFactory.createContextBuilder("chef",
            identity, credential, modules));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String,String, Iterable,
    *      Properties)
    */
   public ChefContext createContext(@Nullable String identity, @Nullable String credential,
         Iterable<? extends Module> modules, Properties overrides) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(contextFactory.createContextBuilder("chef",
            identity, credential, modules, overrides));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec)
    */
   public <S, A> ChefContext createContext(ContextSpec<S, A> contextSpec) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(createContextBuilder(contextSpec));
      return buildContextUnwrappingExceptions(builder);

   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec, Properties)
    */
   public <S, A> ChefContext createContext(ContextSpec<S, A> contextSpec, Properties overrides) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(createContextBuilder(contextSpec, overrides));
      return buildContextUnwrappingExceptions(builder);
   }

}