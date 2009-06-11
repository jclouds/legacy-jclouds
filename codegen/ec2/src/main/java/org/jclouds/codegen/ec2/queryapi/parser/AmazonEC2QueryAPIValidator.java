/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.codegen.ec2.queryapi.parser;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Set;

import org.jclouds.codegen.ec2.queryapi.AmazonEC2QueryAPI;
import org.jclouds.codegen.ec2.queryapi.Category;
import org.jclouds.codegen.ec2.queryapi.Content;
import org.jclouds.codegen.ec2.queryapi.DataType;
import org.jclouds.codegen.ec2.queryapi.Query;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class AmazonEC2QueryAPIValidator extends AmazonEC2QueryAPIExpectations {
   protected AmazonEC2QueryAPIValidator validateQueriesInCategory(String categoryName) {
      for (String query : expectedQueryNamesForCategoryName.get(categoryName)) {
         validateQueryInCategory(query, categoryName);
      }
      return this;
   }

   protected AmazonEC2QueryAPIValidator validateQueryInCategory(String queryName,
            String categoryName) {
      Category category = getModel().getCategories().get(categoryName);
      checkState(category != null, String.format("category %1$s not present", categoryName));
      Query query = category.getQueries().get(queryName);
      validateTopLevelType(query);
      checkState(category != null, String.format("query %1$s not present in category %2$s",
               queryName, categoryName));
      checkState(query.getResponseType().equals(query.getType() + "Response"), String.format(
               "Invalid responseType for %1$s [%2$s] should be %1$sResponse", queryName, query
                        .getResponseType()));
      checkNotNull(query.getDescription(), String.format("%1$s: getDescription()", query.getType()));

      validateTopLevelType(query);

      DataType response = getModel().getDataTypes().get(query.getResponseType());
      checkNotNull(response, String.format("response %1$s not present in domain for %2$s", query
               .getResponseType(), query.getType()));
      checkState(query.getResponseType().equals(query.getType() + "Response"));
      checkState(response.getType().equals(query.getType() + "Response"));
      validateTopLevelType(response);
      return validateDataType(query);
   }

   protected void validateTopLevelType(DataType type) {
      checkState(type.getAncestor() == null, String
               .format("%1$s should not have an ancestor", type));
      checkNotNull(type.getExampleCode(), String.format("%1$s: getExampleCode()", type.getType()));
      checkNotNull(type.getExampleHTML(), String.format("%1$s: getExampleHTML()", type.getType()));
   }

   protected AmazonEC2QueryAPIValidator validateDataType(DataType dataType) {
      checkState(dataType.getSee().size() >= 1, "see should have at least one entry");
      Set<String> fieldNames = Sets.newTreeSet(Iterables.transform(dataType.getContents(),
               new Function<Content, String>() {
                  public String apply(Content field) {
                     return field.getName();
                  }
               }));
      checkState(Sets.difference(fieldNames,
               expectedFieldNamesForDataTypeName.get(dataType.getType())).size() == 0, String
               .format("%1$s hasn't the correct fields.  has [%2$s] should have [%3$s]", dataType
                        .getType(), fieldNames, expectedFieldNamesForDataTypeName.get(dataType
                        .getType())));
      return this;
   }

   private AmazonEC2QueryAPI model;

   public AmazonEC2QueryAPIValidator validateCommands() {
      for (String categoryName : expectedQueryNamesForCategoryName.keySet()) {
         for (String queryName : expectedQueryNamesForCategoryName.get(categoryName)) {
            validateQueryInCategory(queryName, categoryName);
         }
      }
      return this;
   }

   public AmazonEC2QueryAPIValidator validateDomain() {
      for (String dataTypeName : expectedFieldNamesForDataTypeName.keySet()) {
         if (!this.queryNames.contains(dataTypeName))
            validateDataType(checkNotNull(getModel().getDataTypes().get(dataTypeName), dataTypeName));
      }
      return this;
   }

   public AmazonEC2QueryAPIValidator validateCategories() {
      checkState(Sets.difference(getModel().getCategories().keySet(),
               expectedQueryNamesForCategoryName.keySet()).size() == 0);
      checkState(getModel().getCategories().keySet().size() == expectedQueryNamesForCategoryName
               .keySet().size());
      return this;
   }

   public void setModel(AmazonEC2QueryAPI model) {
      this.model = model;
   }

   public AmazonEC2QueryAPI getModel() {
      return model;
   }

}