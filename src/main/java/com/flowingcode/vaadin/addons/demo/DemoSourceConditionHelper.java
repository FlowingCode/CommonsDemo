/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2026 Flowing Code
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.flowingcode.vaadin.addons.demo;

import java.util.Map;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
class DemoSourceConditionHelper {

  public boolean eval(String condition, Map<String, String> env) {
    if (condition == null) {
      return true;
    }

    String[] expr = condition.split(" ");

    if (expr.length == 3) {
      String lhs = env.get(expr[0]);

      if (lhs == null) {
        return false;
      }

      String operator = expr[1];
      String rhs = expr[2];

      switch (operator) {
        case "lt":
          return compare(lhs, rhs) < 0;
        case "le":
          return compare(lhs, rhs) <= 0;
        case "eq":
          return compare(lhs, rhs) == 0;
        case "ge":
          return compare(lhs, rhs) >= 0;
        case "gt":
          return compare(lhs, rhs) > 0;
        case "ne":
          return compare(lhs, rhs) != 0;
        default:
          throw new IllegalArgumentException("Unknown operator: " + operator);
      }
    } else {
      throw new IllegalArgumentException("Invalid condition: '" + condition
          + "'. Must be exactly 3 components: [VARIABLE] [OPERATOR] [VERSION]");
    }
  }

  private int compare(String a, String b) {
    String[] aa = split(a);
    String[] bb = split(b);

    int minLength = Math.min(aa.length, bb.length);

    for (int i = 0; i < minLength; i++) {
      int ai = Integer.parseInt(aa[i]);
      int bi = Integer.parseInt(bb[i]);
      int c = Integer.compare(ai, bi);
      if (c != 0) {
        return c;
      }
    }

    return 0;
  }

  private String[] split(@NonNull String version) {
    if (!version.matches("^\\d+(\\.\\d+){0,2}$")) {
      throw new IllegalArgumentException("Invalid version: '" + version
          + "'. Must be 'major', 'major.minor', or major.minor.patch'");
    }
    return version.split("\\.");
  }
}
