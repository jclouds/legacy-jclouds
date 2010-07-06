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
package org.jclouds.twitter.domain;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class Status implements Comparable<Status> {
   @SerializedName("created_at")
   private Date createdAt;
   private boolean favorited;
   private Location geo;
   private long id;
   @SerializedName("in_reply_to_screen_name")
   private String inReplyToScreenName;
   @SerializedName("in_reply_to_status_id")
   private Long inReplyToStatusId;
   @SerializedName("in_reply_to_user_id")
   private Integer inReplyToUserId;
   private String source;
   private String text;
   private boolean truncated;
   private User user;

   public Status() {
   }

   public Status(long id, User user, String text) {
      this.id = id;
      this.user = user;
      this.text = text;
   }

   public Status(Date createdAt, boolean favorited, Location geo, long id,
            String inReplyToScreenName, Long inReplyToStatusId, Integer inReplyToUserId,
            String source, String text, boolean truncated, User user) {
      this.createdAt = createdAt;
      this.favorited = favorited;
      this.geo = geo;
      this.id = id;
      this.inReplyToScreenName = inReplyToScreenName;
      this.inReplyToStatusId = inReplyToStatusId;
      this.inReplyToUserId = inReplyToUserId;
      this.source = source;
      this.text = text;
      this.truncated = truncated;
      this.user = user;
   }

   public int compareTo(Status o) {
      return (int) ((this == o) ? 0 : id + "".compareTo(o.id + ""));
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((user == null) ? 0 : user.hashCode());
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
      Status other = (Status) obj;
      if (id != other.id)
         return false;
      if (user == null) {
         if (other.user != null)
            return false;
      } else if (!user.equals(other.user))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Status [createdAt=" + createdAt + ", favorited=" + favorited + ", geo=" + geo
               + ", id=" + id + ", inReplyToScreenName=" + inReplyToScreenName
               + ", inReplyToStatusId=" + inReplyToStatusId + ", inReplyToUserId="
               + inReplyToUserId + ", source=" + source + ", text=" + text + ", truncated="
               + truncated + ", user=" + user + "]";
   }

   public Date getCreatedAt() {
      return createdAt;
   }

   public boolean isFavorited() {
      return favorited;
   }

   public Location getGeo() {
      return geo;
   }

   public long getId() {
      return id;
   }

   public String getInReplyToScreenName() {
      return inReplyToScreenName;
   }

   public Long getInReplyToStatusId() {
      return inReplyToStatusId;
   }

   public Integer getInReplyToUserId() {
      return inReplyToUserId;
   }

   public String getSource() {
      return source;
   }

   public String getText() {
      return text;
   }

   public boolean isTruncated() {
      return truncated;
   }

   public User getUser() {
      return user;
   }

}