package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.dao.daup.MyGraphRepository;
import cn.com.servyou.gxfx.basic.model.MyGraph;
import cn.com.servyou.gxfx.basic.service.MyGraphService;
import cn.com.servyou.tdap.web.PagerBean;
import com.github.pagehelper.PageHelper;
import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author lpp
 * 2018-11-29
 */
@Service
public class MyGraphServiceImpl implements MyGraphService {
    private final MyGraphRepository repository;

    @Autowired
    public MyGraphServiceImpl(MyGraphRepository repository) {
        this.repository = repository;
    }

    @Override
    public PagerBean<MyGraph> getByUserId(@NonNull String userId, @NonNull MyGraph.OrderBy orderBy, int pageSize, int pageIndex) {
        PageHelper.startPage(pageIndex, pageSize);
        PageHelper.orderBy(orderBy.getValue());
        return new PagerBean<MyGraph>(repository.getByUserId(userId));
    }

    @Override
    public MyGraph get(@NonNull String graphId) {
        return repository.get(graphId);
    }

    @Override
    public void delete(@NonNull String graphId) {
        repository.delete(graphId);
    }

    @Override
    public void add(@NonNull MyGraph graph) {
        repository.add(graph);
    }

    @Override
    public void update(@NonNull MyGraph graph) {
        repository.update(graph);
    }

    @Override
    public void save(MyGraph graph) {
        if (StringUtils.isEmpty(graph.getGraphId())) {
            graph.setGraphId(UUID.randomUUID().toString());
            add(graph);
        } else {
            update(graph);
        }
    }
}
