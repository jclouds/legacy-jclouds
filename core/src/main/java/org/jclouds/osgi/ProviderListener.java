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
package org.jclouds.osgi;

import org.jclouds.providers.ProviderMetadata;

/**
 * A listener interface for {@link ProviderMetadata}. In OSGi a provider can be added or removed dynamically. OSGi
 * services using this interface will receive a notification whenever this happens.
 */
public interface ProviderListener {

   /**
    * Method to be called when a Provider gets added.
    * 
    * @param provider
    *           The provider that was added.
    * @param <P>
    *           The {@link ProviderMetadata}.
    */
   <P extends ProviderMetadata> void added(P provider);

   /**
    * Method to be called when a Provider gets removed.
    * 
    * @param provider
    *           The provider that was added.
    * @param <P>
    *           The {@link ProviderMetadata}.
    */
   <P extends ProviderMetadata> void removed(P provider);

}
