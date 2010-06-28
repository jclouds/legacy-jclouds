package org.jclouds.aws.elb;

import java.util.List;
import java.util.Properties;

import org.jclouds.aws.elb.config.ELBRestClientModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Creates {@link ELBContext} or {@link Injector} instances based on the most commonly requested
 * arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see ELBContext
 */
public class ELBContextBuilder extends RestContextBuilder<ELBClient, ELBAsyncClient> {

   public ELBContextBuilder(Properties props) {
      super(ELBClient.class, ELBAsyncClient.class, props);
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new ELBRestClientModule());
   }
}
