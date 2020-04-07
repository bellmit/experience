package cn.com.servyou.gxfx.basic.view;

import cn.com.servyou.gxfx.basic.model.*;
import cn.com.servyou.gxfx.basic.util.TransformUtil;
import cn.com.servyou.gxfx.model.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author lpp
 * 2018-08-07
 */
@ApiModel("图")
public class GraphEntity<Q extends GraphEntity.QyVertex, Z extends GraphEntity.ZrrVertex, F extends GraphEntity.PlEdge, R extends GraphEntity.RzEdge> {
    private static final int NEXT = 2;

    private static final int KIND_CENTER = 0;
    private static final int KIND_SY = 1;
    private static final int KIND_XY = 2;
    public static final int KIND_RYGL = 3;

    public static final int KIND4CENTER_CENTER = 0;
    public static final int KIND4CENTER_SY = 1;
    public static final int KIND4CENTER_XY = 2;

    private Graph<Q, Z, F, R> graph;

    @Getter
    @Setter
    private Map<String, Vertex> vertexes;

    @Getter
    @Setter
    private Map<String, BaseEdge> edges;

    public GraphEntity(Graph<Q, Z, F, R> graph) {
        this(graph, graph.getQyMap(), graph.getZrrMap(), graph.getFps(), graph.getRzs());
    }

    public GraphEntity(Graph<Q, Z, F, R> graph, Map<String, ? extends Vertex> qyMap, Map<String, ? extends ZrrVertex> zrrMap, Set<? extends PlEdge> fps, Set<? extends RzEdge> rzs) {
        this.graph = graph;

        this.vertexes = Maps.newHashMap(qyMap);
        this.vertexes.putAll(zrrMap);

        this.edges = Maps.newHashMap();
        for (PlEdge pl : fps) {
            this.edges.put(pl.getId(), pl);
        }
        for (RzEdge rz : rzs) {
            this.edges.put(rz.getId(), rz);
        }
    }

    public Graph<Q, Z, F, R> graph() {
        return graph;
    }

    private Map<String, Collection<String>> syMap() {
        return BaseEdge.syMap(this.edges.values());
    }

    private Map<String, Collection<String>> xyMap() {
        return BaseEdge.xyMap(this.edges.values());
    }

    public GraphEntity<Q, Z, F, R> fillQy(Map<String, Double> jxzeMap, Map<String, Double> xxzeMap, Map<String, Nsr> nsrMap, Map<String, Collection<String>> rgbsMap) {
        for (GraphEntity.QyVertex q : this.graph.getQyMap().values()) {
            String id = q.getId();
            String key = q.getKey();

            q.fill(nsrMap.get(key));
            q.setJxze(jxzeMap.containsKey(id) ? jxzeMap.get(id) : 0.0);
            q.setXxze(xxzeMap.containsKey(id) ? xxzeMap.get(id) : 0.0);
            q.setRgbsqy(rgbsMap.containsKey(key));
            q.rgbs(rgbsMap.get(key));
        }
        return this;
    }

    public GraphEntity<Q, Z, F, R> fillZrr() {
        for (GraphEntity.ZrrVertex z : this.graph.getZrrMap().values()) {
            final String id = z.getId();
            Collection<? extends GraphEntity.RzEdge> rz = Collections2.filter(this.graph.getRzs(), new Predicate<RzEdge>() {
                @Override
                public boolean apply(GraphEntity.RzEdge input) {
                    return id.equalsIgnoreCase(input.getFrom());
                }
            });

            z.setRzqys(rz.size());
            z.setRzgw(Rz.rzgw(rz));
        }
        return this;
    }

    public GraphEntity<Q, Z, F, R> fillSxy() {
        Map<String, Collection<String>> syMap = this.syMap();
        Map<String, Collection<String>> xyMap = this.xyMap();
        for (Vertex v : vertexes.values()) {
            String id = v.getId();
            v.setSy(syMap.containsKey(id) ? syMap.get(id) : Collections.<String>emptyList());
            v.setXy(xyMap.containsKey(id) ? xyMap.get(id) : Collections.<String>emptyList());
        }
        return this;
    }

