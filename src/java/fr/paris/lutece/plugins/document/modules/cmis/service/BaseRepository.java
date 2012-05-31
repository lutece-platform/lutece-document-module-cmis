/*
 * Copyright (c) 2002-2012, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.document.modules.cmis.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import org.apache.chemistry.opencmis.commons.data.PermissionMapping;
import org.apache.chemistry.opencmis.commons.definitions.PermissionDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.*;

/**
 *
 * @author pierre
 */
public abstract class BaseRepository
{
    protected static PermissionDefinition createPermission(String permission, String description)
    {
        PermissionDefinitionDataImpl pd = new PermissionDefinitionDataImpl();
        pd.setPermission(permission);
        pd.setDescription(description);

        return pd;
    }

    protected static PermissionMapping createMapping(String key, String permission)
    {
        PermissionMappingDataImpl pm = new PermissionMappingDataImpl();
        pm.setKey(key);
        pm.setPermissions(Collections.singletonList(permission));

        return pm;
    }


    protected void addPropertyId(PropertiesImpl props, String typeId, Set<String> filter, String id, String value)
    {
        if (!checkAddProperty(props, typeId, filter, id))
        {
            return;
        }

        props.addProperty(new PropertyIdImpl(id, value));
    }

    protected void addPropertyIdList(PropertiesImpl props, String typeId, Set<String> filter, String id,
            List<String> value)
    {
        if (!checkAddProperty(props, typeId, filter, id))
        {
            return;
        }

        props.addProperty(new PropertyIdImpl(id, value));
    }

    protected void addPropertyString(PropertiesImpl props, String typeId, Set<String> filter, String id, String value)
    {
        if (!checkAddProperty(props, typeId, filter, id))
        {
            return;
        }

        props.addProperty(new PropertyStringImpl(id, value));
    }

    protected void addPropertyInteger(PropertiesImpl props, String typeId, Set<String> filter, String id, long value)
    {
        addPropertyBigInteger(props, typeId, filter, id, BigInteger.valueOf(value));
    }

    protected void addPropertyBigInteger(PropertiesImpl props, String typeId, Set<String> filter, String id,
            BigInteger value)
    {
        if (!checkAddProperty(props, typeId, filter, id))
        {
            return;
        }

        props.addProperty(new PropertyIntegerImpl(id, value));
    }

    protected void addPropertyBoolean(PropertiesImpl props, String typeId, Set<String> filter, String id, boolean value)
    {
        if (!checkAddProperty(props, typeId, filter, id))
        {
            return;
        }

        props.addProperty(new PropertyBooleanImpl(id, value));
    }

    protected void addPropertyDateTime(PropertiesImpl props, String typeId, Set<String> filter, String id,
            GregorianCalendar value)
    {
        if (!checkAddProperty(props, typeId, filter, id))
        {
            return;
        }

        props.addProperty(new PropertyDateTimeImpl(id, value));
    }

    protected boolean checkAddProperty(org.apache.chemistry.opencmis.commons.data.Properties properties, String typeId, Set<String> filter, String id)
    {
        if ((properties == null) || (properties.getProperties() == null))
        {
            throw new IllegalArgumentException("Properties must not be null!");
        }

        if (id == null)
        {
            throw new IllegalArgumentException("Id must not be null!");
        }


        return true;
    }

    /**
     * Adds the default value of property if defined.
     */
    @SuppressWarnings("unchecked")
    protected static boolean addPropertyDefault(PropertiesImpl props, PropertyDefinition<?> propDef)
    {
        if ((props == null) || (props.getProperties() == null))
        {
            throw new IllegalArgumentException("Props must not be null!");
        }

        if (propDef == null)
        {
            return false;
        }

        List<?> defaultValue = propDef.getDefaultValue();
        if ((defaultValue != null) && (!defaultValue.isEmpty()))
        {
            switch (propDef.getPropertyType())
            {
                case BOOLEAN:
                    props.addProperty(new PropertyBooleanImpl(propDef.getId(), (List<Boolean>) defaultValue));
                    break;
                case DATETIME:
                    props.addProperty(new PropertyDateTimeImpl(propDef.getId(), (List<GregorianCalendar>) defaultValue));
                    break;
                case DECIMAL:
                    props.addProperty(new PropertyDecimalImpl(propDef.getId(), (List<BigDecimal>) defaultValue));
                    break;
                case HTML:
                    props.addProperty(new PropertyHtmlImpl(propDef.getId(), (List<String>) defaultValue));
                    break;
                case ID:
                    props.addProperty(new PropertyIdImpl(propDef.getId(), (List<String>) defaultValue));
                    break;
                case INTEGER:
                    props.addProperty(new PropertyIntegerImpl(propDef.getId(), (List<BigInteger>) defaultValue));
                    break;
                case STRING:
                    props.addProperty(new PropertyStringImpl(propDef.getId(), (List<String>) defaultValue));
                    break;
                case URI:
                    props.addProperty(new PropertyUriImpl(propDef.getId(), (List<String>) defaultValue));
                    break;
                default:
                    throw new RuntimeException("Unknown datatype! Spec change?");
            }

            return true;
        }

        return false;
    }
    
    
        /**
     * Converts milliseconds into a calendar object.
     */
    protected static GregorianCalendar millisToCalendar(long millis) {
        GregorianCalendar result = new GregorianCalendar();
        result.setTimeZone(TimeZone.getTimeZone("GMT"));
        result.setTimeInMillis((long) (Math.ceil(millis / 1000) * 1000));

        return result;
    }

}
