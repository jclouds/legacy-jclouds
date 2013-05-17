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
package org.jclouds.azureblob.predicates.validators;

import javax.inject.Inject;

import org.jclouds.predicates.validators.DnsNameValidator;

import com.google.inject.Singleton;

/**
 * Validates name for Azure container. The complete requirements are listed at:
 * http://weblogs.asp.net
 * /vblasberg/archive/2009/02/17/azure-details-and-limitations-blobs-tables-and-queues.aspx
 * 
 * @see org.jclouds.rest.InputParamValidator
 * @see org.jclouds.predicates.Validator
 * 
 * @author Oleksiy Yarmula
 */
@Singleton
public class ContainerNameValidator extends DnsNameValidator {

   @Inject
   ContainerNameValidator() {
      super(3,63);
   }

   public void validate(String containerName) {
      super.validate(containerName);
      if (containerName.contains("--"))
         throw exception(containerName, "Every dash must be followed by letter or number");
      if (containerName.endsWith("-"))
         throw exception(containerName, "Shouldn't end with a dash");
   }

   @Override
   protected IllegalArgumentException exception(String containerName, String reason) {
      return new IllegalArgumentException(
               String
                        .format(
                                 "Object '%s' doesn't match Azure container naming convention. "
                                          + "Reason: %s. For more info, please refer to http://weblogs.asp.net/vblasberg/archive/2009/02/17/"
                                          + "azure-details-and-limitations-blobs-tables-and-queues.aspx.",
                                 containerName, reason));
   }

}
