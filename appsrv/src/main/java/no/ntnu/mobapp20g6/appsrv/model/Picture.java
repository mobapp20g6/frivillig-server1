package no.ntnu.mobapp20g6.appsrv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Entity(name = "pictures")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Picture implements Serializable {

    @Id
    @Column(name="picture_id")
    private String id;

    @NotEmpty
    @JsonbTransient
    private String filePath;

    @JsonbTransient
    private Long fileSize;

    @JsonbTransient
    private String mimeType;

    public Picture(String id, @NotEmpty String filePath, Long fileSize, String mimeType) {
        this.id = id;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
    }

    // 1-1 REF
    @Getter
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "picture")
    private Group group;


    // 1-1 REF
    @Getter
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "picture")
    private Task task;
}
