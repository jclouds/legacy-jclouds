/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
package org.jclouds.logging.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * TypeListener that will bind {@link org.jclouds.logging.Logger} to members annotated with
 * {@link javax.annotation.Resource}
 * 
 * This class is a TypeListener so that it can create a logger whose category is
 * the same as the name of the injected instance's class.
 * 
 * Note that this occurs post-object construction through
 * {@link com.google.inject.Binder#bindListener}.
 * 
 * Here's an example usage:
 *  <pre>
 *     class A {
 *         @Resource private Logger logger = Logger.NULL; 
 *     }
 * 
 *     Injector i = Guice.createInjector(new AbstractModule() {
 *         @Override protected void configure() { 
 *             bindListener(any(), new
 *                 BindLoggersAnnotatedWithResource( new
 *                     JDKLogger.JDKLoggerFactory()));
 *         }
 *     });
 * 
 *     A = i.getInstance(A.class); 
 *     // A will now have a logger associated with it
 * </pre>
 * 
 * @author Adrian Cole
 * 
 */
public class BindLoggersAnnotatedWithResource implements TypeListener {

    static class AssignLoggerToField<I> implements InjectionListener<I> {
	private final Logger logger;
	private final Field field;

	AssignLoggerToField(Logger logger, Field field) {
	    this.logger = logger;
	    this.field = field;
	}

	public void afterInjection(I injectee) {
	    try {
		field.setAccessible(true);
		field.set(injectee, logger);
	    } catch (IllegalAccessException e) {
		throw new ProvisionException(e.getMessage(), e);
	    }
	}
    }

    static class LoggerFieldsAnnotatedWithResource implements
	    Function<Field, Field> {
	public Field apply(Field from) {
	    Annotation inject = from.getAnnotation(Resource.class);
	    if (inject != null && from.getType().isAssignableFrom(Logger.class)) {
		return from;
	    }
	    return null;
	}
    }

    private final LoggerFactory loggerFactory;

    @Inject
    public BindLoggersAnnotatedWithResource(LoggerFactory loggerFactory) {
	this.loggerFactory = loggerFactory;
    }

    public <I> void hear(TypeLiteral<I> injectableType,
	    TypeEncounter<I> encounter) {

	Class<? super I> type = injectableType.getRawType();
	Set<Field> loggerFields = getLoggerFieldsAnnotatedWithResource(type);
	if (loggerFields.size() == 0)
	    return;

	Logger logger = loggerFactory.getLogger(type.getName());

	for (Field field : loggerFields) {
	    encounter.register(new AssignLoggerToField<I>(logger, field));
	}
    }

    @VisibleForTesting
    Set<Field> getLoggerFieldsAnnotatedWithResource(Class<?> declaredType) {
	Set<Field> fields = new HashSet<Field>();
	Class<?> type = declaredType;
	while (type != null) {
	    fields.addAll(Arrays.asList(type.getDeclaredFields()));
	    type = type.getSuperclass();
	}
	Set<Field> loggerFields = Sets.newHashSet(Iterables.transform(fields,
		new LoggerFieldsAnnotatedWithResource()));
	loggerFields.remove(null);
	return loggerFields;
    }
}