    public GraphEntity<Q, Z, F, R> unitTransform() {
        for (Q q : graph.getQyMap().values()) {
            q.setJxze(Math.round(q.getJxze() / 10000));
            q.setXxze(Math.round(q.getXxze() / 10000));
        }

        for (F fp : graph.getFps()) {
            fp.setJyje(new BigDecimal(fp.getJyje() / 10000).setScale(0, RoundingMode.HALF_UP).doubleValue());
            fp.setJxzb(new BigDecimal(fp.getJxzb() * 100).setScale(1, RoundingMode.HALF_UP).doubleValue());
            fp.setXxzb(new BigDecimal(fp.getXxzb() * 100).setScale(1, RoundingMode.HALF_UP).doubleValue());
        }
        return this;
    }

    public GraphEntity<Q, Z, F, R> fillRyglqy(String centerNode) {
        Set<R> rzSet = this.graph.getRzs();

        Map<String, Collection<String>> rzMap = Rz.rzMap(rzSet);
        for (String s : Rz.ryglqy(centerNode, rzSet)) {
            GraphEntity.QyVertex qy = this.graph.getQyMap().get(s);
            qy.setKind(GraphEntity.KIND_RYGL);
            qy.setQmd(Rz.generateQmd(s, centerNode, rzMap, this.getEdges()));
        }
        return this;
    }

    public GraphEntity<Q, Z, F, R> deepKind(String centerNode, int syDeep, int xyDeep) {
        Map<String, Vertex> vertexMap = this.getVertexes();
        Map<String, Collection<String>> syMap = this.syMap();
        Map<String, Collection<String>> xyMap = this.xyMap();

        GraphEntity.QyVertex c = (GraphEntity.QyVertex) vertexMap.get(centerNode);
        c.setDeep(0);
        c.setKind(GraphEntity.KIND_CENTER);
        c.setKind4Center(GraphEntity.KIND4CENTER_CENTER);

        Collection<String> sy = syMap.get(centerNode);
        if (sy != null) {
            for (String s : sy) {
                deepKind(vertexMap, syMap, xyMap, syDeep, 1, GraphEntity.KIND_SY, GraphEntity.KIND4CENTER_SY, s);
            }
        }

        Collection<String> xy = xyMap.get(centerNode);
        if (xy != null) {
            for (String s : xy) {
                deepKind(vertexMap, syMap, xyMap, xyDeep, 1, GraphEntity.KIND_XY, GraphEntity.KIND4CENTER_XY, s);
            }
        }
        return this;
    }

    private static void deepKind(Map<String, Vertex> vertexMap, Map<String, Collection<String>> syMap, Map<String, Collection<String>> xyMap, int maxDeep, int deep, int kind, int kind4Center, String s) {
        if (deep <= maxDeep + NEXT) {
            Vertex v = vertexMap.get(s);
            if (v.getDeep() == null || deep < v.getDeep()) {
                v.setDeep(deep);
                v.setKind(kind);
                v.setKind4Center(kind4Center);

                Collection<String> sySet = syMap.get(s);
                if (sySet != null) {
                    for (String sy : sySet) {
                        deepKind(vertexMap, syMap, xyMap, maxDeep, deep + 1, GraphEntity.KIND_SY, kind4Center, sy);
                    }
                }

                Collection<String> xySet = xyMap.get(s);
                if (xySet != null) {
                    Vertex vertex = vertexMap.get(s);
                    int nextKind = vertex instanceof GraphEntity.QyVertex ? GraphEntity.KIND_XY : GraphEntity.KIND_RYGL;
                    for (String xy : xySet) {
                        deepKind(vertexMap, syMap, xyMap, maxDeep, deep + 1, nextKind, kind4Center, xy);
                    }
                }
            }
        }
    }

