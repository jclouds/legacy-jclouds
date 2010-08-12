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

package com.google.gson;

import java.io.IOException;

/**
 * The only reason I am pasting this class and changing 2 lines is because the
 * gson project uses abstract classes and final otherwise for every part one
 * might want to extend. We simply need to control the formatting of json
 * literals, and hopefully one day gson will allow us to subclass something.
 * 
 * @author Adrian Cole
 * @author Inderjeet Singh
 */
public class JcloudsCompactFormatter implements JsonFormatter {

   static class FormattingVisitor implements JsonElementVisitor {
      private final Appendable writer;
      private final Escaper escaper;
      private final boolean serializeNulls;

      FormattingVisitor(Appendable writer, Escaper escaper, boolean serializeNulls) {
         this.writer = writer;
         this.escaper = escaper;
         this.serializeNulls = serializeNulls;
      }

      public void visitLiteral(JsonLiteral primitive) throws IOException {
         primitive.toString(writer, escaper);
      }

      public void visitPrimitive(JsonPrimitive primitive) throws IOException {
         primitive.toString(writer, escaper);
      }

      public void visitNull() throws IOException {
         writer.append("null");
      }

      public void startArray(JsonArray array) throws IOException {
         writer.append('[');
      }

      public void visitArrayMember(JsonArray parent, JsonPrimitive member, boolean isFirst) throws IOException {
         if (!isFirst) {
            writer.append(',');
         }
         member.toString(writer, escaper);
      }

      public void visitArrayMember(JsonArray parent, JsonArray member, boolean isFirst) throws IOException {
         if (!isFirst) {
            writer.append(',');
         }
      }

      public void visitArrayMember(JsonArray parent, JsonObject member, boolean isFirst) throws IOException {
         if (!isFirst) {
            writer.append(',');
         }
      }

      public void visitNullArrayMember(JsonArray parent, boolean isFirst) throws IOException {
         if (!isFirst) {
            writer.append(',');
         }
      }

      public void endArray(JsonArray array) throws IOException {
         writer.append(']');
      }

      public void startObject(JsonObject object) throws IOException {
         writer.append('{');
      }

      public void visitObjectMember(JsonObject parent, String memberName, JsonLiteral member, boolean isFirst)
            throws IOException {
         if (!isFirst) {
            writer.append(',');
         }
         writer.append('"');
         writer.append(memberName);
         writer.append("\":");
         member.toString(writer, escaper);
      }

      public void visitObjectMember(JsonObject parent, String memberName, JsonPrimitive member, boolean isFirst)
            throws IOException {
         if (!isFirst) {
            writer.append(',');
         }
         writer.append('"');
         writer.append(memberName);
         writer.append("\":");
         member.toString(writer, escaper);
      }

      public void visitObjectMember(JsonObject parent, String memberName, JsonArray member, boolean isFirst)
            throws IOException {
         if (!isFirst) {
            writer.append(',');
         }
         writer.append('"');
         writer.append(memberName);
         writer.append("\":");
      }

      public void visitObjectMember(JsonObject parent, String memberName, JsonObject member, boolean isFirst)
            throws IOException {
         if (!isFirst) {
            writer.append(',');
         }
         writer.append('"');
         writer.append(memberName);
         writer.append("\":");
      }

      public void visitNullObjectMember(JsonObject parent, String memberName, boolean isFirst) throws IOException {
         if (serializeNulls) {
            visitObjectMember(parent, memberName, (JsonObject) null, isFirst);
         }
      }

      public void endObject(JsonObject object) throws IOException {
         writer.append('}');
      }
   }

   private final boolean escapeHtmlChars;

   public JcloudsCompactFormatter() {
      this(true);
   }

   JcloudsCompactFormatter(boolean escapeHtmlChars) {
      this.escapeHtmlChars = escapeHtmlChars;
   }

   public void format(JsonElement root, Appendable writer, boolean serializeNulls) throws IOException {
      if (root == null) {
         return;
      }
      FormattingVisitor visitor = new FormattingVisitor(writer, new Escaper(escapeHtmlChars), serializeNulls);
      JcloudsTreeNavigator navigator = new JcloudsTreeNavigator(visitor, serializeNulls);
      navigator.navigate(root);
   }
}
