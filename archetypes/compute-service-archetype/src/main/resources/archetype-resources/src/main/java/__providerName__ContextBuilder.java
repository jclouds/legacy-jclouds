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


import java.util.List;
import java.util.Properties;

import com.google.inject.Key;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextBuilder;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import ${package}.compute.config.${providerName}ComputeServiceContextModule;
import ${package}.config.${providerName}RestClientModule;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * @author ${author}
 */
public class ${providerName}ContextBuilder extends ComputeServiceContextBuilder<${providerName}Client, ${providerName}AsyncClient> {

    public ${providerName}ContextBuilder(String providerName, Properties props) {
        super(providerName, new TypeLiteral<${providerName}AsyncClient>() {}, 
                new TypeLiteral<${providerName}Client>() {}, 
                props);
    }

    protected void addClientModule(List<Module> modules) {
        modules.add(new ${providerName}RestClientModule());
    }

    @Override
    protected void addContextModule(String providerName, List<Module> modules) {
        modules.add(new ${providerName}ComputeServiceContextModule(providerName));
    }

}
