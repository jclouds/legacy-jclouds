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

import java.util.Set;
import org.jclouds.elb.domain.Policy;
import org.jclouds.elb.domain.PolicyType;
import org.jclouds.elb.options.ListPoliciesOptions;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides access to Amazon ELB via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference" />
 * @author Adrian Cole
 */
public interface PolicyApi {

   /**
    * Retrieves information about the specified policy.
    * 
    * @param name
    *           Name of the policy to get information about.
    * @return null if not found
    */
   @Nullable
   Policy get(String name);

   /**
    * Returns detailed descriptions of the policies.
    * 
    * If you specify a LoadBalancer name, the operation returns either the descriptions of the
    * specified policies, or descriptions of all the policies created for the LoadBalancer. If you
    * don't specify a LoadBalancer name, the operation returns descriptions of the specified sample
    * policies, or descriptions of all the sample policies. The names of the sample policies have
    * the ELBSample- prefix.
    * 
    * @param options
    *           the options describing the policies query
    * 
    * @return the response object
    */
   Set<Policy> list(ListPoliciesOptions options);

   /**
    * returns descriptions of the specified sample policies, or descriptions of all the sample
    * policies.
    * 
    * @return the response object
    */
   Set<Policy> list();

   /**
    * Retrieves information about the specified policy type.
    * 
    * @param name
    *           Name of the policy type to get information about.
    * @return null if not found
    */
   @Nullable
   PolicyType getType(String name);

   /**
    * Returns meta-information on the specified LoadBalancer policies defined by the Elastic Load
    * Balancing service. The policy types that are returned from this action can be used in a
    * CreateLoadBalancerPolicy action to instantiate specific policy configurations that will be
    * applied to an Elastic LoadBalancer.
    * 
    * @return the response object
    */
   Set<PolicyType> listTypes();

   /**
    * @param names Specifies the name of the policy types. If no names are specified, returns the description of all the policy types defined by Elastic Load Balancing service.
    * 
    * @see #listTypes()
    */
   Set<PolicyType> listTypes(Iterable<String> names);

}
