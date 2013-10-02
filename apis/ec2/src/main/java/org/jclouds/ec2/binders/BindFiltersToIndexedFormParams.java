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
package org.jclouds.ec2.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.Multimap;

/**
 * Binds the Multimap to form parameters for filtering.
 * 
 * <pre>
 * https://ec2.amazonaws.com/?Action=DescribeTags
 * &Filter.1.Name=resource-type
 * &Filter.1.Value.1=instance
 * &Filter.2.Name=key
 * &Filter.2.Value.1=stack
 * &Filter.3.Name=value
 * &Filter.3.Value.1=Test
 * &Filter.3.Value.2=Production
 * &AUTHPARAMS
 * </pre>
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindFiltersToIndexedFormParams implements Binder {
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Multimap, "this binder is only valid for Multimap");
      @SuppressWarnings("unchecked")
      Multimap<String, String> filters = (Multimap<String, String>) input;
      return AWSUtils.indexMultimapToFormValuesWithPrefix(request, "Filter", "Name", "Value", filters);
   }

}
