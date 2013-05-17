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
package org.jclouds.aws.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.ImmutableTable.Builder;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

/**
 * @author Adrian Cole
 */
public class BindTableToIndexedFormParams implements Binder {

   private final String rowPattern;
   private final String columnPattern;
   private final String valuePattern;

   protected BindTableToIndexedFormParams(String rowPattern, String columnPattern, String valuePattern) {
      this.rowPattern = rowPattern;
      this.columnPattern = columnPattern;
      this.valuePattern = valuePattern;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      if (checkNotNull(input, "input") instanceof Map) {
         Builder<Object, Object, Object> builder = ImmutableTable.builder();
         int index = 1;
         for (Map.Entry<?, ?> entry : ((Map<?, ?>) input).entrySet())
            builder.put(index++, entry.getKey(), entry.getValue());
         input = builder.build();
      }
      checkArgument(checkNotNull(input, "input") instanceof Table, "this binder is only valid for Table");
      Table<?, ?, ?> table = Table.class.cast(input);

      ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
      int amazonOneBasedIndex = 1; // according to docs, counters must start
                                   // with 1
      for (Cell<?, ?, ?> cell : table.cellSet()) {
         // not null by contract
         builder.put(format(rowPattern, amazonOneBasedIndex), cell.getRowKey().toString());
         builder.put(format(columnPattern, amazonOneBasedIndex), cell.getColumnKey().toString());
         builder.put(format(valuePattern, amazonOneBasedIndex), cell.getValue().toString());

         amazonOneBasedIndex++;
      }
      Multimap<String, String> forms = Multimaps.forMap(builder.build());
      return forms.size() == 0 ? request : (R) request.toBuilder().replaceFormParams(forms).build();
   }

}
