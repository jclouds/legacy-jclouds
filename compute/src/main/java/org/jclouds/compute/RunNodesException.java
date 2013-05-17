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
package org.jclouds.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.createExecutionErrorMessage;
import static org.jclouds.compute.util.ComputeServiceUtils.createNodeErrorMessage;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;

/**
 * 
 * @author Adrian Cole
 */
public class RunNodesException extends Exception {

   private final String group;
   private final int count;
   private final Template template;
   private final Set<? extends NodeMetadata> successfulNodes;
   private final Map<? extends NodeMetadata, ? extends Throwable> failedNodes;
   private final Map<?, Exception> executionExceptions;

   public RunNodesException(String group, int count, Template template,
            Set<? extends NodeMetadata> successfulNodes, Map<?, Exception> executionExceptions,
            Map<? extends NodeMetadata, ? extends Throwable> failedNodes) {
      super(
               String
                        .format(
                                 "error running %d node%s group(%s) location(%s) image(%s) size(%s) options(%s)%n%s%n%s",
                                 count, count > 1 ? "s" : "", group, template.getLocation().getId(),
                                 template.getImage().getProviderId(), template.getHardware()
                                          .getProviderId(), template.getOptions(),
                                 createExecutionErrorMessage(executionExceptions),
                                 createNodeErrorMessage(failedNodes)));
      this.group = group;
      this.count = count;
      this.template = template;
      this.successfulNodes = successfulNodes;
      this.failedNodes = failedNodes;
      this.executionExceptions = executionExceptions;
   }

   /**
    * 
    * @return Nodes that performed startup without error
    */
   public Set<? extends NodeMetadata> getSuccessfulNodes() {
      return successfulNodes;
   }

   /**
    * 
    * @return Nodes that performed startup without error, but incurred problems applying options
    */
   public Map<?, ? extends Throwable> getExecutionErrors() {
      return executionExceptions;
   }

   /**
    * 
    * @return Nodes that performed startup without error, but incurred problems applying options
    */
   public Map<? extends NodeMetadata, ? extends Throwable> getNodeErrors() {
      return failedNodes;
   }

   public String getGroup() {
      return group;
   }

   public int getCount() {
      return count;
   }

   public Template getTemplate() {
      return template;
   }

}
