/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.compute;

import java.util.Map;

import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;

/**
 * A means of specifying the interface between the {@link ComputeServices} and a concrete compute
 * cloud implementation, jclouds or otherwise.
 * 
 * @author Adrian Cole
 * 
 */
public interface ComputeServiceAdapter<N, H, I, L> {
   N createNodeAndStoreCredentials(String tag, String name, Template template, Map<String, Credentials> credentialStore);

   Iterable<H> listHardware();

   Iterable<I> listImages();

   Iterable<L> listLocations();

   N getNode(String id);

   void destroyNode(String id);

   void rebootNode(String id);

   Iterable<N> listNodes();

}