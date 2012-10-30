/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.lifecycle.config;

import static com.google.inject.matcher.Matchers.any;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.lifecycle.Closer;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ExecutionList;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * This associates java lifecycle annotations with guice hooks. For example, we invoke
 * {@link PostConstruct} after injection, and Associate {@link PreDestroy} with a global
 * {@link Closer} object.
 * 
 * <h3>Important</h3> Make sure you create your injector with {@link Stage#PRODUCTION} and execute
 * the bound {@link ExecutionList} prior to using any other objects.
 * 
 * <p/>
 * Ex.
 * <pre>
 * 
 * </pre>
 * 
 * @author Adrian Cole
 */
public class LifeCycleModule extends AbstractModule {

   protected void configure() {

      Closeable executorCloser = new Closeable() {
         @Inject
         @Named(Constants.PROPERTY_USER_THREADS)
         ExecutorService userExecutor;
         @Inject
         @Named(Constants.PROPERTY_IO_WORKER_THREADS)
         ExecutorService ioExecutor;
         // ScheduledExecutor is defined in an optional module
         @Inject(optional = true)
         @Named(Constants.PROPERTY_SCHEDULER_THREADS)
         ScheduledExecutorService scheduledExecutor;

         public void close() throws IOException {
            assert userExecutor != null;
            userExecutor.shutdownNow();
            assert ioExecutor != null;
            ioExecutor.shutdownNow();
            // ScheduledExecutor is defined in an optional module
            if (scheduledExecutor != null)
                scheduledExecutor.shutdownNow();
         }
      };

      binder().requestInjection(executorCloser);
      Closer closer = new Closer();
      closer.addToClose(executorCloser);
      bind(Closer.class).toInstance(closer);

      ExecutionList list = new ExecutionList();
      bindPostInjectionInvoke(closer, list);
      bind(ExecutionList.class).toInstance(list);
   }

   protected void bindPostInjectionInvoke(final Closer closer, final ExecutionList list) {
      bindListener(any(), new TypeListener() {
         public <I> void hear(TypeLiteral<I> injectableType, TypeEncounter<I> encounter) {
            Set<Method> methods = new HashSet<Method>();
            Class<? super I> type = injectableType.getRawType();
            while (type != null) {
               methods.addAll(Arrays.asList(type.getDeclaredMethods()));
               type = type.getSuperclass();
            }
            for (final Method method : methods) {
               invokePostConstructMethodAfterInjection(encounter, method);
               associatePreDestroyWithCloser(closer, encounter, method);
            }
         }

         private <I> void associatePreDestroyWithCloser(final Closer closer, TypeEncounter<I> encounter,
                  final Method method) {
            PreDestroy preDestroy = method.getAnnotation(PreDestroy.class);
            if (preDestroy != null) {
               encounter.register(new InjectionListener<I>() {
                  public void afterInjection(final I injectee) {
                     closer.addToClose(new Closeable() {
                        public void close() throws IOException {
                           try {
                              method.invoke(injectee);
                           } catch (InvocationTargetException ie) {
                              Throwable e = ie.getTargetException();
                              throw new IOException(e.getMessage());
                           } catch (IllegalAccessException e) {
                              throw new IOException(e.getMessage());
                           }
                        }
                     });

                  }
               });
            }
         }

         private <I> void invokePostConstructMethodAfterInjection(TypeEncounter<I> encounter, final Method method) {
            PostConstruct postConstruct = method.getAnnotation(PostConstruct.class);
            if (postConstruct != null) {
               encounter.register(new InjectionListener<I>() {
                  public void afterInjection(final I injectee) {
                     list.add(new Runnable() {
                        public void run() {
                           try {
                              method.invoke(injectee);
                           } catch (InvocationTargetException ie) {
                              Throwable e = ie.getTargetException();
                              throw Throwables.propagate(e);
                           } catch (IllegalAccessException e) {
                              throw Throwables.propagate(e);
                           }
                        }
                     }, MoreExecutors.sameThreadExecutor());
                  }
               });
            }
         }
      });
   }

}
