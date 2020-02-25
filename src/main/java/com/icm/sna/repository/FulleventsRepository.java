package com.icm.sna.repository;

import com.icm.sna.entity.Fullevents;
import com.icm.sna.vo.Edge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FulleventsRepository extends JpaRepository<Fullevents, Integer> {
    @Query("select e from Fullevents e where e.matchId=?1 and e.eventTime>?2 and e.eventTime<?3 and e.matchPeriod like ?4")
    List<Fullevents> findByMatchId(int matchID, double start, double end, String part);

    @Query("select e.eventDestinationX-e.eventOriginX from Fullevents e where e.matchId=?1" +
            " and e.eventTime>?2 and e.eventTime<?3 and e.teamId like concat('%',?4,'%') and e.eventType not like '%Substitution%' and e.eventType not like 'Foul'")
    List<Double> findByTimedx(int matchID, double start, double end, String team);

    @Query("select e.eventDestinationY-e.eventOriginY from Fullevents e where e.matchId=?1 " +
            "and e.eventTime>?2 and e.eventTime<?3 and e.teamId like concat('%',?4,'%') and e.eventType not like '%Substitution%' and e.eventType not like 'Foul'")
    List<Double> findByTimedy(int matchID, double start, double end, String team);

    @Query("select e.eventOriginX from Fullevents e where e.matchId=?1 and e.eventTime>?2 " +
            "and e.eventTime<?3 and e.teamId like concat('%',?4,'%') and e.eventType not like '%Substitution%' and e.eventType not like 'Foul'")
    List<Double> findByTimeox(int matchID, double start, double end, String team);

    @Query("select e.eventOriginY from Fullevents e where e.matchId=?1 and e.eventTime>?2 " +
            "and e.eventTime<?3 and e.teamId like concat('%',?4,'%') and e.eventType not like '%Substitution%' and e.eventType not like 'Foul'")
    List<Double> findByTimeoy(int matchID, double start, double end, String team);

    //    @Query("select distinct e.originPlayerId from Fullevents e where e.matchId=?1")
    @Query("select distinct e.originPlayerId from Fullevents e where e.matchId =?1 and e.eventTime>?2 and e.eventTime<?3 and e.matchPeriod like ?4")
    List<String> findDistinctByOriginPlayerId(int matchID, double start, double end, String part);

    @Query("select distinct e.destinationPlayerId from Fullevents e where e.matchId=?1 and e.eventTime>?2 and e.eventTime<?3 and e.matchPeriod like ?4")
    List<String> findDistinctByDesPlayerId(int matchID, double start, double end, String part);

    @Query("select e from Fullevents e where e.matchId=?1 and e.eventType=?2")
    List<Fullevents> findByEventType(int matchId, String eventType);

    @Query("select e from Fullevents e where e.destinationPlayerId=?2 and e.originPlayerId=?1")
    List<Fullevents> findPasses(String origin, String dest);

    @Query("select distinct new com.icm.sna.vo.Edge(e.originPlayerId, e.destinationPlayerId) from Fullevents e where e.matchId=?1 and e.eventType=?2")
    List<Edge> findDistinctByPasses(int matchId, String type);

    @Query("select sum(f.eventDestinationX) from Fullevents f where f.matchId=?1 and f.originPlayerId=?2 and f.eventTime>?3 and f.eventTime<?4 and f.matchPeriod like ?5")
    Long sum_x(int matchId, String player, double start, double end, String part);

    @Query("select sum(f.eventDestinationY) from Fullevents f where f.matchId=?1 and f.originPlayerId=?2 and f.eventTime>?3 and f.eventTime<?4 and f.matchPeriod like ?5")
    Long sum_y(int matchId, String player, double start, double end, String part);

    @Query("select count(f) from Fullevents f where f.matchId=?1 and f.originPlayerId=?2 and f.eventTime>?3 and f.eventTime<?4 and f.matchPeriod like ?5")
    Long count_x(int matchId, String player, double start, double end, String part);
}

