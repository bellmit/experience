package cn.com.servyou.gxfx.basic.util;

import cn.com.servyou.gxfx.basic.model.DfHwJyjeEntity;
import cn.com.servyou.gxfx.basic.model.Nsr;
import cn.com.servyou.gxfx.basic.view.GraphEntity;
import cn.com.servyou.gxfx.basic.view.HwzzGraphEntity;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.*;

import java.util.*;

/**
 * @author lpp
 * 2018-12-25
 */
public class HwzzGraphBuilder {
    private final String nsrdzdah;
    private final List<DfHwJyjeEntity> dfHwJyjeList;
    private final Map<String, Nsr> nsrMap;
    private final Map<String, Collection<String>> rgbsMap;

    public HwzzGraphBuilder(String nsrdzdah, List<DfHwJyjeEntity> dfHwJyjeList, Map<String, Nsr> nsrMap, Map<String, Collection<String>> rgbsMap) {
        this.nsrdzdah = nsrdzdah;
        this.dfHwJyjeList = dfHwJyjeList;
        this.nsrMap = nsrMap;
        this.rgbsMap = rgbsMap;
    }

    public HwzzGraphEntity build() {
        Map<String, HwzzGraphEntity.Vertex> qyMap = qyMap();
        Map<String, HwzzGraphEntity.Vertex> hwMap = hwMap();
        Map<String, HwzzGraphEntity.Edge> hwEdgeMap = hwEdgeMap();
        Map<String, HwzzGraphEntity.Edge> hwDfEdgeMap = hwDfEdgeMap();

        Map<String, HwzzGraphEntity.Vertex> vertexMap = Maps.newHashMap(qyMap);
        vertexMap.putAll(hwMap);

        Map<String, HwzzGraphEntity.Edge> edgeMap = Maps.newHashMap(hwEdgeMap);
        edgeMap.putAll(hwDfEdgeMap);

        return new HwzzGraphEntity(vertexMap, edgeMap);
    }

    private Map<String, HwzzGraphEntity.Vertex> qyMap() {
        Nsr cn = nsrMap.get(nsrdzdah);
        Map<String, HwzzGraphEntity.Vertex> result = Maps.newHashMap();
        HwzzGraphEntity.QyVertex q = new HwzzGraphEntity.QyVertex(nsrdzdah, nsrdzdah, cn.getName(), cn.getSbh(), cn.getNsrztdm(), cn.getNsrztmc(), cn.getDjrq(), TransformUtil.isSmhy(cn.getHydm()), rgbsMap.containsKey(nsrdzdah), rgbsMap.containsKey(nsrdzdah) ? Lists.newArrayList(rgbsMap.get(nsrdzdah)) : Lists.<String>newArrayList(), TransformUtil.djzclxdm(cn.getDjzclxdm()), TransformUtil.djzclxmc(cn.getDjzclxdm(), cn.getDjzclxmc()), cn.getHydm(), cn.getHymc(), false);
        q.setDeep(0);
        q.setKind4Center(GraphEntity.KIND4CENTER_CENTER);
        q.setSy(centerNodeSxyHwId(DfHwJyjeEntity.GF_BJ));
        q.setXy(centerNodeSxyHwId(DfHwJyjeEntity.XF_BJ));
        result.put(nsrdzdah, q);
        for (DfHwJyjeEntity e : dfHwJyjeList) {
            String dfnsrdzdah = e.getDfnsrdzdah();
            String qyId = e.dfNsrId();
            if (!result.containsKey(qyId)) {
                HwzzGraphEntity.QyVertex qy = new HwzzGraphEntity.QyVertex(qyId, dfnsrdzdah, e.getDfnsrmc());
                qy.fill(nsrMap.get(dfnsrdzdah));
                qy.rgbs(rgbsMap.get(dfnsrdzdah));
                qy.setDeep(2);
                if (DfHwJyjeEntity.GF_BJ.equals(e.getGfxfBj())) {
                    qy.setKind4Center(GraphEntity.KIND4CENTER_SY);
                    qy.setSy(Collections.<String>emptyList());
                    qy.setXy(Collections.singleton(e.hwId()));
                } else {
                    qy.setKind4Center(GraphEntity.KIND4CENTER_XY);
                    qy.setSy(Collections.singleton(e.hwId()));
                    qy.setXy(Collections.<String>emptyList());
                }

                result.put(qy.getId(), qy);
            }
        }

        return result;
    }

    private Collection<String> centerNodeSxyHwId(final String gfXfBj) {
        Collection<DfHwJyjeEntity> sy = Collections2.filter(dfHwJyjeList, new Predicate<DfHwJyjeEntity>() {
            @Override
            public boolean apply(DfHwJyjeEntity input) {
                return gfXfBj.equalsIgnoreCase(input.getGfxfBj());
            }
        });

        Collection<String> collection = Collections2.transform(sy, new Function<DfHwJyjeEntity, String>() {
            @Override
            public String apply(DfHwJyjeEntity input) {
                return input.hwId();
            }
        });

        return Sets.newHashSet(collection);
    }

