/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.s3.domain;

import java.util.Date;

/**
 * System metadata of the S3Bucket
 * 
 * @author Adrian Cole
 */
public class BucketMetadata implements Comparable<BucketMetadata> {
   /** The serialVersionUID */
   private static final long serialVersionUID = -6965068835316857535L;
   private final Date creationDate;
   private final CanonicalUser owner;
   private final String name;

   /**
    * Location constraint of the bucket.
    * 
    * @author Adrian Cole
    * @see <a href= "http://docs.amazonwebservices.com/AmazonS3/latest/RESTBucketLocationGET.html"
    *      />
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AmazonS3/latest/dev/index.html?LocationSelection.html"
    *      />
    */
   public static enum LocationConstraint {
      /**
       * 
       * US Standard—Uses Amazon S3 servers in the United States
       * <p/>
       * This is the default Region. All requests sent to s3.amazonaws.com go to this Region unless
       * you specify a LocationConstraint on a bucket. The US Standard Region automatically places
       * your data in either Amazon's east or west coast data centers depending on what will provide
       * you with the lowest latency. To use this region, do not set the LocationConstraint bucket
       * parameter. The US Standard Region provides eventual consistency for all requests.
       */
      US_STANDARD,

      /**
       * Uses Amazon S3 servers in Ireland.
       * <p/>
       * In Amazon S3, the EU (Ireland) Region provides read-after-write consistency for PUTS of new
       * objects in your Amazon S3 bucket and eventual consistency for overwrite PUTS and DELETES.
       */
      EU,

      /**
       * US-West (Northern California)—Uses Amazon S3 servers in Northern California
       * <p/>
       * Optionally, use the endpoint s3-us-west-1.amazonaws.com on all requests to this bucket to
       * reduce the latency you might experience after the first hour of creating a bucket in this
       * Region.
       * <p/>
       * In Amazon S3, the US-West (Northern California) Region provides read-after-write
       * consistency for PUTS of new objects in your Amazon S3 bucket and eventual consistency for
       * overwrite PUTS and DELETES.
       */
      US_WEST;

      /**
       * returns the value expected in xml documents from the S3 service.
       * <p/>
       * {@code US_STANDARD} is returned as "" xml documents, so we return "".
       */
      public String value() {
         switch (this) {
            case US_STANDARD:
               return "";
            case EU:
               return "EU";
            case US_WEST:
               return "us-west-1";
            default:
               throw new IllegalStateException("unimplemented location: " + this);
         }
      }

      /**
       * parses the value expected in xml documents from the S3 service.=
       * <p/>
       * {@code US_STANDARD} is returned as "" xml documents.
       */
      public static LocationConstraint fromValue(String v) {
         if (v.equals(""))
            return US_STANDARD;
         if (v.equals("EU"))
            return EU;
         else if (v.equals("us-west-1"))
            return US_WEST;
         throw new IllegalStateException("unimplemented location: " + v);
      }
   }

   public BucketMetadata(String name, Date creationDate, CanonicalUser owner) {
      this.name = name;
      this.creationDate = creationDate;
      this.owner = owner;
   }

   /**
    * Every bucket and object in Amazon S3 has an owner, the user that created the bucket or object.
    * The owner of a bucket or object cannot be changed. However, if the object is overwritten by
    * another user (deleted and rewritten), the new object will have a new owner.
    */
   public CanonicalUser getOwner() {
      return owner;
   }

   public Date getCreationDate() {
      return creationDate;
   }

   /**
    * To comply with Amazon S3 requirements, bucket names must:
    * <p/>
    * Contain lowercase letters, numbers, periods (.), underscores (_), and dashes (-)
    * <p/>
    * Start with a number or letter
    * <p/>
    * Be between 3 and 255 characters long
    * <p/>
    * Not be in an IP address style (e.g., "192.168.5.4")
    */
   public String getName() {
      return name;
   }

   public int compareTo(BucketMetadata o) {
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BucketMetadata other = (BucketMetadata) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (owner == null) {
         if (other.owner != null)
            return false;
      } else if (!owner.equals(other.owner))
         return false;
      return true;
   }
}