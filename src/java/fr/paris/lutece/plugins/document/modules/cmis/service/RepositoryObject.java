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
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpaceHome;

import java.util.List;


/**
 * Repository object
 */
public class RepositoryObject
{
    private static final String STYPE_DOCUMENT = "D";
    private static final String STYPE_SPACE = "S";
    private static final int TYPE_DOCUMENT = 0;
    private static final int TYPE_SPACE = 1;
    private String _id;
    private Document _document;
    private DocumentSpace _space;
    private String _name;
    private int _type;

    RepositoryObject( String objectId )
    {
        _id = objectId;

        String strType = objectId.substring( 0, 1 );
        String strId = objectId.substring( 1 );
        int nId = Integer.parseInt( strId );

        if ( strType.equalsIgnoreCase( STYPE_DOCUMENT ) )
        {
            _type = TYPE_DOCUMENT;
            _document = DocumentHome.findByPrimaryKey( nId );
            _name = _document.getTitle(  );
        }
        else if ( strType.equalsIgnoreCase( STYPE_SPACE ) )
        {
            _type = TYPE_SPACE;
            _space = DocumentSpaceHome.findByPrimaryKey( nId );
            _name = _space.getName(  );
        }
    }

    /**
     *
     * @return
     */
    public String getId(  )
    {
        return _id;
    }

    /**
     *
     * @return
     */
    public String getName(  )
    {
        return _name;
    }

    /**
     *
     * @return
     */
    public boolean isDocument(  )
    {
        return _type == TYPE_DOCUMENT;
    }

    /**
     *
     * @return
     */
    public boolean isSpace(  )
    {
        return _type == TYPE_SPACE;
    }

    /**
     *
     * @return
     */
    public Document getDocument(  )
    {
        return _document;
    }

    /**
     *
     * @return
     */
    public List<Document> getDocumentChildren(  )
    {
        return DocumentHome.findBySpaceKey( _space.getId(  ) );
    }

    /**
     *
     * @return
     */
    public List<DocumentSpace> getSpaceChildren(  )
    {
        return DocumentSpaceHome.findChilds( _space.getId(  ) );
    }
}
