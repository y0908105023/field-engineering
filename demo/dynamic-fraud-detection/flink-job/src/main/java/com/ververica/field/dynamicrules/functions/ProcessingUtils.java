package com.ververica.field.dynamicrules.functions;

import com.ververica.field.dynamicrules.Rule;
import java.util.HashSet;
import java.util.Set;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapState;

class ProcessingUtils {

  static void handleRuleBroadcast(Rule rule, BroadcastState<Integer, Rule> broadcastState)
      throws Exception {
    switch (rule.getRuleState()) {
      case ACTIVE:
      case PAUSE:
        broadcastState.put(rule.getRuleId(), rule);
        break;
      case DELETE:
        // TODO: add logic to find a new "widest window" in case associated rule of the current one
        // gets removed
        broadcastState.remove(rule.getRuleId());
        break;
    }
  }

  static <K, V> Set<V> addToStateValuesSet(MapState<K, Set<V>> mapState, K key, V value)
      throws Exception {

    Set<V> valuesSet = mapState.get(key);

    if (valuesSet != null) {
      valuesSet.add(value);
    } else {
      valuesSet = new HashSet<>();
      valuesSet.add(value);
    }
    mapState.put(key, valuesSet);
    return valuesSet;
  }
}
