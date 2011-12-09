package org.jclouds.http.internal;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.jclouds.Constants;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Records all http commands submitted, storing them in the given List<HttpCommand>.
 * 
 * @author Adrian Cole
 */
@Singleton
public class TrackingJavaUrlHttpCommandExecutorService extends JavaUrlHttpCommandExecutorService {

   private final List<HttpCommand> commandsInvoked;

   /**
    * Creates a guice module, which will bind in TrackingJavaUrlHttpCommandExecutorService and also bind
    * the given list so that it is used by the tracker.
    */
   public static Module newTrackingModule(final List<HttpCommand> commandsInvoked) {
      return new AbstractModule() {

         @Override
         protected void configure() {
            bind(JavaUrlHttpCommandExecutorService.class).to(
                     TrackingJavaUrlHttpCommandExecutorService.class);
            bind(new TypeLiteral<List<HttpCommand>>() {
            }).toInstance(commandsInvoked);
         }
      };
   }
   
   @Inject
   public TrackingJavaUrlHttpCommandExecutorService(HttpUtils utils,
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioWorkerExecutor,
            DelegatingRetryHandler retryHandler, IOExceptionRetryHandler ioRetryHandler,
            DelegatingErrorHandler errorHandler, HttpWire wire, @Named("untrusted") HostnameVerifier verifier,
            @Named("untrusted") Supplier<SSLContext> untrustedSSLContextProvider, List<HttpCommand> commandsInvoked)
            throws SecurityException, NoSuchFieldException {
      super(utils, ioWorkerExecutor, retryHandler, ioRetryHandler, errorHandler, wire, verifier,
               untrustedSSLContextProvider);
      this.commandsInvoked = commandsInvoked;
   }

   @Override
   public Future<HttpResponse> submit(HttpCommand command) {
      commandsInvoked.add(command);
      return super.submit(command);
   }

}