#set( $ucaseClientName = ${clientName.toUpperCase()} )
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import ${package}.config.${clientName}ContextModule;
import ${package}.config.${clientName}RestClientModule;
import ${package}.reference.${clientName}Constants;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author ${author}
 */
public class ${clientName}ContextBuilder extends RestContextBuilder<${clientName}Client> {

   public ${clientName}ContextBuilder(Properties props) {
      super(new TypeLiteral<${clientName}Client>() {
      }, props);
      checkNotNull(properties.getProperty(${clientName}Constants.PROPERTY_${ucaseClientName}_USER));
      checkNotNull(properties.getProperty(${clientName}Constants.PROPERTY_${ucaseClientName}_PASSWORD));
   }

   protected void addClientModule(List<Module> modules) {
      modules.add(new ${clientName}RestClientModule());
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new ${clientName}ContextModule());
   }

}
