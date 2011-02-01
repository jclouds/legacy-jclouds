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

package org.jclouds.compute.strategy;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;

/**
 * Adds a node into an existing tag set, or creates one new.
 * 
 * @author Adrian Cole
 */
public interface CreateNodeWithGroupEncodedIntoName {

   /**
    * create a node given the name and template parameters such as imageid, hardwareid, and
    * locationid.
    * 
    * @param group
    *           group name supplied by the user
    * @param name
    *           supplied by {@link CreateNodesInGroupThenAddToSet } and must have the tag encoded into
    *           it.
    * @param template
    *           supplied by the user
    * @return NodeMetadata from the new object, most likely in some pending state.
    */
   NodeMetadata createNodeWithGroupEncodedIntoName(String group, String name, Template template);

}