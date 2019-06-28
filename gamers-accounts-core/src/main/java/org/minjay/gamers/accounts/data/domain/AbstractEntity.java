package org.minjay.gamers.accounts.data.domain;

import org.springframework.data.domain.Persistable;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Simple interface for entities.
 */
public abstract class AbstractEntity<ID extends Serializable> implements Persistable<ID>, Serializable {

    private static final long serialVersionUID = -7141570524683041372L;

    protected ID id;

    /**
     * Returns the id of the entity.
     *
     * @return the id
     */
    public ID getId() {
        return id;
    }

    /**
     * Sets the id of the entity.
     *
     * @param id the id to set
     */
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * Returns if the entity is new or was persisted already.
     *
     * @return if the object is new
     */
    @Transient
    public boolean isNew() {
        return null == getId();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        AbstractEntity<?> that = (AbstractEntity<?>) obj;

        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }
}
