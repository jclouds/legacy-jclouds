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
package org.jclouds.simpledb.domain;



/**
 * AttributePair is a class to support the attributes to put in the SimpleDB
 * <p/>
 * 
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class AttributePair 
{

	private String key;
	private String value;
	private boolean replace;
	
	
	/**
	 * 
	 * Default constructor to represent an attribute in a domain in SimpleDB
	 * 
	 * <p/>
	 * 
	 * @param key Name of Attribute
	 * @param value Value of Attribute
	 * @param replace Replace value if it already exists in domain
	 */
	public AttributePair(String key, String value, boolean replace)
	{
		this.key = key;
                
		this.value = value ;
		this.replace = replace;
	}
	
	
	public String getKey() {
		return key;
	}


	public String getValue() {
		return value;
	}

        public void setValue(String value)
        {
            this.value = value;
        }


	public boolean isReplace() {
		return replace;
	}

	
	
	
}
