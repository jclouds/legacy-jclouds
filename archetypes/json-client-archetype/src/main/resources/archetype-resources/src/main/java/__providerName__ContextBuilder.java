#set( $ucaseProviderName = ${providerName.toUpperCase()} )
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package};

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Properties;

import org.jclouds.rest.RestContextBuilder;
import ${package}.${providerName}AsyncClient;
import ${package}.${providerName}Client;

import ${package}.config.${providerName}ContextModule;
import ${package}.config.${providerName}RestClientModule;
import ${package}.reference.${providerName}Constants;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author ${author}
 */
public class ${providerName}ContextBuilder extends RestContextBuilder<${providerName}AsyncClient, ${providerName}Client> {

   public ${providerName}ContextBuilder(String providerName, Properties props) {
      super(providerName, new TypeLiteral<${providerName}AsyncClient>() {
      }, new TypeLiteral<${providerName}Client>() {
      }, props);
      checkNotNull(properties.getProperty(${providerName}Constants.PROPERTY_${ucaseProviderName}_USER));
      checkNotNull(properties.getProperty(${providerName}Constants.PROPERTY_${ucaseProviderName}_PASSWORD));
   }

   protected void addClientModule(List<Module> modules) {
      modules.add(new ${providerName}RestClientModule());
   }

   @Override
   protected void addContextModule(String providerName, List<Module> modules) {
      modules.add(new ${providerName}ContextModule(providerName));
   }

}
