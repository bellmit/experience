package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.gxfx.basic.model.FpFilterParams;
import cn.com.servyou.gxfx.basic.model.Graph;
import cn.com.servyou.gxfx.basic.service.FpService;
import cn.com.servyou.gxfx.basic.service.GraphService;
import cn.com.servyou.gxfx.basic.service.RzService;
import cn.com.servyou.gxfx.basic.service.VertexService;
import cn.com.servyou.gxfx.model.Fp;
import cn.com.servyou.gxfx.model.Rz;
import cn.com.servyou.gxfx.model.RzType;
import cn.com.servyou.gxfx.model.Vertex;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lpp
 * 2018-12-03
 */
@Service
public class GraphServiceImpl implements GraphService {

    private final FpService fpService;
    private final RzService rzService;
    private final VertexService vertexService;

    @Autowired
    public GraphServiceImpl(FpService fpService, RzService rzService, VertexService vertexService) {
        this.fpService = fpService;
        this.rzService = rzService;
        this.vertexService = vertexService;
    }

    @Override
    public <Q extends Vertex, Z extends Vertex, F extends Fp, R extends Rz> Graph<Q, Z, F, R> graphInit(Graph.InitParams<Q, Z, F, R> params) {
        String centerNode = params.getCenterNode();
        Set<RzType> rzTypes = params.getRzTypes();

        Set<String> qyIdSet = Sets.newHashSet(centerNode);
        fetchPl(qyIdSet, params);
        if (!rzTypes.isEmpty()) {
            qyIdSet.addAll(rzService.searchRyglqy(centerNode, rzTypes));
        }

        Set<F> plSet = fpService.readPl(qyIdSet, qyIdSet, params.getSyParams(), params.fpClass());

        Set<String> zrrIdSet;
        Set<R> rzSet;
        if (!rzTypes.isEmpty()) {
            rzSet = rzService.readRz(qyIdSet, rzTypes, params.rzClass());
            zrrIdSet = Sets.newHashSet(Collections2.transform(rzSet, new Function<Rz, String>() {
                @Override
                public String apply(Rz rz) {
                    return rz.getFrom();
                }
            }));
        } else {
            zrrIdSet = Collections.emptySet();
            rzSet = Collections.emptySet();
        }

        Map<String, Q> qyMap = vertexService.vertex(qyIdSet, params.qyClass());
        Map<String, Z> zrrMap = vertexService.vertex(zrrIdSet, params.zrrClass());

        return new Graph<Q, Z, F, R>(qyMap, zrrMap, plSet, rzSet);
    }

    @Override
    public <Q extends Vertex, Z extends Vertex, F extends Fp, R extends Rz> Graph<Q, Z, F, R> graphExpand(Graph.ExpandParams<Q, Z, F, R> params) {

        final Set<String> existNodes = params.getExistNodes();

        Set<String> sy = fpService.searchSxy(Collections.singleton(params.getExpandNode()), params.getSyParams());
        Set<String> xy = fpService.searchSxy(Collections.singleton(params.getExpandNode()), params.getXyParams());

        final Set<String> newQySet = Sets.newHashSet(Collections2.filter(Sets.union(sy, xy), new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return !existNodes.contains(input);
            }
        }));


        Set<F> pl1 = fpService.readPl(newQySet, newQySet, params.getDataParams(), params.fpClass());
        Set<F> pl2 = fpService.readPl(newQySet, existNodes, params.getDataParams(), params.fpClass());
        Set<F> pl3 = fpService.readPl(existNodes, newQySet, params.getDataParams(), params.fpClass());
        Set<F> fpSet = Sets.union(Sets.union(pl1, pl2), pl3);


        final Set<String> zrrIdSet;
        Set<R> rzSet;
        if (!params.getRzTypes().isEmpty()) {
            Set<R> tmpRzSet = rzService.readRz(newQySet, Sets.union(newQySet, existNodes), params.getRzTypes(), params.rzClass());

            // filter zrr which exist
            zrrIdSet = Sets.newHashSet(Collections2.filter(Collections2.transform(tmpRzSet, new Function<R, String>() {
                @Override
                public String apply(R rz) {
                    return rz.getFrom();
                }
            }), new Predicate<String>() {
                @Override
                public boolean apply(String input) {
                    return !existNodes.contains(input);
                }
            }));

            // filter edge which exist
            rzSet = Sets.newHashSet(Collections2.filter(tmpRzSet, new Predicate<R>() {
                @Override
                public boolean apply(R input) {
                    if (zrrIdSet.contains(input.getFrom())) {
                        // zrr vertex not exist, so all the edge not exist
                        return true;
                    } else {
                        // zrr exist, the qyId in new Qy Set not exist
                        return newQySet.contains(input.getTo());
                    }
                }
            }));
        } else {
            zrrIdSet = Collections.emptySet();
            rzSet = Collections.emptySet();
        }

        Map<String, Q> qyMap = vertexService.vertex(newQySet, params.qyClass());
        Map<String, Z> zrrMap = vertexService.vertex(zrrIdSet, params.zrrClass());

        return new Graph<Q, Z, F, R>(qyMap, zrrMap, fpSet, rzSet);
    }

    private void fetchPl(Set<String> result, Graph.InitParams params) {
        String centerNode = params.getCenterNode();
        int syDeep = params.getSyDeep();
        int xyDeep = params.getXyDeep();
        FpFilterParams syFilterParams = params.getSyParams();
        FpFilterParams xyFilterParams = params.getXyParams();
        Set<String> syNext = fpService.searchSxy(Collections.singleton(centerNode), syFilterParams);
        Set<String> xyNext = fpService.searchSxy(Collections.singleton(centerNode), xyFilterParams);

        Set<String> sySource = addAndFindNextSource(result, syNext, xyNext, syDeep, xyDeep);
        Set<String> xySource = addAndFindNextSource(result, xyNext, syNext, xyDeep, syDeep);

        int maxDeep = syDeep > xyDeep ? syDeep : xyDeep;
        // 一层层的查找，确保查到的点在最短路径上
        for (int i = 1; i < maxDeep; i++) {
            if (i < syDeep) {
                syNext = readSxy(sySource, syFilterParams, xyFilterParams);
            } else {
                syNext = Collections.emptySet();
            }

            if (i < xyDeep) {
                xyNext = readSxy(xySource, syFilterParams, xyFilterParams);
            } else {
                xyNext = Collections.emptySet();
            }

            sySource = addAndFindNextSource(result, syNext, xyNext, syDeep, xyDeep);
            xySource = addAndFindNextSource(result, xyNext, syNext, xyDeep, syDeep);
        }
    }

    private Set<String> readSxy(Set<String> sySource, FpFilterParams syFilterParams, FpFilterParams xyFilterParams) {
        return Sets.union(fpService.searchSxy(sySource, syFilterParams), fpService.searchSxy(sySource, xyFilterParams));
    }

    private static Set<String> addAndFindNextSource(Set<String> result, Set<String> syNext, Set<String> xyNext, int syDeep, int xyDeep) {
        Set<String> sySource = new HashSet<String>(syNext.size());

        for (String s : syNext) {
            if (!result.contains(s)) {
                // 当一个点是上下游且处理同一层时，认为其是 深度大的 那一侧
                if (syDeep >= xyDeep) {
                    result.add(s);
                    sySource.add(s);
                } else {
                    if (!xyNext.contains(s)) {
                        result.add(s);
                        sySource.add(s);
                    }
                }
            }
        }

        return sySource;
    }
}
