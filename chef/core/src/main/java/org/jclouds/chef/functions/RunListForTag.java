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

package org.jclouds.chef.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.chef.reference.ChefConstants.CHEF_BOOTSTRAP_DATABAG;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.ChefClient;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.json.Json;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * 
 * Retrieves the run-list for a specific tag
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class RunListForTag implements Function<String, List<String>> {
   public static final Type RUN_LIST_TYPE = new TypeLiteral<Map<String, List<String>>>() {
   }.getType();
   private final ChefClient client;
   private final Json json;
   private final String databag;

   @Inject
   public RunListForTag(@Named(CHEF_BOOTSTRAP_DATABAG) String databag, ChefClient client, Json json) {
      this.databag = checkNotNull(databag, "databag");
      this.client = checkNotNull(client, "client");
      this.json = checkNotNull(json, "json");
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<String> apply(String from) {
      DatabagItem list = client.getDatabagItem(databag, from);
      checkState(list != null, "databag item %s/%s not found", databag, from);
      return ((Map<String, List<String>>) json.fromJson(list.toString(), RUN_LIST_TYPE)).get("run_list");
   }

}