/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.opscodeplatform.domain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import com.google.gson.annotations.SerializedName;

/**
 * User object.
 * 
 * @author Adrian Cole
 */
public class User {
   private String username;
   @SerializedName("first_name")
   private String firstName;
   @SerializedName("middle_name")
   private String middleName;
   @SerializedName("last_name")
   private String lastName;
   @SerializedName("display_name")
   private String displayName;
   private String email;
   @SerializedName("twitter_account")
   private String twitterAccount;
   private String city;
   private String country;
   @SerializedName("image_file_name")
   private String imageFileName;
   private String password;
   @SerializedName("public_key")
   private PublicKey publicKey;
   @SerializedName("private_key")
   private PrivateKey privateKey;
   private X509Certificate certificate;
   private String salt;

   public User(String username) {
      this.username = username;
   }

   public User(String username, String firstName, String middleName, String lastName, String displayName, String email,
         String twitterAccount, String city, String country, String imageFileName, String password,
         PublicKey publicKey, PrivateKey privateKey, X509Certificate certificate, String salt) {
      this.username = username;
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
      this.displayName = displayName;
      this.email = email;
      this.twitterAccount = twitterAccount;
      this.city = city;
      this.country = country;
      this.imageFileName = imageFileName;
      this.password = password;
      this.publicKey = publicKey;
      this.privateKey = privateKey;
      this.certificate = certificate;
      this.salt = salt;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((certificate == null) ? 0 : certificate.hashCode());
      result = prime * result + ((city == null) ? 0 : city.hashCode());
      result = prime * result + ((country == null) ? 0 : country.hashCode());
      result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
      result = prime * result + ((email == null) ? 0 : email.hashCode());
      result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
      result = prime * result + ((imageFileName == null) ? 0 : imageFileName.hashCode());
      result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
      result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());
      result = prime * result + ((password == null) ? 0 : password.hashCode());
      result = prime * result + ((privateKey == null) ? 0 : privateKey.hashCode());
      result = prime * result + ((publicKey == null) ? 0 : publicKey.hashCode());
      result = prime * result + ((salt == null) ? 0 : salt.hashCode());
      result = prime * result + ((twitterAccount == null) ? 0 : twitterAccount.hashCode());
      result = prime * result + ((username == null) ? 0 : username.hashCode());
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
      if (certificate == null) {
         if (other.certificate != null)
            return false;
      } else if (!certificate.equals(other.certificate))
         return false;
      if (city == null) {
         if (other.city != null)
            return false;
      } else if (!city.equals(other.city))
         return false;
      if (country == null) {
         if (other.country != null)
            return false;
      } else if (!country.equals(other.country))
         return false;
      if (displayName == null) {
         if (other.displayName != null)
            return false;
      } else if (!displayName.equals(other.displayName))
         return false;
      if (email == null) {
         if (other.email != null)
            return false;
      } else if (!email.equals(other.email))
         return false;
      if (firstName == null) {
         if (other.firstName != null)
            return false;
      } else if (!firstName.equals(other.firstName))
         return false;
      if (imageFileName == null) {
         if (other.imageFileName != null)
            return false;
      } else if (!imageFileName.equals(other.imageFileName))
         return false;
      if (lastName == null) {
         if (other.lastName != null)
            return false;
      } else if (!lastName.equals(other.lastName))
         return false;
      if (middleName == null) {
         if (other.middleName != null)
            return false;
      } else if (!middleName.equals(other.middleName))
         return false;
      if (password == null) {
         if (other.password != null)
            return false;
      } else if (!password.equals(other.password))
         return false;
      if (privateKey == null) {
         if (other.privateKey != null)
            return false;
      } else if (!privateKey.equals(other.privateKey))
         return false;
      if (publicKey == null) {
         if (other.publicKey != null)
            return false;
      } else if (!publicKey.equals(other.publicKey))
         return false;
      if (salt == null) {
         if (other.salt != null)
            return false;
      } else if (!salt.equals(other.salt))
         return false;
      if (twitterAccount == null) {
         if (other.twitterAccount != null)
            return false;
      } else if (!twitterAccount.equals(other.twitterAccount))
         return false;
      if (username == null) {
         if (other.username != null)
            return false;
      } else if (!username.equals(other.username))
         return false;
      return true;
   }

   // only for deserialization
   User() {

   }

   public String getUsername() {
      return username;
   }

   public String getFirstName() {
      return firstName;
   }

   public String getMiddleName() {
      return middleName;
   }

   public String getLastName() {
      return lastName;
   }

   public String getDisplayName() {
      return displayName;
   }

   public String getEmail() {
      return email;
   }

   public String getTwitterAccount() {
      return twitterAccount;
   }

   public String getCity() {
      return city;
   }

   public String getCountry() {
      return country;
   }

   public String getImageFileName() {
      return imageFileName;
   }

   public String getPassword() {
      return password;
   }

   public PublicKey getPublicKey() {
      return publicKey;
   }

   @Override
   public String toString() {
      return "[certificate=" + certificate + ", city=" + city + ", country=" + country + ", displayName=" + displayName
            + ", email=" + email + ", firstName=" + firstName + ", imageFileName=" + imageFileName + ", lastName="
            + lastName + ", middleName=" + middleName + ", password=" + (password != null) + ", privateKey="
            + (privateKey != null) + ", publicKey=" + publicKey + ", salt=" + salt + ", twitterAccount="
            + twitterAccount + ", username=" + username + "]";
   }

   public PrivateKey getPrivateKey() {
      return privateKey;
   }

   public X509Certificate getCertificate() {
      return certificate;
   }

   public String getSalt() {
      return salt;
   }

}
