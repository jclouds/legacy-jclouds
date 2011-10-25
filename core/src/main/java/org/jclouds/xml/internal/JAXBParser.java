/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.xml.internal;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jclouds.xml.XMLParser;

/**
 * Parses XML documents using JAXB.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class JAXBParser implements XMLParser
{
    @Override
    public String toXML(final Object src) throws IOException
    {
        return toXML(src, src.getClass());
    }

    @Override
    public <T> String toXML(final Object src, final Class<T> type) throws IOException
    {
        try
        {
            JAXBContext context = JAXBContext.newInstance(type);
            Marshaller marshaller = context.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshal(src, writer);
            return writer.toString();
        }
        catch (JAXBException ex)
        {
            throw new IOException("Could not marshall object", ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T fromXML(final String xml, final Class<T> type) throws IOException
    {
        try
        {
            StringReader reader = new StringReader(xml);
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (T) unmarshaller.unmarshal(reader);
        }
        catch (Exception ex)
        {
            throw new IOException("Could not unmarshall document", ex);
        }
    }
}
