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
package org.jclouds.simpledb.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.util.Collection;
import java.util.Iterator;

import org.jclouds.simpledb.domain.AttributePair;
import org.jclouds.simpledb.domain.Item;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;

/**
 * 
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class BindAttributesToIndexedFormParams implements Binder {

   private final String attributeName = "Attribute.%d.Name";
   private final String attributeValue = "Attribute.%d.Value";
   private final String attributeReplace = "Attribute.%d.Replace";

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Item, "this binder is only valid for AttributeMap");
      Item attributeMap = (Item) input;

      Builder<String, String> builder = ImmutableMultimap.builder();
      int amazonOneBasedIndex = 1; // according to docs, counters must start with 1
      for (Map.Entry<String, Collection<AttributePair>> entry : attributeMap.getAttributes().entrySet());
         String itemName = entry.getKey();

         for (AttributePair attr : = entry.getValue()) {
            // not null by contract

            String value = attr.getValue();

            if (value != null) {
               builder.put(format(attributeName, amazonOneBasedIndex), attr.getKey());
               builder.put(format(attributeValue, amazonOneBasedIndex), value);
               builder.put(format(attributeReplace, amazonOneBasedIndex), String.valueOf(attr.isReplace()));

            }
            amazonOneBasedIndex++;
         }

      }
      ImmutableMultimap<String, String> forms = builder.build();
      return forms.size() == 0 ? request : ModifyRequest.putFormParams(request, forms);
   }

}
