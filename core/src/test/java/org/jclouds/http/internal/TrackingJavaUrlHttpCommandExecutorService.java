package org.jclouds.http.internal;

import java.lang.reflect.Method;
import java.util.Collection;
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
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
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
   
   public static Method getJavaMethodForRequestAtIndex(final Collection<HttpCommand> commandsInvoked, int index) {
      return getJavaMethodForRequest(Iterables.get(commandsInvoked, index));
   }

   public static Method getJavaMethodForRequest(HttpCommand commandInvoked) {
      return GeneratedHttpRequest.class.cast(commandInvoked.getCurrentRequest()).getJavaMethod();
   }

   @SuppressWarnings("unchecked")
   public static List<Object> getJavaArgsForRequestAtIndex(final Collection<HttpCommand> commandsInvoked, int index) {
      return GeneratedHttpRequest.class.cast(Iterables.get(commandsInvoked, index).getCurrentRequest()).getArgs();
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