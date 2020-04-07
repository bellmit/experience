package mq;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.pitaya.kernel.PitayaException;

import java.util.Map;

public class Topic<K, V> {
  private static final Map<String, Topic> topics = Maps.newConcurrentMap();

  public final String name;
  public final Class<K> keyClass;
  public final Class<V> valClass;

  private Topic(String name, Class<K> keyClass, Class<V> valClass) {
    Preconditions.checkArgument(!StringUtils.isEmpty(name));
    this.name = name;
    this.keyClass = keyClass;
    this.valClass = valClass;
  }

  public static <K, V> Topic<K, V> getOrCreate(@NotNull String name, @NotNull Class<K> keyClass,
      @NotNull Class<V> valClass) {
    Preconditions.checkArgument(!StringUtils.isEmpty(name));
    Preconditions.checkNotNull(keyClass);
    Preconditions.checkNotNull(valClass);

    if (topics.containsKey(name)) {
      Topic topic = topics.get(name);
      if (topic.keyClass.equals(keyClass) && topic.valClass.equals(valClass)) {
        return topic;
      } else {
        throw new PitayaException(String.format(
            "Topic %s has exist, but key or val class not equals.", name));
      }
    } else {
      Topic<K, V> topic = new Topic<>(name, keyClass, valClass);
      topics.put(name, topic);
      return topic;
    }
  }

  public static <K, V> Topic<K, V> getOrCreate(@NotNull String name, @NotNull String keyClass,
      @NotNull String valClass) {
    Preconditions.checkArgument(!StringUtils.isEmpty(name));
    Preconditions.checkArgument(!StringUtils.isEmpty(keyClass));
    Preconditions.checkArgument(!StringUtils.isEmpty(valClass));

    return getOrCreate(name, forName(keyClass), forName(valClass));
  }

  private static Class forName(String kvClass) {
    try {
      return Class.forName(kvClass);
    } catch (ClassNotFoundException e) {
      throw new PitayaException("class not found", e);
    }
  }

  public static <K, V> String encode(Topic<K, V> topic) {
    return JSON.toJSONString(new TopicAlias(topic.name, topic.keyClass.getName(), topic.valClass
        .getName()));
  }

  public static <K, V> Topic<K, V> decode(String json) {
    TopicAlias as = JSON.parseObject(json, TopicAlias.class);
    return Topic.getOrCreate(as.name, as.keyClass, as.valClass);
  }

  private static class TopicAlias {
    private String name;
    private String keyClass;
    private String valClass;

    public TopicAlias() {
      // for json convert
    }

    public TopicAlias(String name, String keyClass, String valClass) {
      this.name = name;
      this.keyClass = keyClass;
      this.valClass = valClass;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getKeyClass() {
      return keyClass;
    }

    public void setKeyClass(String keyClass) {
      this.keyClass = keyClass;
    }

    public String getValClass() {
      return valClass;
    }

    public void setValClass(String valClass) {
      this.valClass = valClass;
    }
  }
}
