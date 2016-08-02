package com.atmire.util.subclasses;

import com.atmire.util.helper.MetadataFieldString;
import org.apache.commons.lang3.StringUtils;
import org.dspace.content.DCValue;
import org.dspace.content.Item;
import org.dspace.content.authority.Choices;

/**
* Created by: Antoine Snyers (antoine at atmire dot com)
* Date: 19 Sep 2014
*/
public class MetadatumExtended extends DCValue {

    /**
    * @param metadataFieldString schema.element.qualifier[language]::authority::confidence
    */
    public MetadatumExtended(String metadataFieldString, String value) {
        this(MetadataFieldString.encapsulate(metadataFieldString), value);
    }

    public MetadatumExtended(DCValue field, String value){
        this(field.schema, field.element, field.qualifier, field.language, value, field.authority, field.confidence);
    }

    public MetadatumExtended(DCValue dcValue) {
        this(dcValue.schema, dcValue.element, dcValue.qualifier, dcValue.language, dcValue.value, dcValue.authority, dcValue.confidence);
    }

    public MetadatumExtended(String schema, String element, String qualifier, String language, String value, String authority, int confidence) {
        this.schema = schema;
        this.element = element;
        this.qualifier = qualifier;
        this.language = language;
        this.value = value;
        this.authority = authority;
        this.confidence = confidence;
    }

    public MetadatumExtended() {

    }

    public String getSchema() {
        return schema;
    }


    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public MetadatumExtended withWildcards() {
        String wilcard = Item.ANY;
        String schema1 = StringUtils.isBlank(schema) ? wilcard : schema;
        String element1 = StringUtils.isBlank(element) ? wilcard : element;
        String qualifier1 = StringUtils.isBlank(qualifier) ? wilcard : qualifier;
        String language1 = StringUtils.isBlank(language) ? wilcard : language;
        return new MetadatumExtended(schema1, element1, qualifier1, language1, value, authority, confidence);
    }

    public MetadatumExtended filledWith(DCValue dcValue) {
        String schema1 = StringUtils.isBlank(schema) ? dcValue.schema : schema;
        String element1 = StringUtils.isBlank(element) ? dcValue.element : element;
        String qualifier1 = StringUtils.isBlank(qualifier) ? dcValue.qualifier : qualifier;
        String language1 = StringUtils.isBlank(language) ? dcValue.language : language;
        String value1 = StringUtils.isBlank(value) ? dcValue.value : value;
        String authority1 = StringUtils.isBlank(authority) ? dcValue.authority : authority;
        int confidence1 = confidence == Choices.CF_UNSET ? dcValue.confidence : confidence;
        return new MetadatumExtended(schema1, element1, qualifier1, language1, value1, authority1, confidence1);
    }

    public MetadatumExtended filledWithExceptField(DCValue dcValue) {
        String language1 = StringUtils.isBlank(language) ? dcValue.language : language;
        String value1 = StringUtils.isBlank(value) ? dcValue.value : value;
        String authority1 = StringUtils.isBlank(authority) ? dcValue.authority : authority;
        int confidence1 = confidence == Choices.CF_UNSET ? dcValue.confidence : confidence;
        return new MetadatumExtended(schema, element, qualifier, language1, value1, authority1, confidence1);
    }

    public MetadatumExtended copy() {
        MetadatumExtended copy = new MetadatumExtended();
        copy.value = this.value;
        copy.authority = this.authority;
        copy.confidence = this.confidence;
        copy.element = this.element;
        copy.language = this.language;
        copy.qualifier = this.qualifier;
        copy.schema = this.schema;
        return copy;
    }

    /**
     * Get the name of the field in dot notation:  schema.element.qualifier,
     * as in {@code dc.date.issued}.
     *
     * @return stringified name of this field.
     */
    public String getField() {
        return schema + "." + element + (qualifier == null ? "" : ("." + qualifier));
    }

    public boolean hasSameFieldAs(DCValue dcValue) {
        if (dcValue == this) {
            return true;
        }
        if (dcValue.element != null ? !dcValue.element.equals(this.element) : this.element != null) {
            return false;
        }
        if (dcValue.qualifier != null ? !dcValue.qualifier.equals(this.qualifier) : this.qualifier != null) {
            return false;
        }
        if (dcValue.schema != null ? !dcValue.schema.equals(this.schema) : this.schema != null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DCValue dcValue = (DCValue) o;

        if (confidence != dcValue.confidence) {
            return false;
        }
        if (authority != null ? !authority.equals(dcValue.authority) : dcValue.authority != null) {
            return false;
        }
        if (element != null ? !element.equals(dcValue.element) : dcValue.element != null) {
            return false;
        }
        if (language != null ? !language.equals(dcValue.language) : dcValue.language != null) {
            return false;
        }
        if (qualifier != null ? !qualifier.equals(dcValue.qualifier) : dcValue.qualifier != null) {
            return false;
        }
        if (schema != null ? !schema.equals(dcValue.schema) : dcValue.schema != null) {
            return false;
        }
        if (value != null ? !value.equals(dcValue.value) : dcValue.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = element != null ? element.hashCode() : 0;
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (authority != null ? authority.hashCode() : 0);
        result = 31 * result + confidence;
        return result;
    }
}
