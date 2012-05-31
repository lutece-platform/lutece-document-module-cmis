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

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import java.math.BigInteger;
import java.util.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.*;
import org.apache.chemistry.opencmis.commons.definitions.PermissionDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.*;
import org.apache.chemistry.opencmis.commons.impl.server.ObjectInfoImpl;
import org.apache.chemistry.opencmis.commons.server.CallContext;

/**
 *
 * @author levy
 */
public class DocumentRepository extends BaseRepository
{

    private static final String REPOSITORY_ID = "document";
    private static final String CMIS_VERSION = "1.0";
    private static final String PRODUCT_NAME = "CMIS document module";
    private static final String PRODUCT_VERSION = "0.9";
    private static final String VENDOR_NAME = "Lutece";
    private static final String ROOT_ID = "@root@";
    private static final String SHADOW_EXT = ".cmis.xml";
    private static final String SHADOW_FOLDER = "cmis.xml";
    private static final String USER_UNKNOWN = "<unknown>";
    private static final String CMIS_READ = "cmis:read";
    private static final String CMIS_WRITE = "cmis:write";
    private static final String CMIS_ALL = "cmis:all";
    /**
     * Types
     */
    private final TypeManager types = new TypeManager();

    public RepositoryInfo getInfos()
    {
        RepositoryInfoImpl repositoryInfo = new RepositoryInfoImpl();

        repositoryInfo.setId(REPOSITORY_ID);
        repositoryInfo.setName(REPOSITORY_ID);
        repositoryInfo.setDescription(REPOSITORY_ID);

        repositoryInfo.setCmisVersionSupported(CMIS_VERSION);

        repositoryInfo.setProductName(PRODUCT_NAME);
        repositoryInfo.setProductVersion(PRODUCT_VERSION);
        repositoryInfo.setVendorName(VENDOR_NAME);

        repositoryInfo.setRootFolder(ROOT_ID);

        repositoryInfo.setThinClientUri("");

        RepositoryCapabilitiesImpl capabilities = new RepositoryCapabilitiesImpl();
        capabilities.setCapabilityAcl(CapabilityAcl.DISCOVER);
        capabilities.setAllVersionsSearchable(false);
        capabilities.setCapabilityJoin(CapabilityJoin.NONE);
        capabilities.setSupportsMultifiling(false);
        capabilities.setSupportsUnfiling(false);
        capabilities.setSupportsVersionSpecificFiling(false);
        capabilities.setIsPwcSearchable(false);
        capabilities.setIsPwcUpdatable(false);
        capabilities.setCapabilityQuery(CapabilityQuery.NONE);
        capabilities.setCapabilityChanges(CapabilityChanges.NONE);
        capabilities.setCapabilityContentStreamUpdates(CapabilityContentStreamUpdates.ANYTIME);
        capabilities.setSupportsGetDescendants(true);
        capabilities.setSupportsGetFolderTree(true);
        capabilities.setCapabilityRendition(CapabilityRenditions.NONE);

        repositoryInfo.setCapabilities(capabilities);

        AclCapabilitiesDataImpl aclCapability = new AclCapabilitiesDataImpl();
        aclCapability.setSupportedPermissions(SupportedPermissions.BASIC);
        aclCapability.setAclPropagation(AclPropagation.OBJECTONLY);

        // permissions
        List<PermissionDefinition> permissions = new ArrayList<PermissionDefinition>();
        permissions.add(createPermission(CMIS_READ, "Read"));
        permissions.add(createPermission(CMIS_WRITE, "Write"));
        permissions.add(createPermission(CMIS_ALL, "All"));
        aclCapability.setPermissionDefinitionData(permissions);

        // mapping
        List<PermissionMapping> list = new ArrayList<PermissionMapping>();
        list.add(createMapping(PermissionMapping.CAN_CREATE_DOCUMENT_FOLDER, CMIS_READ));
        list.add(createMapping(PermissionMapping.CAN_CREATE_FOLDER_FOLDER, CMIS_READ));
        list.add(createMapping(PermissionMapping.CAN_DELETE_CONTENT_DOCUMENT, CMIS_WRITE));
        list.add(createMapping(PermissionMapping.CAN_DELETE_OBJECT, CMIS_ALL));
        list.add(createMapping(PermissionMapping.CAN_DELETE_TREE_FOLDER, CMIS_ALL));
        list.add(createMapping(PermissionMapping.CAN_GET_ACL_OBJECT, CMIS_READ));
        list.add(createMapping(PermissionMapping.CAN_GET_ALL_VERSIONS_VERSION_SERIES, CMIS_READ));
        list.add(createMapping(PermissionMapping.CAN_GET_CHILDREN_FOLDER, CMIS_READ));
        list.add(createMapping(PermissionMapping.CAN_GET_DESCENDENTS_FOLDER, CMIS_READ));
        list.add(createMapping(PermissionMapping.CAN_GET_FOLDER_PARENT_OBJECT, CMIS_READ));
        list.add(createMapping(PermissionMapping.CAN_GET_PARENTS_FOLDER, CMIS_READ));
        list.add(createMapping(PermissionMapping.CAN_GET_PROPERTIES_OBJECT, CMIS_READ));
        list.add(createMapping(PermissionMapping.CAN_MOVE_OBJECT, CMIS_WRITE));
        list.add(createMapping(PermissionMapping.CAN_MOVE_SOURCE, CMIS_READ));
        list.add(createMapping(PermissionMapping.CAN_MOVE_TARGET, CMIS_WRITE));
        list.add(createMapping(PermissionMapping.CAN_SET_CONTENT_DOCUMENT, CMIS_WRITE));
        list.add(createMapping(PermissionMapping.CAN_UPDATE_PROPERTIES_OBJECT, CMIS_WRITE));
        list.add(createMapping(PermissionMapping.CAN_VIEW_CONTENT_OBJECT, CMIS_READ));
        Map<String, PermissionMapping> map = new LinkedHashMap<String, PermissionMapping>();
        for (PermissionMapping pm : list)
        {
            map.put(pm.getKey(), pm);
        }
        aclCapability.setPermissionMappingData(map);

        repositoryInfo.setAclCapabilities(aclCapability);

        return repositoryInfo;
    }

