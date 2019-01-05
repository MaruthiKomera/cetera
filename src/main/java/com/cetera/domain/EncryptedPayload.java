package com.cetera.domain;

import com.cetera.enums.SystemUser;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import java.util.Date;

/**
 * <b>EncryptedPayload</b> represents the encrypted domain request that internal
 * advisors of <b>Cetera</b> send to to <i>PeopleController</i>.
 * 
 * @author Radwan
 *
 */
@Entity
public class EncryptedPayload extends BaseDomain {
    
    
    public EncryptedPayload(String payload) {
        this.payload = payload;
    }

    public EncryptedPayload(String payload, Date createdOn) {
        this.payload = payload;
        this.setCreatedOn(createdOn);
        this.setCreatedBy(SystemUser.CETERA.name());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAYLOAD")
    @SequenceGenerator(name = "PAYLOAD", sequenceName = "ENCRYPTED_PAYLOAD_SEQ", allocationSize = 1)
    private Long id;

    @Lob
    @Basic
    private String payload;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
