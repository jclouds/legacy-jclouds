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
package org.jclouds.savvis.vpdc;

import com.google.common.collect.ImmutableSet;

import java.net.URI;
import java.util.Set;

import org.jclouds.providers.BaseProviderMetadata;
import org.jclouds.providers.ProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Savvis Symphony VPDC services.
 * 
 * @author Kedar Dave
 */
public class SavvisSymphonyVPDCProviderMetadata extends BaseProviderMetadata {

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return "savvis-symphonyvpdc";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType() {
      return ProviderMetadata.COMPUTE_TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return "Savvis Symphony VPDC";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getIdentityName() {
      return "username";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getCredentialName() {
      return "password";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getHomepage() {
      return URI.create("http://www.savvis.com/en-US/infrastructure-services/Cloud/Pages/SavvisSymphonyVPDC.aspx");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getConsole() {
      return URI.create("https://www.savvisstation.com");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getApiDocumentation() {
      return URI.create("https://api.savvis.net/doc/spec/api/index.html");
   }

   /**
    * {@inheritDoc}
    * list of data centers from savvisstation colocation guide 
    * https://www.savvisstation.com/DocumentViewer?GUID=a95f0387-cbfe-43eb-b25b-4f2b0f68498f&sessionid=SavvisCCC%3ac9a8984b9655b01916be587e5204b2cf
    * Once we have confirmation from Savvis as to what data centers are used for vpdc deployments,
    * iso codes for those will be entered here
    * 
    * City								Code
    * 	Lithia Springs, GA 				AT1
	*	Waltham, MA 					BO1
	*	Waltham, MA 					BO2
	*	Waltham, MA 					B03
	*	Elk Grove Village, IL 			CH3
	*	Chicago, IL 					CH4 
	*	Sterling, VA 					DC2
	*	Sterling, VA 					DC3
	*	Sterling, VA 					DC4 Phase I 
	*   Sterling, VA 					DC4 Phase II
	*	Fort Worth, TX					DL1 
	*	Fort Worth, TX 					DL2 
	*	El Segundo, CA 					LA1 
	*	Jersey City, NJ 				NJ1
	*	Weehawken, NJ 					NJ2
	*	Piscataway, NJ 					NJ3
	*	Piscataway, NJ 2nd floor 		NJ3
	*	Weehawken, NJ 					NJ2X
	*	Irvine, CA 						OC2
	*	Santa Clara, CA 				SC4
	*	Santa Clara, CA 				SC5
	*	Santa Clara, CA 				SC8
	*	Santa Clara, CA 				SC9
	*	Tukwila, WA 1st floor 			SE2
	*	Montreal, Canada 				MR1
	*	Toronto, Canada 				TR1
	*	Vancouver, Canada 				VC1
    */
   @Override
   public Set<String> getIso3166Codes() {
      return ImmutableSet.of("");
   }

}