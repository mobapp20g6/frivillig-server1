package no.ntnu.mobapp20g6.appsrv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Entity(name = "pictures")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Picture implements Serializable {

    @Id
    private Long id;

    @NotEmpty
    @JsonbTransient
    private String filePath;

    @JsonbTransient
    private Long fileSize;

    @JsonbTransient
    private String mimeType;
}
