package mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.MoreObjects;
import org.pitaya.kernel.PitayaException;

public class MqMsg<K, V> {
  private String topic;
  private String keyClass;
  private String valClass;
  private K key;
  private V val;

  public MqMsg() {
    // for json convert
  }

  public MqMsg(Topic<K, V> topic, K k, V v) {
    this(topic.name, topic.keyClass.getName(), topic.valClass.getName(), k, v);
  }

  public MqMsg(String topic, String keyClass, String valClass, K key, V val) {
    this.topic = topic;
    this.keyClass = keyClass;
    this.valClass = valClass;
    this.key = key;
    this.val = val;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
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

  public K getKey() {
    return key;
  }

  public void setKey(K key) {
    this.key = key;
  }

  public V getVal() {
    return val;
  }

  public void setVal(V val) {
    this.val = val;
  }

  public Topic topic() {
    return Topic.getOrCreate(topic, forName(keyClass), forName(valClass));
  }

  public static <K, V> String encode(MqMsg<K, V> msg) {
    return JSON.toJSONString(msg);
  }

  public static MqMsg decode(String json) {
    MqMsg msg = JSON.parseObject(json, MqMsg.class);
    msg.decode();
    return msg;
  }

  public void decode() {
    if (key instanceof JSONObject) {
      key = parseJsonObject((JSONObject) key, keyClass());
    }
    if (val instanceof JSONObject) {
      val = parseJsonObject((JSONObject) val, valClass());
    }
  }

  private Class<K> keyClass() {
    return forName(keyClass);
  }

  private Class<V> valClass() {
    return forName(valClass);
  }

  private <T> T parseJsonObject(JSONObject jo, Class<T> aClass) {
    return jo.toJavaObject(aClass);
  }

  private Class forName(String kvClass) {
    try {
      return Class.forName(kvClass);
    } catch (ClassNotFoundException e) {
      throw new PitayaException("class not found", e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MqMsg<?, ?> mqMsg = (MqMsg<?, ?>) o;

    if (!topic.equals(mqMsg.topic)) {
      return false;
    }
    if (!keyClass.equals(mqMsg.keyClass)) {
      return false;
    }
    if (!valClass.equals(mqMsg.valClass)) {
      return false;
    }
    if (!key.equals(mqMsg.key)) {
      return false;
    }
    return val.equals(mqMsg.val);

  }

  @Override
  public int hashCode() {
    int result = topic.hashCode();
    result = 31 * result + keyClass.hashCode();
    result = 31 * result + valClass.hashCode();
    result = 31 * result + key.hashCode();
    result = 31 * result + val.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("topic", topic).add("keyClass", keyClass)
        .add("valClass", valClass).add("key", key).add("val", val).toString();
  }
}
