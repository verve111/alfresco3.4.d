
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="updateDraftReturn" type="{http://webservices.clb.content.ibm.com}ClbResponse"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "updateDraftReturn"
})
@XmlRootElement(name = "updateDraftResponse")
public class UpdateDraftResponse {

    @XmlElement(required = true)
    protected ClbResponse updateDraftReturn;

    /**
     * Gets the value of the updateDraftReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbResponse }
     *     
     */
    public ClbResponse getUpdateDraftReturn() {
        return updateDraftReturn;
    }

    /**
     * Sets the value of the updateDraftReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbResponse }
     *     
     */
    public void setUpdateDraftReturn(ClbResponse value) {
        this.updateDraftReturn = value;
    }

}
