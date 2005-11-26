/*
 * DBBrowser is software for browsing the structure and contents of databases.
 * Copyright (C) 2001 Philip A. Chapman
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the
 *
 *                     Free Software Foundation, Inc.
 *                    51 Franklin Street, Fifth Floor
 *                      Boston, MA  02110-1301, USA.
 */
package us.pcsw.dbbrowser.cp;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.DatabaseMetaData;

import us.pcsw.util.IntegerUtil;
import us.pcsw.util.StringUtil;

/**
 * us.pcsw.dbbrowser.dataimport.DataType
 * -
 * A database data type.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Mar 10, 2005 This class was created by pchapman.</LI>
 * </UL></P>
 */
public final class DataType
{
	// CONSTANTS
	
	// Return values for the getNullable() method.
	
	/**
	 * A field of this type is allowed to be null.
	 */
	public static final int NULLABLE = DatabaseMetaData.typeNullable;
	/**
	 * A field of this type cannot be null.
	 */
	public static final int NULLABLE_NOT = DatabaseMetaData.typeNoNulls;
	/**
	 * It is unknown whether a field of this type can be null.
	 */
	public static final int NULLABLE_UNKNOWN = DatabaseMetaData.typeNullableUnknown;
	
	// Return values for the getSearchable() method.
	
	/**
	 * A field of this type is fully supported in the SQL WHERE clause.
	 */
	public static final int SEARCHABLE = DatabaseMetaData.typeSearchable;
	/**
	 * A field of this type has no support in the SQL WHERE clause.
	 */
	public static final int SEARCHABLE_NONE = DatabaseMetaData.typePredNone;
	/**
	 * A field of this type has basic support in the SQL WHERE clause.
	 * Supported except for WHERE ... LIKE.
	 */
	public static final int SEARCHABLE_BASIC = DatabaseMetaData.typePredBasic;
	/**
	 * A field of this type has character support in the SQL WHERE clause.
	 * Supported only for WHERE ... LIKE.
	 */
	public static final int SEARCHABLE_CHAR = DatabaseMetaData.typePredChar;

	// CONSTRUCTORS
	
	DataType()
	{
		super();
	}
	
	DataType(ResultSet resultSet)
		throws SQLException
	{
		/*
		 *	1 TYPE_NAME String => Type name
		 *	2 DATA_TYPE int => SQL data type from java.sql.Types
		 *	3 PRECISION int => maximum precision
		 *	4 LITERAL_PREFIX String => prefix used to quote a literal (may be null)
		 *	5 LITERAL_SUFFIX String => suffix used to quote a literal (may be null)
		 *	6 CREATE_PARAMS String => parameters used in creating the type (may be null)
		 *	7 NULLABLE short => can you use NULL for this type.
		 *	
		 *	    * typeNoNulls - does not allow NULL values
		 *	    * typeNullable - allows NULL values
		 *	    * typeNullableUnknown - nullability unknown 
		 *	
		 *	8 CASE_SENSITIVE boolean=> is it case sensitive.
		 *	9 SEARCHABLE short => can you use "WHERE" based on this type:
		 *	
		 *	    * typePredNone - No support
		 *	    * typePredChar - Only supported with WHERE .. LIKE
		 *	    * typePredBasic - Supported except for WHERE .. LIKE
		 *	    * typeSearchable - Supported for all WHERE .. 
		 *	
		 *	10 UNSIGNED_ATTRIBUTE boolean => is it unsigned.
		 *	11 FIXED_PREC_SCALE boolean => can it be a money value.
		 *	12 AUTO_INCREMENT boolean => can it be used for an auto-increment value.
		 *	13 LOCAL_TYPE_NAME String => localized version of type name (may be null)
		 *	14 MINIMUM_SCALE short => minimum scale supported
		 *	15 MAXIMUM_SCALE short => maximum scale supported
		 *	16 SQL_DATA_TYPE int => unused
		 *	17 SQL_DATETIME_SUB int => unused
		 *	18 NUM_PREC_RADIX int => usually 2 or 10 
		 */
		typeName = resultSet.getString(1);
		jdbcType = resultSet.getInt(2);
		maxPrecision = resultSet.getInt(3);
		literalPrefix = StringUtil.getNullableString(resultSet, 4);
		literalSuffix = StringUtil.getNullableString(resultSet, 5);
		parameters = StringUtil.getNullableString(resultSet, 6);
		nullable = resultSet.getInt(7);
		caseSensitive = resultSet.getBoolean(8);
		searchable = resultSet.getInt(9);
		unsignedAttr = resultSet.getBoolean(10);
		fixedPrecisionScale = resultSet.getBoolean(11);
		autoInc = resultSet.getBoolean(12);
		typeNameLocalized = StringUtil.getNullableString(resultSet, 13);
		minScale = IntegerUtil.getNullableInteger(resultSet, 14);
		maxScale = IntegerUtil.getNullableInteger(resultSet, 15);
		// 16 unused
		// 17 unused
		numericPrecisionRadix = resultSet.getInt(18);
	}
	
