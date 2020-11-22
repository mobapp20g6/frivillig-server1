package no.ntnu.mobapp20g6.appsrv.model;

import lombok.*;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Entity(name = "pictures")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude={"group","task"})
public class Picture implements Serializable {

    @Id
    @Column(name="picture_id")
    private String id;

    @NotEmpty
    @JsonbTransient
    private String fileName;

    @JsonbTransient
    private Long fileSize;

    @JsonbTransient
    private String mimeType;

    public Picture(String id, @NotEmpty String fileName, Long fileSize, String mimeType) {
        this.id = id;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
    }

    // 1-1 REF
    @Getter
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "picture")
    @JsonbTransient
    private Group group;


    // 1-1 REF
    @Getter
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "picture")
    @JsonbTransient
    private Task task;
}
