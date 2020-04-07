package mq;

import org.pitaya.kernel.PitayaApplication;
import org.pitaya.kernel.PitayaRunner;


/**
 * @author michael
 *
 */
@PitayaRunner
public class PluginRunner {

  public static void main(String[] args) {
    PitayaApplication.run(PluginRunner.class, args);
  }

}
