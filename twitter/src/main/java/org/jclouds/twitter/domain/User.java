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
   @SerializedName("contributors_enabled")
   private boolean contributorsEnabled;
   private long id;
   private String location;
   private String name;
   private String lang;
   private boolean notifications;
   @SerializedName("profile_background_color")
   private String profileBackgroundColor;
   @SerializedName("profile_background_image_url")
   private URI profileBackgroundImageUrl;
   @SerializedName("profile_background_tile")
   private boolean profileBackgroundTile;
   @SerializedName("profile_use_background_image")
   private boolean profileUseBackgroundImage;
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

   public User(long id, String screenName) {
      this.id = id;
      this.screenName = screenName;
   }

   public User(Date createdAt, String description, int favouritesCount, int followersCount,
            boolean following, int friendsCount, boolean geoEnabled, boolean contributorsEnabled,
            long id, String location, String name, String lang, boolean notifications,
            String profileBackgroundColor, boolean profileUseBackgroundImage,
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
      this.geoEnabled = geoEnabled;
      this.id = id;
      this.location = location;
      this.name = name;
      this.lang = lang;
      this.notifications = notifications;
      this.contributorsEnabled = contributorsEnabled;
      this.profileBackgroundColor = profileBackgroundColor;
      this.profileUseBackgroundImage = profileUseBackgroundImage;
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

   public Date getCreatedAt() {
      return createdAt;
   }

   public String getDescription() {
      return description;
   }

   public int getFavouritesCount() {
      return favouritesCount;
   }

   public int getFollowersCount() {
      return followersCount;
   }

   public boolean isFollowing() {
      return following;
   }

   public int getFriendsCount() {
      return friendsCount;
   }

   public boolean isGeoEnabled() {
      return geoEnabled;
   }

   public long getId() {
      return id;
   }

   public String getLocation() {
      return location;
   }

   public String getName() {
      return name;
   }

   public boolean isNotifications() {
      return notifications;
   }

   public String getProfileBackgroundColor() {
      return profileBackgroundColor;
   }

   public URI getProfileBackgroundImageUrl() {
      return profileBackgroundImageUrl;
   }

   public boolean isProfileBackgroundTile() {
      return profileBackgroundTile;
   }

   public boolean isProfileUseBackgroundImage() {
      return profileUseBackgroundImage;
   }

   public URI getProfileImageUrl() {
      return profileImageUrl;
   }

   public String getProfileLinkColor() {
      return profileLinkColor;
   }

   public String getProfileSidebarBorderColor() {
      return profileSidebarBorderColor;
   }

   public String getProfileSidebarFillColor() {
      return profileSidebarFillColor;
   }

   public String getProfileTextColor() {
      return profileTextColor;
   }

   public boolean isProtected() {
      return isProtected;
   }

   public String getScreenName() {
      return screenName;
   }

   public int getStatusesCount() {
      return statusesCount;
   }

   public String getTimeZone() {
      return timeZone;
   }

   public URI getUrl() {
      return url;
   }

   public int getUtcOffset() {
      return utcOffset;
   }

   public boolean isVerified() {
      return verified;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (id ^ (id >>> 32));
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
      if (screenName == null) {
         if (other.screenName != null)
            return false;
      } else if (!screenName.equals(other.screenName))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "User [createdAt=" + createdAt + ", description=" + description + ", favouritesCount="
               + favouritesCount + ", followersCount=" + followersCount + ", following="
               + following + ", friendsCount=" + friendsCount + ", geoEnabled=" + geoEnabled
               + ", id=" + id + ", isProtected=" + isProtected + ", location=" + location
               + ", name=" + name + ", notifications=" + notifications
               + ", profileBackgroundColor=" + profileBackgroundColor
               + ", profileBackgroundImageUrl=" + profileBackgroundImageUrl
               + ", profileBackgroundTile=" + profileBackgroundTile + ", profileImageUrl="
               + profileImageUrl + ", profileLinkColor=" + profileLinkColor
               + ", profileSidebarBorderColor=" + profileSidebarBorderColor
               + ", profileSidebarFillColor=" + profileSidebarFillColor + ", profileTextColor="
               + profileTextColor + ", profileUseBackgroundImage=" + profileUseBackgroundImage
               + ", screenName=" + screenName + ", statusesCount=" + statusesCount + ", timeZone="
               + timeZone + ", url=" + url + ", utcOffset=" + utcOffset + ", verified=" + verified
               + "]";
   }

   public int compareTo(User o) {
      if (screenName == null)
         return -1;
      return (this == o) ? 0 : screenName.compareTo(o.screenName);
   }

   public boolean isContributorsEnabled() {
      return contributorsEnabled;
   }

   public String getLang() {
      return lang;
   }
}