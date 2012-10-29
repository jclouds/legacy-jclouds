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

import java.net.URI;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class User implements Comparable<User> {
   @SerializedName("created_at")
   private Date createdAt;
   private String description;
   @SerializedName("favourites_count")
   private int favouritesCount;
   @SerializedName("followers_count")
   private int followersCount;
   private boolean following;
   @SerializedName("friends_count")
   private int friendsCount;
   @SerializedName("geo_enabled")
   private boolean geoEnabled;
   private long id;
   private String location;
   private String name;
   private boolean notifications;
   @SerializedName("profile_background_color")
   private String profileBackgroundColor;
   @SerializedName("profile_background_image_url")
   private URI profileBackgroundImageUrl;
   @SerializedName("profile_background_tile")
   private boolean profileBackgroundTile;
   @SerializedName("profile_image_url")
   private URI profileImageUrl;
   @SerializedName("profile_link_color")
   private String profileLinkColor;
   @SerializedName("profile_sidebar_border_color")
   private String profileSidebarBorderColor;
   @SerializedName("profile_sidebar_fill_color")
   private String profileSidebarFillColor;
   @SerializedName("profile_text_color")
   private String profileTextColor;
   @SerializedName("protected")
   private boolean isProtected;
   @SerializedName("screen_name")
   private String screenName;
   @SerializedName("statuses_count")
   private int statusesCount;
   @SerializedName("time_zone")
   private String timeZone;
   private URI url;
   @SerializedName("utc_offset")
   private int utcOffset;
   private boolean verified;

   public User() {

   }

   public User(Date createdAt, String description, int favouritesCount, int followersCount,
            boolean following, int friendsCount, boolean geoEnabled, long id, String location,
            String name, boolean notifications, String profileBackgroundColor,
            URI profileBackgroundImageUrl, boolean profileBackgroundTile, URI profileImageUrl,
            String profileLinkColor, String profileSidebarBorderColor,
            String profileSidebarFillColor, String profileTextColor, boolean isProtected,
            String screenName, int statusesCount, String timeZone, URI url, int utcOffset,
            boolean verified) {
      this.createdAt = createdAt;
      this.description = description;
      this.favouritesCount = favouritesCount;
      this.followersCount = followersCount;
      this.following = following;
      this.friendsCount = friendsCount;
      this.setGeoEnabled(geoEnabled);
      this.id = id;
      this.location = location;
      this.name = name;
      this.notifications = notifications;
      this.profileBackgroundColor = profileBackgroundColor;
      this.profileBackgroundImageUrl = profileBackgroundImageUrl;
      this.profileBackgroundTile = profileBackgroundTile;
      this.profileImageUrl = profileImageUrl;
      this.profileLinkColor = profileLinkColor;
      this.profileSidebarBorderColor = profileSidebarBorderColor;
      this.profileSidebarFillColor = profileSidebarFillColor;
      this.profileTextColor = profileTextColor;
      this.isProtected = isProtected;
      this.screenName = screenName;
      this.statusesCount = statusesCount;
      this.timeZone = timeZone;
      this.url = url;
      this.utcOffset = utcOffset;
      this.verified = verified;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((screenName == null) ? 0 : screenName.hashCode());
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
      User other = (User) obj;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (screenName == null) {
         if (other.screenName != null)
            return false;
      } else if (!screenName.equals(other.screenName))
         return false;
      return true;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getScreenName() {
      return screenName;
   }

   public void setScreenName(String screenName) {
      this.screenName = screenName;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public URI getProfileImageUrl() {
      return profileImageUrl;
   }

   public void setProfileImageUrl(URI profileImageUrl) {
      this.profileImageUrl = profileImageUrl;
   }

   public URI getUrl() {
      return url;
   }

   public void setUrl(URI url) {
      this.url = url;
   }

   public boolean isProtected() {
      return isProtected;
   }

   public void setProtected(boolean isProtected) {
      this.isProtected = isProtected;
   }

   public int getFollowersCount() {
      return followersCount;
   }

   public void setFollowersCount(int followersCount) {
      this.followersCount = followersCount;
   }

   public String getProfileBackgroundColor() {
      return profileBackgroundColor;
   }

   public void setProfileBackgroundColor(String profileBackgroundColor) {
      this.profileBackgroundColor = profileBackgroundColor;
   }

   public String getProfileTextColor() {
      return profileTextColor;
   }

   public void setProfileTextColor(String profileTextColor) {
      this.profileTextColor = profileTextColor;
   }

   public String getProfileLinkColor() {
      return profileLinkColor;
   }

   public void setProfileLinkColor(String profileLinkColor) {
      this.profileLinkColor = profileLinkColor;
   }

   public String getProfileSidebarFillColor() {
      return profileSidebarFillColor;
   }

   public void setProfileSidebarFillColor(String profileSidebarFillColor) {
      this.profileSidebarFillColor = profileSidebarFillColor;
   }

   public String getProfileSidebarBorderColor() {
      return profileSidebarBorderColor;
   }

   public void setProfileSidebarBorderColor(String profileSidebarBorderColor) {
      this.profileSidebarBorderColor = profileSidebarBorderColor;
   }

   public int getFriendsCount() {
      return friendsCount;
   }

   public void setFriendsCount(int friendsCount) {
      this.friendsCount = friendsCount;
   }

   public Date getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
   }

   public int getFavouritesCount() {
      return favouritesCount;
   }

   public void setFavouritesCount(int favouritesCount) {
      this.favouritesCount = favouritesCount;
   }

   public int getUtcOffset() {
      return utcOffset;
   }

   public void setUtcOffset(int utcOffset) {
      this.utcOffset = utcOffset;
   }

   public String getTimeZone() {
      return timeZone;
   }

   public void setTimeZone(String timeZone) {
      this.timeZone = timeZone;
   }

   public URI getProfileBackgroundImageUrl() {
      return profileBackgroundImageUrl;
   }

   public void setProfileBackgroundImageUrl(URI profileBackgroundImageUrl) {
      this.profileBackgroundImageUrl = profileBackgroundImageUrl;
   }

   public boolean isProfileBackgroundTile() {
      return profileBackgroundTile;
   }

   public void setProfileBackgroundTile(boolean profileBackgroundTile) {
      this.profileBackgroundTile = profileBackgroundTile;
   }

   public int getStatusesCount() {
      return statusesCount;
   }

   public void setStatusesCount(int statusesCount) {
      this.statusesCount = statusesCount;
   }

   public boolean isNotifications() {
      return notifications;
   }

   public void setNotifications(boolean notifications) {
      this.notifications = notifications;
   }

   public boolean isFollowing() {
      return following;
   }

   public void setFollowing(boolean following) {
      this.following = following;
   }

   public boolean isVerified() {
      return verified;
   }

   public void setVerified(boolean verified) {
      this.verified = verified;
   }

   public int compareTo(User o) {
      if (screenName == null)
         return -1;
      return (this == o) ? 0 : screenName.compareTo(o.screenName);
   }

   public void setGeoEnabled(boolean geoEnabled) {
      this.geoEnabled = geoEnabled;
   }

   public boolean isGeoEnabled() {
      return geoEnabled;
   }
}
