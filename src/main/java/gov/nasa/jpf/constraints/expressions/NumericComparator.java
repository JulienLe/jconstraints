/**
 * Copyright 2020, TU Dortmund, Malte Mues (@mmuesly)
 *
 * This is a derived version of JConstraints original located at:
 * https://github.com/psycopaths/jconstraints
 *
 * Until commit: https://github.com/tudo-aqua/jconstraints/commit/876e377
 * the original license is:
 * Copyright (C) 2015, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The PSYCO: A Predicate-based Symbolic Compositional Reasoning environment
 * platform is licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Modifications and new contributions are Copyright by TU Dortmund 2020, Malte Mues
 * under Apache 2.0 in alignment with the original repository license.
 */
package gov.nasa.jpf.constraints.expressions;

/** numeric comparator */
public enum NumericComparator implements ExpressionOperator {
  EQ("==") {
    public NumericComparator not() {
      return NE;
    }

    public boolean eval(int cmpResult) {
      return (cmpResult == 0);
    }
  },
  NE("!=") {
    public NumericComparator not() {
      return EQ;
    }

    public boolean eval(int cmpResult) {
      return (cmpResult != 0);
    }
  },
  LT("<") {
    public NumericComparator not() {
      return GE;
    }

    public boolean eval(int cmpResult) {
      return (cmpResult < 0);
    }
  },
  LE("<=") {
    public NumericComparator not() {
      return GT;
    }

    public boolean eval(int cmpResult) {
      return (cmpResult <= 0);
    }
  },
  GT(">") {
    public NumericComparator not() {
      return LE;
    }

    public boolean eval(int cmpResult) {
      return (cmpResult > 0);
    }
  },
  GE(">=") {
    public NumericComparator not() {
      return LT;
    }

    public boolean eval(int cmpResult) {
      return (cmpResult >= 0);
    }
  };

  private final String str;

  private NumericComparator(String str) {
    this.str = str;
  }

  public abstract NumericComparator not();

  public abstract boolean eval(int cmpResult);

  @Override
  public String toString() {
    return str;
  }

  public static NumericComparator fromString(String str) {
    switch (str) {
      case "==":
        return EQ;
      case "!=":
        return NE;
      case "<":
        return LT;
      case "<=":
        return LE;
      case ">":
        return GT;
      case ">=":
        return GE;
      default:
        return null;
    }
  }
}
