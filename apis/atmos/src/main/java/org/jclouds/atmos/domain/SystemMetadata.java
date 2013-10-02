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
package org.jclouds.atmos.domain;

import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

/**
 * Metadata of a Atmos Online object
 * 
 * @author Adrian Cole
 */
public class SystemMetadata extends DirectoryEntry {

   private final Date atime;
   private final Date ctime;
   private final String gid;
   private final Date itime;
   private final Date mtime;
   private final int nlink;
   private final String policyname;
   private final long size;
   private final String uid;
   private final byte[] contentmd5;

   public SystemMetadata(@Nullable byte [] contentmd5, Date atime, Date ctime, String gid, Date itime, Date mtime, int nlink,
            String objectid, String objname, String policyname, long size, FileType type, String uid) {
      super(objectid, type, objname);
      this.contentmd5 = contentmd5;
      this.atime = atime;
      this.ctime = ctime;
      this.gid = gid;
      this.itime = itime;
      this.mtime = mtime;
      this.nlink = nlink;
      this.policyname = policyname;
      this.size = size;
      this.uid = uid;
   }

   public String getGroupID() {
      return gid;
   }

   public int getHardLinkCount() {
      return nlink;
   }

   public Date getInceptionTime() {
      return itime;
   }

   public Date getLastAccessTime() {
      return atime;
   }

   public Date getLastMetadataModification() {
      return mtime;
   }

   public Date getLastUserDataModification() {
      return ctime;
   }

   public String getPolicyName() {
      return policyname;
   }

   public long getSize() {
      return size;
   }

   public String getUserID() {
      return uid;
   }

   public byte[] getContentMD5() {
      return contentmd5;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((atime == null) ? 0 : atime.hashCode());
      result = prime * result + ((ctime == null) ? 0 : ctime.hashCode());
      result = prime * result + ((gid == null) ? 0 : gid.hashCode());
      result = prime * result + ((itime == null) ? 0 : itime.hashCode());
      result = prime * result + ((mtime == null) ? 0 : mtime.hashCode());
      result = prime * result + nlink;
      result = prime * result + ((policyname == null) ? 0 : policyname.hashCode());
      result = prime * result + (int) (size ^ (size >>> 32));
      result = prime * result + ((uid == null) ? 0 : uid.hashCode());
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
      SystemMetadata other = (SystemMetadata) obj;
      if (atime == null) {
         if (other.atime != null)
            return false;
      } else if (!atime.equals(other.atime))
         return false;
      if (ctime == null) {
         if (other.ctime != null)
            return false;
      } else if (!ctime.equals(other.ctime))
         return false;
      if (gid == null) {
         if (other.gid != null)
            return false;
      } else if (!gid.equals(other.gid))
         return false;
      if (itime == null) {
         if (other.itime != null)
            return false;
      } else if (!itime.equals(other.itime))
         return false;
      if (mtime == null) {
         if (other.mtime != null)
            return false;
      } else if (!mtime.equals(other.mtime))
         return false;
      if (nlink != other.nlink)
         return false;
      if (policyname == null) {
         if (other.policyname != null)
            return false;
      } else if (!policyname.equals(other.policyname))
         return false;
      if (size != other.size)
         return false;
      if (uid == null) {
         if (other.uid != null)
            return false;
      } else if (!uid.equals(other.uid))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[type=" + getType() + ", id=" + getObjectID() + ", name=" + getObjectName() + "]";
   }

}
