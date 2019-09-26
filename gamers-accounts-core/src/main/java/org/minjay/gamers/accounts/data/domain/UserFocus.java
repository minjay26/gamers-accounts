package org.minjay.gamers.accounts.data.domain;

import com.fasterxml.jackson.annotation.JsonView;
import org.minjay.gamers.accounts.data.jackson.DataView;

import javax.persistence.*;

@Entity
@Table(indexes = {@Index(name = "index_from_userId", columnList = "fromUserId"),
        @Index(name = "index_to_userId", columnList = "toUserId")})
public class UserFocus extends AbstractEntityAuditable<Long> {

    @JsonView(DataView.Basic.class)
    private Long fromUserId;
    @JsonView(DataView.Basic.class)
    private Long toUserId;

    @Id
    @GeneratedValue
    @Override
    public Long getId() {
        return super.getId();
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
}
