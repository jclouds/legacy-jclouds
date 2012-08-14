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

package org.jclouds.imagemaker;

import java.util.List;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;

public interface PackageProcessor {

   public enum Type {
      CACHER, INSTALLER
   }

   /**
    * Returns the type of this package processor.
    * 
    * @return
    */
   public Type type();

   /**
    * Returns the name of the package type this processor handles (e.g. apt).
    * 
    * @return
    */
   public String name();

   /**
    * Whether this processor is compatible with {@link NodeMetadata}
    * 
    * @param node
    * @return
    */
   public boolean isCompatible(NodeMetadata node);

   /**
    * Executes this package processor on the node.
    * 
    * @param node
    */
   public ExecResponse process(NodeMetadata node, List<String> packages);

}