    /**
     * CMIS getTypesChildren.
     */
    public TypeDefinitionList getTypesChildren(CallContext context, String typeId, boolean includePropertyDefinitions,
            BigInteger maxItems, BigInteger skipCount)
    {
        return types.getTypesChildren(context, typeId, includePropertyDefinitions, maxItems, skipCount);
    }

    /**
     * CMIS getTypeDefinition.
     */
    public TypeDefinition getTypeDefinition(CallContext context, String typeId)
    {

        return types.getTypeDefinition(context, typeId);
    }

    public ObjectInFolderList getChildren(String folderId, String filter, String orderBy, Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount, ExtensionsData extension)
    {
        ObjectInFolderListImpl result = new ObjectInFolderListImpl();
        result.setObjects(new ArrayList<ObjectInFolderData>());
        result.setHasMoreItems(false);
        int count = 0;

        // skip and max
        int skip = (skipCount == null ? 0 : skipCount.intValue());
        if (skip < 0)
        {
            skip = 0;
        }

        int max = (maxItems == null ? Integer.MAX_VALUE : maxItems.intValue());
        if (max < 0)
        {
            max = Integer.MAX_VALUE;
        }



        // iterate through children

        for (Document child : DocumentHome.findAll())
        {
            // skip hidden and shadow files
            if (child.isOutOfDate() || !child.isValid())
            {
                continue;
            }

            count++;

            if (skip > 0)
            {
                skip--;
                continue;
            }

            if (result.getObjects().size() >= max)
            {
                result.setHasMoreItems(true);
                continue;
            }

            // build and add child object
            ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
            objectInFolder.setObject(getObject("" + child.getId(), filter, includeAllowableActions, includeRelationships, renditionFilter, includePathSegment, includePathSegment, extension));

            result.getObjects().add(objectInFolder);
        }

        result.setNumItems(BigInteger.valueOf(count));

        return result;
    }

    public ObjectData getObject(String objectId, String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds, Boolean includeAcl, ExtensionsData extension)
    {
        ObjectDataImpl result = new ObjectDataImpl();
        Document document = DocumentHome.findByPrimaryKey(Integer.parseInt(objectId));
        compileProperties(document, null, null);
        return result;
    }

