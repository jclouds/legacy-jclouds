/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

/**
 * The gson project use package to control access to their objects. However,
 * this prevents us from doing valid work, like controling the json emitted on a
 * per-object basis. This is here to afford us to do this.
 * 
 * @author Adrian Cole
 */
public final class JsonLiteral extends JsonElement {
   private final CharSequence literal;

   public JsonLiteral(CharSequence literal) {
      this.literal = checkNotNull(literal, "literal");
   }

   @Override
   protected void toString(Appendable sb, Escaper escaper) throws IOException {
      sb.append(literal);
   }

}
