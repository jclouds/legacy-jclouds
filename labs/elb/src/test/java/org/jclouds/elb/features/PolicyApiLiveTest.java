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
package org.jclouds.elb.features;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.elb.domain.AttributeMetadata;
import org.jclouds.elb.domain.Policy;
import org.jclouds.elb.domain.PolicyType;
import org.jclouds.elb.internal.BaseELBApiLiveTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "PolicyApiLiveTest")
public class PolicyApiLiveTest extends BaseELBApiLiveTest {

   private void checkPolicy(Policy policy) {
      checkNotNull(policy.getName(), "Name cannot be null for a Policy.");
      checkNotNull(policy.getTypeName(), "TypeName cannot be null for a Policy.");
      checkNotNull(policy.getAttributes(), "While it is ok to have no attributes, the map cannot be null.");
   }

   @Test
   protected void testDescribePolicies() {
      Set<Policy> response = api().list();

      for (Policy policy : response) {
         checkPolicy(policy);
      }

      if (response.size() > 0) {
         Policy policy = response.iterator().next();
         Assert.assertEquals(api().get(policy.getName()), policy);
      }

   }

   private void checkPolicyType(PolicyType policyType) {
      checkNotNull(policyType.getName(), "Name cannot be null for a PolicyType.");
      checkNotNull(policyType.getDescription(), "Description cannot be null for a PolicyType.");
      checkNotNull(policyType.getAttributeMetadata(), "While it is ok to have no attributes, the set cannot be null.");
      for (AttributeMetadata<?> attributeMetadata: policyType.getAttributeMetadata()) {
         checkAttributeMetadata(attributeMetadata);
      }
   }

   private void checkAttributeMetadata(AttributeMetadata<?> attributeMetadata) {
      checkNotNull(attributeMetadata.getName(), "Name cannot be null for a AttributeMetadata.");
      checkNotNull(attributeMetadata.getType(), "Type cannot be null for a AttributeMetadata.");
      checkNotNull(attributeMetadata.getCardinality(), "Cardinality cannot be null for a AttributeMetadata.");
      checkNotNull(attributeMetadata.getDefaultValue(), "While DefaultValue can be null, its optional wrapper cannot");
      checkNotNull(attributeMetadata.getDescription(),  "While Description can be null, its optional wrapper cannot");
   }

   @Test
   protected void testDescribePolicyTypes() {
      Set<PolicyType> response = api().listTypes();

      for (PolicyType policyType : response) {
         checkPolicyType(policyType);
      }

      if (response.size() > 0) {
         PolicyType policyType = response.iterator().next();
         Assert.assertEquals(api().getType(policyType.getName()), policyType);
      }

      if (response.size() > 0) {
         Iterable<String> names = Iterables.transform(response, new Function<PolicyType, String>() {

            @Override
            public String apply(@Nullable PolicyType input) {
               return input.getName();
            }

         });
         Assert.assertEquals(api().listTypes(names), response);
      }
   }

   protected PolicyApi api() {
      return context.getApi().getPolicyApi();
   }
}