    @Getter
    @Setter
    public static class Vertex extends cn.com.servyou.gxfx.model.Vertex {
        protected Integer deep;
        protected Integer kind;
        protected Integer kind4Center;
        protected Collection<String> sy;
        protected Collection<String> xy;

        public int getType() {
            if (super.getNsr()) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    @Getter
    @Setter
    public static class QyVertex extends Vertex {
        private String sbh;
        private String nsrztdm;
        private String nsrztmc;
        private String xydjmc;
        private String lazt;
        private String djrq;
        private double jxze;
        private double xxze;
        private boolean sm;
        private boolean rgbsqy;
        private List<String> rgbsArr;
        private String djzclxdm;
        private String djzclxmc;
        private String hydm;
        private String hymc;
        private String dsswjgmc;
        private Double qmd;
        private boolean ws;

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        public void fill(Nsr nsr) {
            if (nsr != null) {
                this.sbh = nsr.getSbh();
                this.nsrztdm = nsr.getNsrztdm();
                this.nsrztmc = nsr.getNsrztmc();
                this.djrq = nsr.getDjrq();
                this.sm = TransformUtil.isSmhy(nsr.getHydm());
                this.djzclxdm = TransformUtil.djzclxdm(nsr.getDjzclxdm());
                this.djzclxmc = TransformUtil.djzclxmc(nsr.getDjzclxdm(), nsr.getDjzclxmc());
                this.hydm = nsr.getHydm();
                this.hymc = nsr.getHymc();
                this.dsswjgmc = nsr.getDsswjgmc();
                this.ws = false;
            } else {
                super.name = String.format("%s(外省)", super.name);
                this.ws = true;
            }
        }

        public void rgbs(Collection<String> strings) {
            if (strings != null) {
                this.rgbsArr = Lists.newArrayList(strings);
            } else {
                this.rgbsArr = Collections.emptyList();
            }
        }
    }

    @Getter
    @Setter
    public static class ZrrVertex extends Vertex {
        private String lxdh;
        private String dqmc;
        private int age;
        private int rzqys;
        private String rzgw;

        public int getAge() {
            return TransformUtil.fetchAgeFromSfz(key);
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    public static class PlEdge extends Fp {
        public int getType() {
            return 0;
        }

        public double getGrd() {
            double val = Math.max(jxzb, xxzb);
            return new BigDecimal(val / 100).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    public static class RzEdge extends Rz {
        public int getType() {
            return 1;
        }

        public Integer getWorkType() {
            return TransformUtil.rzTypes(super.rzTypes());
        }

        public String getRzgw() {
            return super.rzgwDisplay();
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    @ApiModel
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InitParams<Q extends GraphEntity.QyVertex, Z extends GraphEntity.ZrrVertex, F extends GraphEntity.PlEdge, R extends GraphEntity.RzEdge> implements Graph.InitParams<Q, Z, F, R> {
        @ApiModelProperty(value = "中心节点", required = true)
        private String centerNode;
        @ApiModelProperty(value = "上游层数", required = true)
        private int syDeep;
        @ApiModelProperty(value = "下游层数", required = true)
        private int xyDeep;
        @ApiModelProperty(value = "上游过滤类型", required = true, allowableValues = "top, jyje")
        private String syLimitType;
        @ApiModelProperty(value = "上游过滤值", required = true)
        private double syLimitValue;
        @ApiModelProperty(value = "下游过滤类型", required = true, allowableValues = "top, jyje")
        private String xyLimitType;
        @ApiModelProperty(value = "下游过滤值", required = true)
        private double xyLimitValue;
        @ApiModelProperty(value = "任职类型", required = true)
        private int workType;
        @ApiModelProperty(value = "发票类型", required = true, allowableValues = "zp, pp, all")
        private Fplx fplx;
        @ApiModelProperty(value = "开始月份", required = true, example = "201601")
        private String begin;
        @ApiModelProperty(value = "结束月份", required = true, example = "201612")
        private String end;

        private Class<Q> qyClass;
        private Class<Z> zrrClass;
        private Class<F> fpClass;
        private Class<R> rzClass;

        public void init(Class<Q> qyClass, Class<Z> zrrClass, Class<F> fpClass, Class<R> rzClass) {
            this.qyClass = qyClass;
            this.zrrClass = zrrClass;
            this.fpClass = fpClass;
            this.rzClass = rzClass;
        }

        @Override
        public String getCenterNode() {
            return cn.com.servyou.gxfx.model.Vertex.key2Id(this.centerNode);
        }

        @Override
        public FpFilterParams getSyParams() {
            return new FpFilterParams(fplx, Fp.monthOf(begin), Fp.monthOf(end), SxyType.sy, FpFilterParams.FilterType.valueOf(syLimitType), syLimitValue);
        }

        @Override
        public FpFilterParams getXyParams() {
            return new FpFilterParams(fplx, Fp.monthOf(begin), Fp.monthOf(end), SxyType.xy, FpFilterParams.FilterType.valueOf(xyLimitType), xyLimitValue);
        }

        @Override
        public Set<RzType> getRzTypes() {
            return TransformUtil.rzTypes(workType);
        }

        @Override
        public Class<Q> qyClass() {
            return qyClass;
        }

        @Override
        public Class<Z> zrrClass() {
            return zrrClass;
        }

        @Override
        public Class<F> fpClass() {
            return fpClass;
        }

        @Override
        public Class<R> rzClass() {
            return rzClass;
        }
    }

    @ApiModel
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpandParams<Q extends GraphEntity.QyVertex, Z extends GraphEntity.ZrrVertex, F extends GraphEntity.PlEdge, R extends GraphEntity.RzEdge> implements Graph.ExpandParams<Q, Z, F, R> {
        @ApiModelProperty(value = "展开的节点", required = true, example = "Vertex/1234567890")
        private String expandNode;
        private Set<String> allNodes;
        private String syLimitType;
        private Double syLimitValue;
        private String xyLimitType;
        private Double xyLimitValue;
        private Integer workType;
        private Fplx fplx;
        private String begin;
        private String end;

        private Class<Q> qyClass;
        private Class<Z> zrrClass;
        private Class<F> fpClass;
        private Class<R> rzClass;

        public void init(Class<Q> qyClass, Class<Z> zrrClass, Class<F> fpClass, Class<R> rzClass) {
            this.qyClass = qyClass;
            this.zrrClass = zrrClass;
            this.fpClass = fpClass;
            this.rzClass = rzClass;
        }

        @Override
        public Set<String> getExistNodes() {
            return this.allNodes;
        }

        @Override
        public FpFilterParams getSyParams() {
            return new FpFilterParams(fplx, Fp.monthOf(begin), Fp.monthOf(end), SxyType.sy, FpFilterParams.FilterType.valueOf(syLimitType), syLimitValue);
        }

        @Override
        public FpFilterParams getXyParams() {
            return new FpFilterParams(fplx, Fp.monthOf(begin), Fp.monthOf(end), SxyType.xy, FpFilterParams.FilterType.valueOf(xyLimitType), xyLimitValue);
        }

        @Override
        public FpDataParams getDataParams() {
            return new FpDataParams(fplx, Fp.monthOf(begin), Fp.monthOf(end));
        }

        @Override
        public Set<RzType> getRzTypes() {
            return TransformUtil.rzTypes(workType);
        }


        @Override
        public Class<Q> qyClass() {
            return qyClass;
        }

        @Override
        public Class<Z> zrrClass() {
            return zrrClass;
        }

        @Override
        public Class<F> fpClass() {
            return fpClass;
        }

        @Override
        public Class<R> rzClass() {
            return rzClass;
        }
    }
}
