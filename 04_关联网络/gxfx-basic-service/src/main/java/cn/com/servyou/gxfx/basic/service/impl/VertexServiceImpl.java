package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.gxfx.basic.repository.VertexRepository;
import cn.com.servyou.gxfx.basic.service.VertexService;
import cn.com.servyou.gxfx.model.Vertex;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * @author lpp
 * 2018-11-15
 */
@Service
public class VertexServiceImpl implements VertexService {
    private final VertexRepository repository;

    @Autowired
    public VertexServiceImpl(@NonNull VertexRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends Vertex> Map<String, T> vertex(@NonNull Set<String> ids, @NonNull Class<T> tClass) {
        return repository.vertex(ids, tClass);
    }
}
