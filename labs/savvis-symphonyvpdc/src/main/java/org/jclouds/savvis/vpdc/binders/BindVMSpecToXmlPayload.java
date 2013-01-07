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
package org.jclouds.savvis.vpdc.binders;

import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.savvis.vpdc.domain.VMSpec;

import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindVMSpecToXmlPayload extends BaseBindVMSpecToXmlPayload<VMSpec> {

   protected VMSpec findSpecInArgsOrNull(GeneratedHttpRequest gRequest) {
      for (Object arg : gRequest.getInvocation().getArgs()) {
         if (arg instanceof VMSpec) {
            return VMSpec.class.cast(arg);
         }
      }
      throw new IllegalArgumentException("Iterable<VMSpec> must be included in the argument list");
   }

   @Override
   protected void bindSpec(VMSpec spec, XMLBuilder rootBuilder) throws ParserConfigurationException,
            FactoryConfigurationError {
      checkSpec(spec);
      rootBuilder.a("name", spec.getName()).a("type", "application/vnd.vmware.vcloud.vApp+xml").a("href", "");
      addOperatingSystemAndVirtualHardware(spec, rootBuilder);
   }

}
