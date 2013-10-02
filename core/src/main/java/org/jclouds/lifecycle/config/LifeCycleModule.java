/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.lifecycle.config;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static com.google.inject.matcher.Matchers.any;
import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_SCHEDULER_THREADS;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.jclouds.reflect.Reflection2.methods;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;

import org.jclouds.lifecycle.Closer;

import com.google.common.base.Predicate;
import com.google.common.reflect.Invokable;
import com.google.common.util.concurrent.ExecutionList;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * This associates java lifecycle annotations with guice hooks. For example, we invoke {@link PostConstruct} after
 * injection, and Associate {@link PreDestroy} with a global {@link Closer} object.
 * 
 * <h3>Important</h3> Make sure you create your injector with {@link Stage#PRODUCTION} and execute the bound
 * {@link ExecutionList} prior to using any other objects.
 * 
 * <p/>
 * Ex.
 * 
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
         @Named(PROPERTY_USER_THREADS)
         ListeningExecutorService userExecutor;
         @Inject
         @Named(PROPERTY_IO_WORKER_THREADS)
         ListeningExecutorService ioExecutor;
         // ScheduledExecutor is defined in an optional module
         @Inject(optional = true)
         @Named(PROPERTY_SCHEDULER_THREADS)
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

   private static final Predicate<Invokable<?, ?>> isPreDestroy = new Predicate<Invokable<?, ?>>() {
      public boolean apply(Invokable<?, ?> in) {
         return in.isAnnotationPresent(PreDestroy.class);
      }
   };

   private static final Predicate<Invokable<?, ?>> isPostConstruct = new Predicate<Invokable<?, ?>>() {
      public boolean apply(Invokable<?, ?> in) {
         return in.isAnnotationPresent(PostConstruct.class);
      }
   };

   protected void bindPostInjectionInvoke(final Closer closer, final ExecutionList list) {
      bindListener(any(), new TypeListener() {
         public <I> void hear(TypeLiteral<I> injectableType, TypeEncounter<I> encounter) {
            Collection<? extends Invokable<? super I, Object>> methods = methods(injectableType.getRawType());
            for (final Invokable<? super I, Object> method : filter(methods, isPostConstruct)) {
               encounter.register(new InjectionListener<I>() {
                  public void afterInjection(final I injectee) {
                     list.add(new Runnable() {
                        public void run() {
                           invokeOnInjectee(method, injectee);
                        }

                     }, sameThreadExecutor());
                  }
               });
            }
            for (final Invokable<? super I, Object> method : filter(methods, isPreDestroy)) {
               encounter.register(new InjectionListener<I>() {
                  public void afterInjection(final I injectee) {
                     closer.addToClose(new Closeable() {
                        public void close() throws IOException {
                           invokeOnInjectee(method, injectee);
                        }
                     });
                  }
               });
            }
         }

      });
   }

   private static <I> void invokeOnInjectee(Invokable<? super I, Object> method, I injectee) {
      try {
         method.invoke(injectee);
      } catch (InvocationTargetException ie) {
         throw propagate(ie.getTargetException());
      } catch (IllegalAccessException e) {
         throw propagate(e);
      }
   }
}
