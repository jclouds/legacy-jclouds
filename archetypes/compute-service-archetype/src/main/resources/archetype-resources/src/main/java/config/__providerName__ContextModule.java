#set( $ucaseProviderName = ${providerName.toUpperCase()} )
/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package ${package}.config;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.gson.*;
import ${package}.${providerName}AsyncClient;
import ${package}.${providerName}Client;
import org.jclouds.http.functions.config.ParserModule.DateAdapter;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import ${package}.${providerName};
import ${package}.reference.${providerName}Constants;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the ${providerName} connection, including logging and http transport.
 *
 * @author ${author}
 */
public class ${providerName}ContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DateAdapter.class).to(DateSecondsAdapter.class);
    }

    @Provides
    @Singleton
    RestContext<${providerName}AsyncClient, ${providerName}Client> provideContext(Closer closer, ${providerName}AsyncClient asyncApi,
                                                                ${providerName}Client syncApi, @${providerName} URI endPoint, @Named(${providerName}Constants.PROPERTY_${ucaseProviderName}_USER) String account) {
        return new RestContextImpl<${providerName}AsyncClient, ${providerName}Client>(closer, asyncApi, syncApi, endPoint, account);
    }

    @Singleton
    public static class DateSecondsAdapter implements DateAdapter {

        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTime());
        }

        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String toParse = json.getAsJsonPrimitive().getAsString();
            return new Date(Long.valueOf(toParse));
        }
    }
}