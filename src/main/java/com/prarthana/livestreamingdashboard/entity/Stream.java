package com.prarthana.livestreamingdashboard.entity;



import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="streams")
public class Stream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String streamName;

    private String channelName;

    private String status;

    private String quality;

    private String location;

}
