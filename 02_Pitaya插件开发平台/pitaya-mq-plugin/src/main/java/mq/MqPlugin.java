package mq;

import org.pitaya.kernel.PitayaPlugin;
import org.pitaya.kernel.PitayaPluginType;

public class MqPlugin implements PitayaPlugin {

  @Override
  public String name() {
    return "mq";
  }

  @Override
  public String description() {
    return "mq plugin";
  }

  @Override
  public int order() {
    return 12;
  }



  @Override
  public boolean hasEntity() {
    return false;
  }

  @Override
  public void init() {
    // do nothing
  }

  @Override
  public String datasource() {
    return null;
  }

  @Override
  public PitayaPluginType plgType() {
    return PitayaPluginType.INFRA_PLUGIN;
  }
}
