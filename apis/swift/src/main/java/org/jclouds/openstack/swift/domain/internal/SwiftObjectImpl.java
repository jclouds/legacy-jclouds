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
package org.jclouds.openstack.swift.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.io.Payload;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.SwiftObject;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link SwiftObject}.
 * 
 * @author Adrian Cole
 */
public class SwiftObjectImpl extends PayloadEnclosingImpl implements SwiftObject, Comparable<SwiftObject> {

   private final DelegatingMutableObjectInfoWithMetadata info;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();

   @Inject
   public SwiftObjectImpl(MutableObjectInfoWithMetadata info) {
      super();
      this.info = new DelegatingMutableObjectInfoWithMetadata(info);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MutableObjectInfoWithMetadata getInfo() {
      return info;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Multimap<String, String> getAllHeaders() {
      return allHeaders;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setAllHeaders(Multimap<String, String> allHeaders) {
      this.allHeaders = checkNotNull(allHeaders, "allHeaders");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(SwiftObject o) {
      if (getInfo().getName() == null)
         return -1;
      return (this == o) ? 0 : getInfo().getName().compareTo(o.getInfo().getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((info == null) ? 0 : info.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      SwiftObjectImpl other = (SwiftObjectImpl) obj;
      if (info == null) {
         if (other.info != null)
            return false;
      } else if (!info.equals(other.info))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[info=" + info + "]";
   }

   @Override
   public void setPayload(Payload data) {
      HttpUtils.copy(data.getContentMetadata(), info);
      data.setContentMetadata(info);
      super.setPayload(data);
   }

}
