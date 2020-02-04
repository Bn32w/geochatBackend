package com.geochat.geochat.domain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Chat.
 */
@Document(collection = "chat")
public class Chat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("message")
    private String message;

    @Field("src_user")
    private String srcUser;

    @Field("dst_user")
    private String dstUser;

    @Field("date")
    private LocalDate date;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public Chat message(String message) {
        this.message = message;
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSrcUser() {
        return srcUser;
    }

    public Chat srcUser(String srcUser) {
        this.srcUser = srcUser;
        return this;
    }

    public void setSrcUser(String srcUser) {
        this.srcUser = srcUser;
    }

    public String getDstUser() {
        return dstUser;
    }

    public Chat dstUser(String dstUser) {
        this.dstUser = dstUser;
        return this;
    }

    public void setDstUser(String dstUser) {
        this.dstUser = dstUser;
    }

    public LocalDate getDate() {
        return date;
    }

    public Chat date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chat)) {
            return false;
        }
        return id != null && id.equals(((Chat) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Chat{" +
            "id=" + getId() +
            ", message='" + getMessage() + "'" +
            ", srcUser='" + getSrcUser() + "'" +
            ", dstUser='" + getDstUser() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
