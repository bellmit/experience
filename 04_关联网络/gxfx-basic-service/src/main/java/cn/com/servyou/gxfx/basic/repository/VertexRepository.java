package cn.com.servyou.gxfx.basic.repository;

import cn.com.servyou.gxfx.model.Vertex;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.util.MapBuilder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lpp
 * 2018-11-15
 */
@Slf4j
@Repository
public class VertexRepository {
    private final ArangoDatabase db;

    @Autowired
    public VertexRepository(@NonNull ArangoDatabase db) {
        this.db = db;
    }

    @NonNull
    public <T extends Vertex> Map<String, T> vertex(@NonNull Set<String> ids, @NonNull Class<T> tClass) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        } else {
            String aql = "for v in V " +
                    "filter v._id in @ids " +
                    "return v";

            Map<String, Object> vars = new MapBuilder()
                    .put("ids", ids)
                    .get();

            ArangoCursor<T> cursor = db.query(aql, vars, null, tClass);
            HashMap<String, T> result = new HashMap<String, T>(ids.size());
            for (T v : cursor) {
                result.put(v.getId(), v);
            }

            if (result.size() < ids.size()) {
                for (String s : ids) {
                    if (!result.containsKey(s)) {
                        log.error("vertex: {} not found in XKCA_VERTEX collection.", s);
                    }
                }
            }

            return result;
        }
    }
}
