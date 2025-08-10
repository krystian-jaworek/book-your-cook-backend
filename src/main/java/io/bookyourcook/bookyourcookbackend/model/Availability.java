package io.bookyourcook.bookyourcookbackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Document(collection = "availabilities")
public class Availability {
    @Id
    private String id;
    private String cookId;
    private LocalDate date;
    private String from;
    private String to;
}