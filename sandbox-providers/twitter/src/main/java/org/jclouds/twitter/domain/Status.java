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
   private String geo;
   private long id;
   @SerializedName("in_reply_to_screen_name")
   private String inReplyToScreenName;
   @SerializedName("in_reply_to_status_id")
   private Integer inReplyToStatusId;
   @SerializedName("in_reply_to_user_id")
   private Integer inReplyToUserId;
   private String source;
   private String text;
   private boolean truncated;
   private User user;

   public Status() {
   }

   public Status(Date createdAt, boolean favorited, String geo, long id,
            String inReplyToScreenName, Integer inReplyToStatusId, Integer inReplyToUserId,
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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((text == null) ? 0 : text.hashCode());
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
      if (createdAt == null) {
         if (other.createdAt != null)
            return false;
      } else if (!createdAt.equals(other.createdAt))
         return false;
      if (id != other.id)
         return false;
      if (text == null) {
         if (other.text != null)
            return false;
      } else if (!text.equals(other.text))
         return false;
      if (user == null) {
         if (other.user != null)
            return false;
      } else if (!user.equals(other.user))
         return false;
      return true;
   }

   public Date getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public String getSource() {
      return source;
   }

   public void setSource(String source) {
      this.source = source;
   }

   public boolean isTruncated() {
      return truncated;
   }

   public void setTruncated(boolean truncated) {
      this.truncated = truncated;
   }

   public Integer getInReplyToStatusId() {
      return inReplyToStatusId;
   }

   public void setInReplyToStatusId(Integer inReplyToStatusId) {
      this.inReplyToStatusId = inReplyToStatusId;
   }

   public Integer getInReplyToUserId() {
      return inReplyToUserId;
   }

   public void setInReplyToUserId(Integer inReplyToUserId) {
      this.inReplyToUserId = inReplyToUserId;
   }

   public boolean isFavorited() {
      return favorited;
   }

   public void setFavorited(boolean favorited) {
      this.favorited = favorited;
   }

   public String getInReplyToScreenName() {
      return inReplyToScreenName;
   }

   public void setInReplyToScreenName(String inReplyToScreenName) {
      this.inReplyToScreenName = inReplyToScreenName;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public int compareTo(Status o) {
      return (int) ((this == o) ? 0 : id + "".compareTo(o.id + ""));
   }

   public void setGeo(String geo) {
      this.geo = geo;
   }

   public String getGeo() {
      return geo;
   }
}
