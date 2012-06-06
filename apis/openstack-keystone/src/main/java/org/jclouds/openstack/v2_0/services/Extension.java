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
package org.jclouds.openstack.v2_0.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.jclouds.openstack.v2_0.ServiceType;

/**
 * An extension of a {@link ServiceType service}. In order for us to understand
 * the context of the extension, we must consider the <a href=
 * "http://docs.openstack.org/api/openstack-compute/2/content/Extensions-d1e1444.html"
 * >extensions call</a>.
 * 
 * <br/>
 * For our purposes, the minimal context of an extension is the type of the
 * service it extends ex. {@link ServiceType#COMPUTE}, and its namespace ex. <a
 * href
 * ="http://docs.openstack.org/ext/keypairs/api/v1.1">http://docs.openstack.org
 * /ext/keypairs/api/v1.1</a>.
 * 
 * @author Adrian Cole
 * 
 * @see ServiceType
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/2/content/Extensions-d1e1444.html"
 *      />
 * @see <a href="http://nova.openstack.org/api_ext" />
 * @see <a href="http://nova.openstack.org/api_ext/ext_keypairs.html" />
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Qualifier
public @interface Extension {

   /**
    * the service type this is an extension of.
    * 
    * <h3>note</h3>
    * 
    * This isn't necessarily one of the built-in {@link ServiceType services},
    * it could be an extension of a custom service.
    * 
    * @return the service type this is an extension of.
    * 
    */
   String of();

   /**
    * namespace ex. <a href
    * ="http://docs.openstack.org/ext/keypairs/api/v1.1">http
    * ://docs.openstack.org /ext/keypairs/api/v1.1</a>.
    * 
    * @return the namespace of the extension
    */
   String namespace();

}