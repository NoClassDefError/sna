package com.icm.sna.service;

import com.icm.sna.entity.Info;
import com.icm.sna.repository.FulleventsRepository;
import com.icm.sna.repository.InfoRepository;
import com.icm.sna.vo.Edge;
import com.icm.sna.vo.MatrixDG;
import com.mathworks.engine.MatlabEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 本程序的核心业务逻辑
 */
@Service
public class EventService {

    @Autowired
    private FulleventsRepository fulleventsRepository;

    @Autowired
    private MatlabEngine engine;

    @Autowired
    private InfoRepository infoRepository;

    public void getPassingEvent(int matchId, double time, double stays, String part) {
        System.out.println("Match " + matchId + " " + part + " " + time + " " + stays);
        Info info = new Info();
        Info info1 = new Info();
        info.setTeam("Huskies");
        info.setStart(time);
        info.setEnd(time + stays);
        info.setMatchId(matchId);
        info.setPart(part);
        info1.setTeam("Opponents");
        info1.setStart(time);
        info1.setEnd(time + stays);
        info1.setMatchId(matchId);
        info1.setPart(part);
        //查询该比赛中有哪些队员，并分成两队
        List<String> huskies = new ArrayList<>();
        List<String> oppos = new ArrayList<>();
        fulleventsRepository.findDistinctByOriginPlayerId(matchId, time, time + stays, part).forEach(s -> {
            if (s != null && !s.isEmpty()) {
                if (s.contains("Huskies")) huskies.add(s);
                if (s.contains("Opponent")) oppos.add(s);
            }
        });
        fulleventsRepository.findDistinctByDesPlayerId(matchId, time, time + stays, part).forEach(s -> {
            if (s != null && !s.isEmpty()) {
                if (s.contains("Huskies")) if (!huskies.contains(s)) huskies.add(s);
                if (s.contains("Opponent")) if (!oppos.contains(s)) oppos.add(s);
            }
        });
        //遍历这些队员，分别输出他们的平均位置
        System.out.println("huskies: " + huskies);
        Locations husLoc = printLoc(matchId, huskies, time, time + stays, part);
        System.out.println("opponent: " + oppos);
        Locations oppoLoc = printLoc(matchId, oppos, time, time + stays, part);

        //查询传球记录
        List<Edge> edgesHus = new ArrayList<>();
        List<Edge> edgesOppo = new ArrayList<>();
//        fulleventsRepository.findDistinctByPasses(matchId, "Pass").forEach(edge -> {
//            if (edge.getDestination() != null) {
//                fulleventsRepository.findPasses(edge.getOrigin(), edge.getDestination()).forEach(fullevents -> {
//                    if (fullevents.getTeamId().contains("Huskies")) edgesHus.add(edge);
//                    if (fullevents.getTeamId().contains("Opponent")) edgesOppo.add(edge);
//                });
//            }
//        });
        fulleventsRepository.findByMatchId(matchId, time, time + stays, part).forEach(fullevents -> {
            if (fullevents.getOriginPlayerId() != null && fullevents.getDestinationPlayerId() != null) {
                if (fullevents.getTeamId().contains("Huskies"))
                    edgesHus.add(new Edge(fullevents.getOriginPlayerId(), fullevents.getDestinationPlayerId()));
                if (fullevents.getTeamId().contains("Opponent"))
                    edgesOppo.add(new Edge(fullevents.getOriginPlayerId(), fullevents.getDestinationPlayerId()));
//                if (fullevents.getOriginPlayerId().equals(fullevents.getDestinationPlayerId()))
//                    System.err.println(fullevents.getId());
            }
        });

        System.out.println("Huskies' passing times:" + edgesHus.size());
        info.setPassingTimes(edgesHus.size());
        System.out.println("Opponent's passing times:" + edgesOppo.size());
        info1.setPassingTimes(edgesOppo.size());
        //生成邻接矩阵并输出
        System.out.println("Huskies matrix in the match " + matchId);
        String matH = new MatrixDG(huskies, edgesHus).print();
        info.setAdjMatrix(matH);

        System.out.println("Opponents matrix in the match " + matchId);
        String matO = new MatrixDG(oppos, edgesOppo).print();
        info1.setAdjMatrix(matO);

        //启动matlab引擎，画图
        System.out.println("Matlab starting...");
        try {
            engine.eval("matH=" + matH);//哈士奇队的邻接矩阵
            engine.eval("matO=" + matO);//对面的邻接矩阵
            engine.eval("X=" + husLoc.avex + ";");//哈士奇队球员X坐标向量
            engine.eval("Y=" + husLoc.avey + ";");
            engine.eval("X2=" + oppoLoc.avex + ";");
            engine.eval("Y2=" + oppoLoc.avey + ";");

            System.out.println("点度中心度");
            engine.eval("sum(matH)");

            System.out.println("哈士奇队伍中心点位置");
            engine.eval("siz=size(X);\n");
            engine.eval("cenx=sum(X)/siz(2)\n");
            info.setCenterX(engine.getVariable("cenx"));
            engine.eval("ceny=sum(Y)/siz(2)\n");
            info.setCenterY(engine.getVariable("ceny"));
            engine.eval("density=mean(((X-cenx).^2+(Y-ceny).^2).^0.5)");
            info.setDesity(engine.getVariable("density"));
            System.out.println("对手队伍中心点位置");
            engine.eval("siz=size(X2);\n");
            engine.eval("cenx=sum(X2)/siz(2)\n");
            info1.setCenterX(engine.getVariable("cenx"));
            engine.eval("ceny=sum(Y2)/siz(2)\n");
            info1.setCenterY(engine.getVariable("ceny"));
            engine.eval("density=mean(((X2-cenx).^2+(Y2-ceny).^2).^0.5)");
            info1.setDesity(engine.getVariable("density"));

            System.out.println("哈士奇特征向量与特征值矩阵");
            engine.eval("[V,D]=eig(matH)");//求特征向量与特征值矩阵
            System.out.println("特征向量最大值和位置");
            engine.eval("[Lambda,i]=max(diag(D))");//特征向量最大值和位置
            info.setLambda(engine.getVariable("Lambda"));
            info.setLambdaPos(engine.getVariable("i"));
            System.out.println("对手特征向量与特征值矩阵");
            engine.eval("[V,D]=eig(matO);");//求特征向量与特征值矩阵
            System.out.println("特征向量最大值和位置");
            engine.eval("[Lambda,i]=max(diag(D))");//特征向量最大值和位置
            info1.setLambda(engine.getVariable("Lambda"));
            info1.setLambdaPos(engine.getVariable("i"));

            System.out.println("求平均最短路径");
            engine.eval("aa=[];\n" +
                    "    siz=size(matH);\n" +
                    "    siz=siz(1);\n" +
                    "    for k = 1:siz\n" +
                    "        aa=[aa;graphshortestpath(sparse(matH),k)];\n" +
                    "    end\n" +
                    "    s=sum(sum(aa))/(siz*(siz-1));");
            Double s = engine.getVariable("s");
            info.setShortest(s);
            engine.eval("aa=[];\n" +
                    "    siz=size(matO);\n" +
                    "    siz=siz(1);\n" +
                    "    for k = 1:siz\n" +
                    "        aa=[aa;graphshortestpath(sparse(matO),k)];\n" +
                    "    end\n" +
                    "    s=sum(sum(aa))/(siz*(siz-1));");
            info1.setShortest(engine.getVariable("s"));
            System.out.println("计算集聚系数");
            info.setCoefficient(coefficient("matH"));
            info1.setCoefficient(coefficient("matO"));
            System.out.println("计算度分布");
            info.setAveDed(degree("matH"));
            info1.setAveDed(degree("matO"));
            info.checkInf();
            info1.checkInf();
            infoRepository.save(info);
            infoRepository.save(info1);
//            getPassingEvent2(matchId, time, stays);
//            drawNodes();
            drawDigraph();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * DeD————————网络图各节点的度分布
     * aver_DeD———————网络图的平均度
     *
     * @param s 邻接矩阵名称
     */
    private Double degree(String s) throws ExecutionException, InterruptedException {
        engine.eval("N=size(" + s + ",2);\n" +
                "DeD=zeros(1,N)\n" +
                "for i=1:N\n" +
                "   DeD(i)=sum(" + s + "(i,:));\n" +
                "end\n" +
                "aver_DeD=mean(DeD)");
        return engine.getVariable("aver_DeD");
    }

    /**
     * 使用matlab计算集聚系数
     *
     * @param s 邻接矩阵在matlab中的名称
     */
    private Double coefficient(String s) throws ExecutionException, InterruptedException {
        engine.eval("trace=" + s +
                "\n N = size(trace,1); % number of nodes\n" +
                "clustering_nodes = zeros(N,1);\n" +
                "for i = 1:N\n" +
                "    t_transitive = [];\n" +
                "    for t1 = 1:size(trace,3)\n" +
                "        neighbors = setdiff(find(trace(i,:,t1)==1),i);\n" +
                "        if ~isempty(neighbors)\n" +
                "            for k = 1:length(neighbors)\n" +
                "                for t2 = t1:size(trace,3)\n" +
                "                    neighbors_of_neighbor = setdiff(find(trace(neighbors(k),:,t2)==1),[neighbors(k) i]);\n" +
                "                    if ~isempty(neighbors_of_neighbor)\n" +
                "                        for l = 1:length(neighbors_of_neighbor)\n" +
                "                            t3 = find(trace(neighbors_of_neighbor(l),i,t2:end)==1,1);\n" +
                "                            if ~isempty(t3)\n" +
                "                                t_transitive = [t_transitive t3];\n" +
                "                            end\n" +
                "                        end\n" +
                "                        break;\n" +
                "                    end\n" +
                "                end\n" +
                "            end\n" +
                "            break;\n" +
                "        end\n" +
                "    end\n" +
                "   \n" +
                "    if isempty(t_transitive)\n" +
                "        clustering_nodes(i) = 0;\n" +
                "    else\n" +
                "        clustering_nodes(i) = 1/min(t_transitive);\n" +
                "    end\n" +
                "end\n" +
                "c = mean(clustering_nodes)\n");
        return engine.getVariable("c");
    }

    private void drawNodes() throws ExecutionException, InterruptedException {
        engine.eval("scatter(X,Y,[],[0,1,0],'filled');\n" +
                "hold on;\n" +
                "scatter(X2,Y2,[],[1,0,0],'filled');\n" +
                "siz=size(X);\n" +
                "sum(X)/siz(2)" +
                "sum(Y)/siz(2)" +
                "scatter(sum(X)/siz(2),sum(Y)/siz(2),[],[0,0.5,0],'filled');\n" +
                "siz=size(X2);\n" +
                "scatter(sum(X2)/siz(2),sum(Y2)/siz(2),[],[0.5,0,0],'filled');\n" +
                "%" +
                "a1=full(adjacency(graph(int8(X),int8(Y),(X.^2+Y.^2).^0.5),'weighted'));\n" +
                "%" +
                "a2=full(adjacency(graph(int8(X2),int8(Y2),(X2.^2+Y2.^2).^0.5),'weighted'));\n" +
                "    " +
                "siz=size(X);\n" +
                "    result=[];\n" +
                "    for ii=[1:siz(2)]\n" +
                "        for jj=[1:siz(2)]\n" +
                "            result(ii,jj)=(X(ii).^2+Y(jj).^2).^0.5;\n" +
                "        end\n" +
                "    end\n" +
                "    a1=result\n" +
                "    " +
                "siz=size(X2);\n" +
                "    result=[];\n" +
                "    for ii=[1:siz(2)]\n" +
                "        for jj=[1:siz(2)]\n" +
                "            result(ii,jj)=(X2(ii).^2+Y2(jj).^2).^0.5;\n" +
                "        end\n" +
                "    end\n" +
                "    a2=result"
        );
        coefficient("a1");
        coefficient("a2");
    }

    private void drawDigraph() throws ExecutionException, InterruptedException {
        //画有向图
        engine.eval("G=digraph(matH);");
        engine.eval("G2=digraph(matO);");
        engine.eval("p=plot(gca,G,'XData',X,'YData',Y)");
        engine.eval("hold on");
        engine.eval("p2=plot(gca,G2,'XData',X2,'YData',Y2)");
        //设置线宽粗细
        engine.eval("G.Nodes.NodeColors = outdegree(G);\n" +
                "p.NodeCData = G.Nodes.NodeColors;\n" +
                "G2.Nodes.NodeColors = outdegree(G2);\n" +
                "p2.NodeCData = G2.Nodes.NodeColors;\n" +
                "colorbar");
        engine.eval("p.LineWidth = 7*G.Edges.Weight/max(G.Edges.Weight);");
        engine.eval("p2.LineWidth = 7*G2.Edges.Weight/max(G2.Edges.Weight);");
    }

    /**
     * 研究特定时间段内传球矢量图
     */
    public void getPassingEvent2(int matchId, double time, double stays) throws ExecutionException, InterruptedException {
        List<Double> edgesHusox = fulleventsRepository.findByTimeox(matchId, time, time + stays, "Huskies");
        List<Double> edgesHusoy = fulleventsRepository.findByTimeoy(matchId, time, time + stays, "Huskies");
        List<Double> edgesOppox = fulleventsRepository.findByTimeox(matchId, time, time + stays, "Opponent");
        List<Double> edgesOppoy = fulleventsRepository.findByTimeoy(matchId, time, time + stays, "Opponent");
        List<Double> edgesHusdx = fulleventsRepository.findByTimedx(matchId, time, time + stays, "Huskies");
        List<Double> edgesHusdy = fulleventsRepository.findByTimedy(matchId, time, time + stays, "Huskies");
        List<Double> edgesOppodx = fulleventsRepository.findByTimedx(matchId, time, time + stays, "Opponent");
        List<Double> edgesOppody = fulleventsRepository.findByTimedy(matchId, time, time + stays, "Opponent");
        handleArray(edgesHusox, edgesHusdx);
        handleArray(edgesHusoy, edgesHusdy);
        handleArray(edgesOppox, edgesOppodx);
        handleArray(edgesOppoy, edgesOppody);
        engine.eval("quiver(" + edgesHusox + "," + edgesHusoy + "," + edgesHusdx + "," + edgesHusdy + ")");
        engine.eval("hold on");
        engine.eval("quiver(" + edgesOppox + "," + edgesOppoy + "," + edgesOppodx + "," + edgesOppody + ")");
    }

    /**
     * 清除查询坐标为null的元素a
     */
    private void handleArray(List<Double> a, List<Double> b) {
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i) == null) {
                if (b.get(i) != null)
                    a.set(i, b.get(i));
                else a.set(i, 0.0);
            }
        }
        for (int i = 0; i < b.size(); i++) {
            if (b.get(i) == null) {
                if (a.get(i) != null)
                    b.set(i, a.get(i));
                else b.set(i, 0.0);
            }
        }
    }

    /**
     * 输出这些球员本场比赛的平均位置
     */
    private Locations printLoc(int matchId, List<String> players, double time, double stays, String part) {
        ArrayList<Double> avex = new ArrayList<>();
        ArrayList<Double> avey = new ArrayList<>();

        for (String s : players) {
            Long sum_x = fulleventsRepository.sum_x(matchId, s, time, stays, part);
            Long sum_y = fulleventsRepository.sum_y(matchId, s, time, stays, part);
            if (sum_x != null && sum_y != null) {
                avex.add((double) sum_x /
                        fulleventsRepository.count_x(matchId, s, time, stays, part));
                avey.add((double) sum_y /
                        fulleventsRepository.count_x(matchId, s, time, stays, part));
            }
        }
        System.out.println("average X location " + avex);
        System.out.println("average Y location " + avey);
        return new Locations(avex, avey);
    }

    static class Locations {
        ArrayList<Double> avex;
        ArrayList<Double> avey;

        public Locations(ArrayList<Double> avex, ArrayList<Double> avey) {
            this.avex = avex;
            this.avey = avey;
        }
    }
}
