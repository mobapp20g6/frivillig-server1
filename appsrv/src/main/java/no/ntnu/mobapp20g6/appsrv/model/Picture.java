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
    private Long id;

    @NotEmpty
    @JsonbTransient
    private String filePath;

    @JsonbTransient
    private Long fileSize;

    @JsonbTransient
    private String mimeType;


    // 1-1 REF
    @Getter
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "picture")
    private Group group;


    // 1-1 REF
    @Getter
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "picture")
    private Task task;
}