    private org.apache.chemistry.opencmis.commons.data.Properties compileProperties(Document document, Set<String> orgfilter, ObjectInfoImpl objectInfo)
    {
        if (document == null)
        {
            throw new IllegalArgumentException("Document must not be null!");
        }


        // copy filter
        Set<String> filter = (orgfilter == null ? null : new HashSet<String>(orgfilter));

        // find base type
        String typeId = null;

        objectInfo.setBaseType(BaseTypeId.CMIS_DOCUMENT);
        objectInfo.setTypeId(typeId);
        objectInfo.setHasAcl(true);
        objectInfo.setHasContent(true);
        objectInfo.setHasParent(true);
        objectInfo.setVersionSeriesId(null);
        objectInfo.setIsCurrentVersion(true);
        objectInfo.setRelationshipSourceIds(null);
        objectInfo.setRelationshipTargetIds(null);
        objectInfo.setRenditionInfos(null);
        objectInfo.setSupportsDescendants(false);
        objectInfo.setSupportsFolderTree(false);
        objectInfo.setSupportsPolicies(false);
        objectInfo.setSupportsRelationships(false);
        objectInfo.setWorkingCopyId(null);
        objectInfo.setWorkingCopyOriginalId(null);

        // let's do it
        try
        {
            PropertiesImpl result = new PropertiesImpl();

            // id
            String id = "" + document.getId();
            addPropertyId(result, typeId, filter, PropertyIds.OBJECT_ID, id);
            objectInfo.setId(id);

            // name
            String name = document.getTitle();
            addPropertyString(result, typeId, filter, PropertyIds.NAME, name);
            objectInfo.setName(name);

            // created and modified by
            addPropertyString(result, typeId, filter, PropertyIds.CREATED_BY, USER_UNKNOWN);
            addPropertyString(result, typeId, filter, PropertyIds.LAST_MODIFIED_BY, USER_UNKNOWN);
            objectInfo.setCreatedBy(USER_UNKNOWN);

            // creation and modification date
            GregorianCalendar lastModified = millisToCalendar(document.getDateModification().getTime());
            addPropertyDateTime(result, typeId, filter, PropertyIds.CREATION_DATE, lastModified);
            addPropertyDateTime(result, typeId, filter, PropertyIds.LAST_MODIFICATION_DATE, lastModified);
            objectInfo.setCreationDate(lastModified);
            objectInfo.setLastModificationDate(lastModified);

            // change token - always null
            addPropertyString(result, typeId, filter, PropertyIds.CHANGE_TOKEN, null);


            // base type and type name
            addPropertyId(result, typeId, filter, PropertyIds.BASE_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());
            /*
             * addPropertyId(result, typeId, filter, PropertyIds.OBJECT_TYPE_ID,
             * TypeManager.DOCUMENT_TYPE_ID);
             *
             * // file properties addPropertyBoolean(result, typeId, filter,
             * PropertyIds.IS_IMMUTABLE, false); addPropertyBoolean(result,
             * typeId, filter, PropertyIds.IS_LATEST_VERSION, true);
             * addPropertyBoolean(result, typeId, filter,
             * PropertyIds.IS_MAJOR_VERSION, true); addPropertyBoolean(result,
             * typeId, filter, PropertyIds.IS_LATEST_MAJOR_VERSION, true);
             * addPropertyString(result, typeId, filter,
             * PropertyIds.VERSION_LABEL, file.getName()); addPropertyId(result,
             * typeId, filter, PropertyIds.VERSION_SERIES_ID, fileToId(file));
             * addPropertyBoolean(result, typeId, filter,
             * PropertyIds.IS_VERSION_SERIES_CHECKED_OUT, false);
             * addPropertyString(result, typeId, filter,
             * PropertyIds.VERSION_SERIES_CHECKED_OUT_BY, null);
             * addPropertyString(result, typeId, filter,
             * PropertyIds.VERSION_SERIES_CHECKED_OUT_ID, null);
             * addPropertyString(result, typeId, filter,
             * PropertyIds.CHECKIN_COMMENT, "");
             *
             * addPropertyInteger(result, typeId, filter,
             * PropertyIds.CONTENT_STREAM_LENGTH,
             * document.getXmlValidatedContent().length());
             * addPropertyString(result, typeId, filter,
             * PropertyIds.CONTENT_STREAM_MIME_TYPE,
             * MimeTypes.getMIMEType(file)); addPropertyString(result, typeId,
             * filter, PropertyIds.CONTENT_STREAM_FILE_NAME,
             * document.getTitle());
             *
             * objectInfo.setHasContent(true);
             * objectInfo.setContentType(MimeTypes.getMIMEType("application/xml"));
             * objectInfo.setFileName(file.getName());
             */

            addPropertyId(result, typeId, filter, PropertyIds.CONTENT_STREAM_ID, null);


            // read custom properties
            // readCustomProperties(file, result, filter, objectInfo);

            if (filter != null)
            {
                if (!filter.isEmpty())
                {
                    // debug("Unknown filter properties: " + filter.toString(), null);
                }
            }

            return result;
        }
        catch (Exception e)
        {
            if (e instanceof CmisBaseException)
            {
                throw (CmisBaseException) e;
            }
            throw new CmisRuntimeException(e.getMessage(), e);
        }
    }
}
