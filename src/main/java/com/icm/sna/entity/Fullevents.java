package com.icm.sna.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Fullevents {
    private Integer matchId;
    private String teamId;
    private String originPlayerId;
    private String destinationPlayerId;
    private String matchPeriod;
    private Double eventTime;
    private String eventType;
    private String eventSubType;
    private Integer eventOriginX;
    private Integer eventOriginY;
    private Integer eventDestinationX;
    private Integer eventDestinationY;
    private int id;

    @Basic
    @Column(name = "MatchID")
    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    @Basic
    @Column(name = "teamid")
    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    @Basic
    @Column(name = "origin_playerid")
    public String getOriginPlayerId() {
        return originPlayerId;
    }

    public void setOriginPlayerId(String originPlayerId) {
        this.originPlayerId = originPlayerId;
    }

    @Basic
    @Column(name = "destination_playerid")
    public String getDestinationPlayerId() {
        return destinationPlayerId;
    }

    public void setDestinationPlayerId(String destinationPlayerId) {
        this.destinationPlayerId = destinationPlayerId;
    }

    @Basic
    @Column(name = "match_period")
    public String getMatchPeriod() {
        return matchPeriod;
    }

    public void setMatchPeriod(String matchPeriod) {
        this.matchPeriod = matchPeriod;
    }

    @Basic
    @Column(name = "event_time")
    public Double getEventTime() {
        return eventTime;
    }

    public void setEventTime(Double eventTime) {
        this.eventTime = eventTime;
    }

    @Basic
    @Column(name = "event_type")
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Basic
    @Column(name = "event_sub_type")
    public String getEventSubType() {
        return eventSubType;
    }

    public void setEventSubType(String eventSubType) {
        this.eventSubType = eventSubType;
    }

    @Basic
    @Column(name = "event_origin_x")
    public Integer getEventOriginX() {
        return eventOriginX;
    }

    public void setEventOriginX(Integer eventOriginX) {
        this.eventOriginX = eventOriginX;
    }

    @Basic
    @Column(name = "event_origin_y")
    public Integer getEventOriginY() {
        return eventOriginY;
    }

    public void setEventOriginY(Integer eventOriginY) {
        this.eventOriginY = eventOriginY;
    }

    @Basic
    @Column(name = "event_destination_x")
    public Integer getEventDestinationX() {
        return eventDestinationX;
    }

    public void setEventDestinationX(Integer eventDestinationX) {
        this.eventDestinationX = eventDestinationX;
    }

    @Basic
    @Column(name = "event_destination_y")
    public Integer getEventDestinationY() {
        return eventDestinationY;
    }

    public void setEventDestinationY(Integer eventDestinationY) {
        this.eventDestinationY = eventDestinationY;
    }

    @Id
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fullevents that = (Fullevents) o;
        return id == that.id &&
                Objects.equals(matchId, that.matchId) &&
                Objects.equals(teamId, that.teamId) &&
                Objects.equals(originPlayerId, that.originPlayerId) &&
                Objects.equals(destinationPlayerId, that.destinationPlayerId) &&
                Objects.equals(matchPeriod, that.matchPeriod) &&
                Objects.equals(eventTime, that.eventTime) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(eventSubType, that.eventSubType) &&
                Objects.equals(eventOriginX, that.eventOriginX) &&
                Objects.equals(eventOriginY, that.eventOriginY) &&
                Objects.equals(eventDestinationX, that.eventDestinationX) &&
                Objects.equals(eventDestinationY, that.eventDestinationY);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, teamId, originPlayerId, destinationPlayerId, matchPeriod, eventTime, eventType, eventSubType, eventOriginX, eventOriginY, eventDestinationX, eventDestinationY, id);
    }
}
