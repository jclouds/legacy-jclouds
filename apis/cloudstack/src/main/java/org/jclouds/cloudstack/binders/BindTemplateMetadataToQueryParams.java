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
package org.jclouds.cloudstack.binders;

import static com.google.common.base.Preconditions.checkArgument;

import org.jclouds.cloudstack.domain.TemplateMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;

/**
 * @author Richard Downer
 */
public class BindTemplateMetadataToQueryParams implements Binder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(input instanceof TemplateMetadata, "this binder is only valid for TemplateMetadata");
      TemplateMetadata metadata = (TemplateMetadata) input;
      Builder<String, String> builder = ImmutableMultimap.<String, String>builder();
      builder.put("name", metadata.getName());
      builder.put("ostypeid", metadata.getOsTypeId());
      builder.put("displaytext", metadata.getDisplayText());
      if (metadata.getSnapshotId() != null) {
	      builder.put("snapshotid", metadata.getSnapshotId());
      }
      if (metadata.getVolumeId() != null) {
	      builder.put("volumeid", metadata.getVolumeId());
      }
      if (metadata.getVirtualMachineId() != null) {
	      builder.put("virtualmachineid", metadata.getVirtualMachineId());
      }
      if (metadata.isPasswordEnabled() != null) {
	      builder.put("passwordenabled", metadata.isPasswordEnabled().toString());
      }
      return (R) request.toBuilder().replaceQueryParams(builder.build()).build();
   }
}