	// METHODS
	
	/*
	* AUTO_INCREMENT boolean => can it be used for an auto-increment value.
	*/
	private boolean autoInc;
	public boolean isAutoIncrement()
	{
		return autoInc;
	}

	/*
	 * CASE_SENSITIVE boolean=> is it case sensitive.
	 */
	private boolean caseSensitive;
	public boolean isCaseSensitive()
	{
		return caseSensitive;
	}
	
	/*
	 * FIXED_PREC_SCALE boolean => can it be a money value.
	 */
	private boolean fixedPrecisionScale;
	public boolean isFixedPrecisionScale()
	{
		return fixedPrecisionScale;
	}

	/*
	 * DATA_TYPE int => SQL data type from java.sql.Types
	 */
	private int jdbcType;
	public int getJDBCType()
	{
		return jdbcType;
	}
	
	/*
	 * LITERAL_PREFIX String => prefix used to quote a literal (may be null)
	 */
	private String literalPrefix;
	public String getLiteralPrefix()
	{
		return literalPrefix;
	}
	
	/*
	 * LITERAL_SUFFIX String => suffix used to quote a literal (may be null)
	 */
	private String literalSuffix;
	public String getLiteralSuffix()
	{
		return literalSuffix;
	}
	
	/*
	 * PRECISION int => maximum precision
	 */
	private int maxPrecision;
	public int getMaxPrecision()
	{
		return maxPrecision;
	}
	
	/*
	 * NULLABLE short => can you use NULL for this type.
	 *
	 * typeNoNulls - does not allow NULL values
	 * typeNullable - allows NULL values
	 * typeNullableUnknown - nullability unknown
	 */
	private int nullable;
	public int getNullable()
	{
		return nullable;
	}
	
	/*
	 * CREATE_PARAMS String => parameters used in creating the type (may be null)
	 */
	private String parameters;
	public String getParameters()
	{
		return parameters;
	}
	
	private Boolean precisionRequired;
	public Boolean isPrecisionRequired()
	{
		return precisionRequired;
	}
	void setPrecisionRequired(Boolean required)
	{
		this.precisionRequired = required;
	}
	
	private Boolean scaleRequired;
	public Boolean isScaleRequired()
	{
		return scaleRequired;
	}
	void setScaleRequired(Boolean required)
	{
		this.scaleRequired = required;
	}
	
	/*
	 * SEARCHABLE short => can you use "WHERE" based on this type:
	 * typePredNone - No support
	 * typePredChar - Only supported with WHERE .. LIKE
	 * typePredBasic - Supported except for WHERE .. LIKE
	 * typeSearchable - Supported for all WHERE ..
	 */
	private int searchable;
	public int getSearchable()
	{
		return searchable;
	}
	
	/*
	 * * TYPE_NAME String => Type name
	 */
	private String typeName;
	public String getTypeName()
	{
		return typeName;
	}
	
	/*
	 * LOCAL_TYPE_NAME String => localized version of type name (may be null)
	 */
	private String typeNameLocalized;
	public String getTypeNameLocalized()
	{
		return typeNameLocalized;
	}
	
	/*
	 * UNSIGNED_ATTRIBUTE boolean => is it unsigned.
	 */
	private boolean unsignedAttr;
	public boolean isUnssignedAttributeSupported()
	{
		return unsignedAttr;
	}
	
	/*
	 * MINIMUM_SCALE short => minimum scale supported
	 */
	private Integer minScale;
	public Integer getMinimumScale()
	{
		return minScale;
	}
	
	/*
	 * MAXIMUM_SCALE short => maximum scale supported
	 */
	private Integer maxScale;
	public Integer getMaximumScale()
	{
		return maxScale;
	}
	
	/*
	 * NUM_PREC_RADIX int => usually 2 or 10
	 */
	private int numericPrecisionRadix;
	public int getNumericPrecisionRadix()
	{
		return numericPrecisionRadix;
	}
	
	// METHODS
	
	public String toString()
	{
		return typeNameLocalized == null ? getTypeName() : typeNameLocalized;
	}
}
