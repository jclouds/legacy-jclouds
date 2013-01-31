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
package org.jclouds.route53.features;

import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.domain.ChangeBatch;
import org.jclouds.route53.domain.RecordSet;
import org.jclouds.route53.domain.RecordSetIterable;
import org.jclouds.route53.domain.RecordSetIterable.NextRecord;

/**
 * @see RecordSetAsyncApi
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/ActionsOnRRS.html"
 *      />
 * @author Adrian Cole
 */
public interface RecordSetApi {

   /**
    * schedules creation of the resource record set.
    */
   Change create(RecordSet rrs);

   /**
    * applies a batch of changes atomically.
    */
   Change apply(ChangeBatch changes);

   /**
    * returns all resource record sets in order.
    */
   PagedIterable<RecordSet> list();

   /**
    * retrieves up to 100 resource record sets in order.
    */
   RecordSetIterable listFirstPage();

   /**
    * retrieves up to 100 resource record sets in order, starting at
    * {@code nextRecord}
    */
   RecordSetIterable listAt(NextRecord nextRecord);

   /**
    * This action deletes a resource record set.
    * 
    * @param rrs
    *           the resource record set to delete
    * @return null if not found or the change in progress
    */
   @Nullable
   Change delete(RecordSet rrs);
}
