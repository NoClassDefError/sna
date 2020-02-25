package com.icm.sna.vo;

import java.util.Arrays;
import java.util.List;

/**
 * 邻接矩阵有向图
 */
public class MatrixDG {
    int size;
    List<String> vertexs;
    int[][] matrix;

    public MatrixDG(List<String> vertexs, List<Edge> edges) {

        size = vertexs.size();
        matrix = new int[size][size];
        for (int[] ints : matrix) Arrays.fill(ints, 0);//构造0矩阵
        this.vertexs = vertexs;
        //和邻接矩阵无向图差别仅仅在这里
        for (Edge edge : edges) {
//            System.out.println(edge.getOrigin() + " " + edge.getDestination());
            int p1 = findVertex(edge.getOrigin());
            int p2 = findVertex(edge.getDestination());
            if (p1 == -1 || p2 == -1) continue;
            matrix[p1][p2]++;
        }
    }

    public String print() {
        int sum = 0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                sum += matrix[i][j];
                if (j == matrix[i].length - 1) {
                    stringBuilder.append(matrix[i][j]);
                    break;
                }
                stringBuilder.append(matrix[i][j]).append(",");
            }
            if (i == matrix.length - 1) break;
            stringBuilder.append(";");
        }
        stringBuilder.append("]");
        System.out.println(stringBuilder);
        System.out.println("sum=" + sum);
        return new String(stringBuilder);
    }

    private int findVertex(String s) {
        int a = 0;
        for (String ss : vertexs) {
            if (ss.equals(s)) return a;
            a++;
        }
        System.out.println("unfound:" + s);
        return -1;
    }
}