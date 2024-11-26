package nesous.digital.services.familyBank.infra.audit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class DatabaseAuditable<String> {

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter
    protected Date creationDate;

    @Column(name = "updated_at", insertable = false)
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter
    protected Date lastModifiedDate;

    @Column(name = "created_by", nullable = false, updatable = false)
    @CreatedBy
    protected String createdBy;

    @Column(name = "updated_by", insertable = false)
    @LastModifiedBy
    protected String lastModifiedBy;
}
