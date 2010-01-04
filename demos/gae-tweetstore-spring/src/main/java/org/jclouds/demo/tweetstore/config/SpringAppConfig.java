package org.jclouds.demo.tweetstore.config;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_BLOBSTORE_CONTEXTBUILDERS;
import static org.jclouds.demo.tweetstore.reference.TweetStoreConstants.PROPERTY_TWEETSTORE_CONTAINER;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.gae.config.GaeHttpCommandExecutorServiceModule;
import org.jclouds.twitter.TwitterClient;
import org.jclouds.twitter.TwitterContextFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * Reads properties and creates resources for servlets.
 * 
 * @author Andrew Phillips
 * @see SpringServletConfig
 */
@Configuration
@Singleton
public class SpringAppConfig implements ResourceLoaderAware {
   /*
    * The call to TwitterContextFactory.createContext in initialize() must be carried out before the
    * servlet context loads, otherwise the GAE will throw an access exception. For this reason, this
    * code cannot be in the default servlet context loaded by the DispatcherServlet, but is executed
    * in the root application context (which is processed by a listener).
    */

   private final Properties props = new Properties();

   Map<String, BlobStoreContext<?, ?>> providerTypeToBlobStoreMap;
   TwitterClient twitterClient;
   String container;

   private boolean initializing;

   @SuppressWarnings("unchecked")
   @PostConstruct
   public void initialize() {
      if (initializing)
         return;
      initializing = true;
      // shared across all blobstores and used to retrieve tweets
      twitterClient = TwitterContextFactory.createContext(props,
               new GaeHttpCommandExecutorServiceModule()).getApi();

      // common namespace for storing tweets.
      container = checkNotNull(props.getProperty(PROPERTY_TWEETSTORE_CONTAINER),
               PROPERTY_TWEETSTORE_CONTAINER);
      ImmutableList<String> contextBuilderClassNames = ImmutableList.<String> of(checkNotNull(
               props.getProperty(PROPERTY_BLOBSTORE_CONTEXTBUILDERS),
               PROPERTY_BLOBSTORE_CONTEXTBUILDERS).split(","));

      // instantiate and store references to all blobstores by provider name
      providerTypeToBlobStoreMap = Maps.newHashMap();
      for (String className : contextBuilderClassNames) {
         Class<BlobStoreContextBuilder<?, ?>> builderClass;
         Constructor<BlobStoreContextBuilder<?, ?>> constructor;
         String name;
         BlobStoreContext<?, ?> context;
         try {
            builderClass = (Class<BlobStoreContextBuilder<?, ?>>) Class.forName(className);
            name = builderClass.getSimpleName().replaceAll("BlobStoreContextBuilder", "");
            constructor = builderClass.getConstructor(Properties.class);
            context = constructor.newInstance(props).withModules(
                     new GaeHttpCommandExecutorServiceModule()).buildContext();
         } catch (Exception e) {
            throw new RuntimeException("error instantiating " + className, e);
         }
         providerTypeToBlobStoreMap.put(name, context);
      }

      // get a queue for submitting store tweet requests
      Queue queue = QueueFactory.getQueue("twitter");
      // submit a job to store tweets for each configured blobstore
      for (String name : providerTypeToBlobStoreMap.keySet()) {
         queue.add(url("/store/do").header("context", name).method(Method.GET));
      }
   }

   @PreDestroy
   public void destroy() {
      for (BlobStoreContext<?, ?> context : providerTypeToBlobStoreMap.values()) {
         context.close();
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org
    * .springframework.core.io.ResourceLoader)
    */
   @Override
   public void setResourceLoader(ResourceLoader resourceLoader) {
      InputStream input = null;
      try {
         input = resourceLoader.getResource("/WEB-INF/jclouds.properties").getInputStream();
         props.load(input);
         if (!initializing)
            initialize();
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         Closeables.closeQuietly(input);
      }
   }
}
