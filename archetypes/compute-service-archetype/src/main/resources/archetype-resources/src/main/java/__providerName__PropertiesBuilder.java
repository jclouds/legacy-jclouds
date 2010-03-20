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
package ${package};

import org.jclouds.PropertiesBuilder;

import static com.google.common.base.Preconditions.checkNotNull;
import static ${package}.reference.${providerName}Constants.*;

import java.net.URI;
import java.util.Properties;

/**
 * Builds properties used in ${providerName} Clients
 * 
 * @author ${author}
 *
 */
public class ${providerName}PropertiesBuilder extends PropertiesBuilder {
    @Override
    protected Properties defaultProperties() {
        Properties properties = super.defaultProperties();
        properties.setProperty(PROPERTY_${ucaseProviderName}_ENDPOINT, "${providerEndpoint}");
        properties.setProperty(PROPERTY_${ucaseProviderName}_SESSIONINTERVAL, 8 * 60 + "");
        return properties;
    }

    public ${providerName}PropertiesBuilder(Properties properties) {
        super(properties);
    }

    public ${providerName}PropertiesBuilder(URI endpoint, String id, String secret) {
        super();
        withCredentials(id, secret);
        withEndpoint(endpoint);
    }

    public ${providerName}PropertiesBuilder withTokenExpiration(long seconds) {
        properties.setProperty(PROPERTY_${ucaseProviderName}_SESSIONINTERVAL, seconds + "");
        return this;
    }

    public ${providerName}PropertiesBuilder withCredentials(String id, String secret) {
        properties.setProperty(PROPERTY_${ucaseProviderName}_USER, checkNotNull(id, "user"));
        properties.setProperty(PROPERTY_${ucaseProviderName}_KEY, checkNotNull(secret, "key"));
        return this;
    }

    public ${providerName}PropertiesBuilder withEndpoint(URI endpoint) {
        properties.setProperty(PROPERTY_${ucaseProviderName}_ENDPOINT, 
                checkNotNull(endpoint, "endpoint").toString());
        return this;
    }
}
