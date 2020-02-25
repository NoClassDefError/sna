package com.icm.sna.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static java.lang.Double.POSITIVE_INFINITY;

@Entity
@Data
public class Info {
    @Id
    @GeneratedValue
    @Column(name="id")
    private Integer id;
    private Integer matchId;
    private Double start;
    private Double end;
    private String part;
    private String team;
    private String adjMatrix;
    private Integer passingTimes;
    private Double centerX;
    private Double centerY;
    private Double shortest;
    private Double coefficient;
    private Double aveDed;
    private Double lambda;
    private Double lambdaPos;
    private Double desity;

    public void checkInf(){
        if(Double.isInfinite(shortest))shortest=-1d;
        if(Double.isInfinite(coefficient))coefficient=-1d;
        if(Double.isInfinite(aveDed))aveDed=-1d;
        if(Double.isInfinite(lambda))lambda=-1d;
        if(Double.isInfinite(lambdaPos))lambdaPos=-1d;
        if(Double.isInfinite(desity))desity=-1d;
    }
}