    private Map<String, HwzzGraphEntity.Vertex> hwMap() {
        Map<String, String> gxjlMap = gxjlMap();

        Map<String, HwzzGraphEntity.Vertex> result = Maps.newHashMap();

        for (DfHwJyjeEntity e : dfHwJyjeList) {
            final String hwId = e.hwId();
            if (!result.containsKey(hwId)) {
                HwzzGraphEntity.HwVertex hw = new HwzzGraphEntity.HwVertex(hwId, e.hwKey(), e.getSpmc(), gxjlMap.get(e.getSpmc()));
                hw.setDeep(1);
                if (DfHwJyjeEntity.GF_BJ.equals(e.getGfxfBj())) {
                    hw.setKind4Center(GraphEntity.KIND4CENTER_SY);
                    hw.setSy(getSxyByHw(hwId));
                    hw.setXy(Collections.singleton(nsrdzdah));
                } else {
                    hw.setKind4Center(GraphEntity.KIND4CENTER_XY);
                    hw.setSy(Collections.singleton(nsrdzdah));
                    hw.setXy(getSxyByHw(hwId));
                }
                result.put(hwId, hw);
            }
        }

        return result;
    }

    private Map<String, String> gxjlMap() {
        SetMultimap<String, String> multimap = Multimaps.newSetMultimap(Maps.<String, Collection<String>>newHashMap(), new Supplier<Set<String>>() {
            @Override
            public Set<String> get() {
                return Sets.newHashSet();
            }
        });

        for (DfHwJyjeEntity e : dfHwJyjeList) {
            multimap.put(e.getSpmc(), e.getGfxfBj());
        }

        Map<String, String> result = Maps.newHashMap();
        for (Map.Entry<String, Collection<String>> entry : multimap.asMap().entrySet()) {
            String spmc = entry.getKey();
            Collection<String> set = entry.getValue();

            if (set.contains(DfHwJyjeEntity.GF_BJ)) {
                if (set.contains(DfHwJyjeEntity.XF_BJ)) {
                    result.put(spmc, "YJYX");
                } else {
                    result.put(spmc, "YJWX");
                }
            } else {
                if (set.contains(DfHwJyjeEntity.XF_BJ)) {
                    result.put(spmc, "YXWJ");
                } else {
                    result.put(spmc, "WJWX");
                    throw new RuntimeException("never occur");
                }
            }
        }

        return result;
    }

    private List<String> getSxyByHw(final String hwId) {
        Collection<DfHwJyjeEntity> hwNsrs = Collections2.filter(dfHwJyjeList, new Predicate<DfHwJyjeEntity>() {
            @Override
            public boolean apply(DfHwJyjeEntity input) {
                return hwId.equals(input.hwId());
            }
        });

        Collection<String> nsrId = Collections2.transform(hwNsrs, new Function<DfHwJyjeEntity, String>() {
            @Override
            public String apply(DfHwJyjeEntity input) {
                return input.dfNsrId();
            }
        });

        return Lists.newArrayList(nsrId);
    }

    private Map<String, HwzzGraphEntity.Edge> hwEdgeMap() {
        Map<String, HwzzGraphEntity.Edge> result = Maps.newHashMap();
        for (DfHwJyjeEntity e : dfHwJyjeList) {
            if (!result.containsKey(e.hwEdgeId())) {
                result.put(e.hwEdgeId(), new HwzzGraphEntity.Edge(e.hwEdgeId(), e.hwEdgeSource(), e.hwEdgeTarget(), getHwJe(e.hwId())));
            }
        }
        return result;
    }

    private double getHwJe(final String hwId) {
        Collection<DfHwJyjeEntity> hwNsrs = Collections2.filter(dfHwJyjeList, new Predicate<DfHwJyjeEntity>() {
            @Override
            public boolean apply(DfHwJyjeEntity input) {
                return hwId.equals(input.hwId());
            }
        });

        Collection<Double> jes = Collections2.transform(hwNsrs, new Function<DfHwJyjeEntity, Double>() {
            @Override
            public Double apply(DfHwJyjeEntity input) {
                return input.getJe();
            }
        });

        double result = 0.0;
        for (Double aDouble : jes) {
            result += aDouble;
        }

        return result;
    }

    private Map<String, HwzzGraphEntity.Edge> hwDfEdgeMap() {
        Map<String, HwzzGraphEntity.Edge> result = Maps.newHashMap();
        for (DfHwJyjeEntity e : dfHwJyjeList) {
            result.put(e.dfEdgeId(), new HwzzGraphEntity.Edge(e.dfEdgeId(), e.dfEdgeSource(), e.dfEdgeTarget(), e.getJe()));
        }
        return result;
    }
}
