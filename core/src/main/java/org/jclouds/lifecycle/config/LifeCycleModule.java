/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
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
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jclouds.lifecycle.Closer;

import com.google.inject.AbstractModule;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class LifeCycleModule extends AbstractModule {

    protected void configure() {
	final ExecutorService executor = Executors.newCachedThreadPool();
	bind(ExecutorService.class).toInstance(executor);
	Closer closer = new Closer();
	closer.addToClose(new Closeable() {
	    public void close() throws IOException {
		executor.shutdownNow();
	    }
	});
	bind(Closer.class).toInstance(closer);
	bindPostInjectionInvoke(closer);
    }

    protected void bindPostInjectionInvoke(final Closer closer) {
	bindListener(any(), new TypeListener() {
	    public <I> void hear(TypeLiteral<I> injectableType,
		    TypeEncounter<I> encounter) {
		Set<Method> methods = new HashSet<Method>();
		Class<? super I> type = injectableType.getRawType();
		while (type != null) {
		    methods.addAll(Arrays.asList(type.getDeclaredMethods()));
		    type = type.getSuperclass();
		}
		for (final Method method : methods) {
		    PostConstruct postConstruct = method
			    .getAnnotation(PostConstruct.class);
		    if (postConstruct != null) {
			encounter.register(new InjectionListener<I>() {
			    public void afterInjection(I injectee) {
				try {
				    method.invoke(injectee);
				} catch (InvocationTargetException ie) {
				    Throwable e = ie.getTargetException();
				    throw new ProvisionException(
					    e.getMessage(), e);
				} catch (IllegalAccessException e) {
				    throw new ProvisionException(
					    e.getMessage(), e);
				}
			    }
			});
		    }

		    PreDestroy preDestroy = method
			    .getAnnotation(PreDestroy.class);
		    if (preDestroy != null) {
			encounter.register(new InjectionListener<I>() {
			    public void afterInjection(final I injectee) {
				closer.addToClose(new Closeable() {
				    public void close() throws IOException {
					try {
					    method.invoke(injectee);
					} catch (InvocationTargetException ie) {
					    Throwable e = ie
						    .getTargetException();
					    throw new IOException(e
						    .getMessage());
					} catch (IllegalAccessException e) {
					    throw new IOException(e
						    .getMessage());
					}
				    }
				});

			    }
			});
		    }
		}
	    }
	});
    }

}
