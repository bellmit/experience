package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.gxfx.basic.model.FpDataParams;
import cn.com.servyou.gxfx.basic.model.Graph;
import cn.com.servyou.gxfx.basic.model.Nsr;
import cn.com.servyou.gxfx.basic.service.*;
import cn.com.servyou.gxfx.basic.view.GraphEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static cn.com.servyou.gxfx.model.Vertex.id2Key;

/**
 * @author lpp
 * 2018-08-06
 */
@Service
public class GraphQueryServiceImpl implements GraphQueryService {

    private final SignService signService;

    private final GraphService graphService;
    private final FpService fpService;
    private final NsrService nsrService;

    @Autowired
    public GraphQueryServiceImpl(SignService signService, GraphService graphService, FpService fpService, NsrService nsrService) {
        this.signService = signService;
        this.graphService = graphService;
        this.fpService = fpService;
        this.nsrService = nsrService;
    }

    @Override
    public <Q extends GraphEntity.QyVertex, Z extends GraphEntity.ZrrVertex, F extends GraphEntity.PlEdge, R extends GraphEntity.RzEdge> GraphEntity<Q, Z, F, R> graphInit(GraphEntity.InitParams<Q, Z, F, R> params) {
        Graph<Q, Z, F, R> graph = graphService.graphInit(params);

        Set<String> qyIdSet = graph.getQyMap().keySet();
        FpDataParams fpDataParams = params.getSyParams();
        Set<String> keys = id2Key(qyIdSet);

        Map<String, Double> jxzeMap = fpService.readJxze(qyIdSet, fpDataParams);
        Map<String, Double> xxzeMap = fpService.readXxze(qyIdSet, fpDataParams);
        Map<String, Nsr> nsrMap = nsrService.getNsrBatch(keys);
        Map<String, Collection<String>> signMap = signService.getSignMap(keys);

        return new GraphEntity<Q, Z, F, R>(graph)
                .fillQy(jxzeMap, xxzeMap, nsrMap, signMap)
                .fillZrr()
                .deepKind(params.getCenterNode(), params.getSyDeep(), params.getXyDeep())
                .fillRyglqy(params.getCenterNode())
                .fillSxy()
                .unitTransform();
    }

    @Override
    public <Q extends GraphEntity.QyVertex, Z extends GraphEntity.ZrrVertex, F extends GraphEntity.PlEdge, R extends GraphEntity.RzEdge> GraphEntity<Q, Z, F, R> graphExpand(GraphEntity.ExpandParams<Q, Z, F, R> params) {
        Graph<Q, Z, F, R> graph = graphService.graphExpand(params);

        Set<String> qyIdSet = graph.getQyMap().keySet();
        FpDataParams fpDataParams = params.getSyParams();
        Set<String> keys = id2Key(qyIdSet);

        Map<String, Double> jxzeMap = fpService.readJxze(qyIdSet, fpDataParams);
        Map<String, Double> xxzeMap = fpService.readXxze(qyIdSet, fpDataParams);
        Map<String, Nsr> nsrMap = nsrService.getNsrBatch(keys);
        Map<String, Collection<String>> signMap = signService.getSignMap(keys);

        return new GraphEntity<Q, Z, F, R>(graph)
                .fillQy(jxzeMap, xxzeMap, nsrMap, signMap)
                .fillZrr()
                .fillSxy()
                .unitTransform();
    }
}
