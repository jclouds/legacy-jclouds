#set( $lcaseProviderName = ${providerName.toLowerCase()} )
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
package ${package}.reference;

/**
 * Configuration properties and constants used in ${providerName} connections.
 *
 * @author Adrian Cole
 */
public interface ${providerName}Constants {
    public static final String PROPERTY_${ucaseProviderName}_ENDPOINT = "jclouds.${lcaseProviderName}.endpoint";
    public static final String PROPERTY_${ucaseProviderName}_USER = "jclouds.${lcaseProviderName}.user";
    public static final String PROPERTY_${ucaseProviderName}_KEY = "jclouds.${lcaseProviderName}.key";
    /**
     * how long do we wait before obtaining a new timestamp for requests.
     */
    public static final String PROPERTY_${ucaseProviderName}_SESSIONINTERVAL = "jclouds.${lcaseProviderName}.sessioninterval";

}
