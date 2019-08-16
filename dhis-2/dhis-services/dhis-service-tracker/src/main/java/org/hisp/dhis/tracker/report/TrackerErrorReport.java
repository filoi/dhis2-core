package org.hisp.dhis.tracker.report;

/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.MoreObjects;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.tracker.TrackerErrorCode;
import org.hisp.dhis.tracker.TrackerErrorMessage;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "errorReport", namespace = DxfNamespaces.DXF_2_0 )
public class TrackerErrorReport
{
    protected final TrackerErrorMessage message;

    protected final Class<?> mainKlass;

    protected String mainId;

    protected Class<?> errorKlass;

    protected String errorProperty;

    protected Object value;

    public TrackerErrorReport( Class<?> mainKlass, TrackerErrorCode errorCode, Object... args )
    {
        this.mainKlass = mainKlass;
        this.message = new TrackerErrorMessage( errorCode, args );
    }

    public TrackerErrorReport( Class<?> mainKlass, TrackerErrorMessage message )
    {
        this.mainKlass = mainKlass;
        this.message = message;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public TrackerErrorCode getErrorCode()
    {
        return message.getErrorCode();
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getMessage()
    {
        return message.getMessage();
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Class<?> getMainKlass()
    {
        return mainKlass;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getMainId()
    {
        return mainId;
    }

    public TrackerErrorReport setMainId( String mainId )
    {
        this.mainId = mainId;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Class<?> getErrorKlass()
    {
        return errorKlass;
    }

    public TrackerErrorReport setErrorKlass( Class<?> errorKlass )
    {
        this.errorKlass = errorKlass;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getErrorProperty()
    {
        return errorProperty;
    }

    public TrackerErrorReport setErrorProperty( String errorProperty )
    {
        this.errorProperty = errorProperty;
        return this;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Object getValue()
    {
        return value;
    }

    public TrackerErrorReport setValue( Object value )
    {
        this.value = value;
        return this;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this )
            .add( "message", getMessage() )
            .add( "errorCode", message.getErrorCode() )
            .add( "mainKlass", mainKlass )
            .add( "errorKlass", errorKlass )
            .add( "value", value )
            .toString();
    }
}
