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
package org.jclouds.ovf.xml;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.ovf.Envelope;
import org.jclouds.ovf.VirtualSystem;
import org.jclouds.ovf.Envelope.Builder;
import org.jclouds.ovf.xml.internal.BaseEnvelopeHandler;

/**
 * @author Adrian Cole
 */
public class EnvelopeHandler extends
         BaseEnvelopeHandler<VirtualSystem, VirtualSystem.Builder, VirtualSystemHandler, Envelope, Envelope.Builder> {

   @Inject
   public EnvelopeHandler(DiskSectionHandler diskHandler, NetworkSectionHandler networkHandler,
            VirtualSystemHandler virtualSystemHandler, Provider<Builder> envelopeBuilderProvider) {
      super(diskHandler, networkHandler, virtualSystemHandler, envelopeBuilderProvider);
   }

}
