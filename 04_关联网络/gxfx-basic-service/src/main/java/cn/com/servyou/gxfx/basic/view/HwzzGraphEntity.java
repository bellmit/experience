package cn.com.servyou.gxfx.basic.view;

import cn.com.servyou.gxfx.basic.model.Nsr;
import cn.com.servyou.gxfx.basic.util.TransformUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author lpp
 * 2018-10-22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HwzzGraphEntity {
    private Map<String, Vertex> vertexes;
    private Map<String, Edge> edges;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Vertex {
        protected String id;
        protected String key;
        protected int type;
        protected String name;
        protected Integer deep;
        protected Integer kind4Center;
        protected Collection<String> sy;
        protected Collection<String> xy;

        public Vertex(String id, String key, int type, String name) {
            this.id = id;
            this.key = key;
            this.type = type;
            this.name = name;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class QyVertex extends Vertex {
        private String sbh;
        private String nsrztdm;
        private String nsrztmc;
        private String djrq;
        private boolean sm;
        private boolean rgbsqy;
        private List<String> rgbsArr;
        private String djzclxdm;
        private String djzclxmc;
        private String hydm;
        private String hymc;
        private boolean ws;

        public QyVertex(String id, String key, String name, String sbh, String nsrztdm, String nsrztmc, String djrq, boolean sm, boolean rgbsqy, List<String> rgbsArr, String djzclxdm, String djzclxmc, String hydm, String hymc, boolean ws) {
            super(id, key, 0, name);
            this.sbh = sbh;
            this.nsrztdm = nsrztdm;
            this.nsrztmc = nsrztmc;
            this.djrq = djrq;
            this.sm = sm;
            this.rgbsqy = rgbsqy;
            this.rgbsArr = rgbsArr;
            this.djzclxdm = djzclxdm;
            this.djzclxmc = djzclxmc;
            this.hydm = hydm;
            this.hymc = hymc;
            this.ws = ws;
        }

        public QyVertex(String id, String key, String name) {
            super(id, key, 0, name);
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
                this.ws = false;
            } else {
                super.name = String.format("%s(Õ‚ °)", super.name);
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
    @NoArgsConstructor
    public static class HwVertex extends Vertex {
        private String gxjl;

        public HwVertex(String id, String key, String name, String gxjl) {
            super(id, key, 1, name);
            this.gxjl = gxjl;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Edge {
        protected String id;
        protected String source;
        protected String target;
        protected double je;

        public Edge(String id, String source, String target, double je) {
            this.id = id;
            this.source = source;
            this.target = target;
            this.je = new BigDecimal(je / 10000).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }
    }

}
