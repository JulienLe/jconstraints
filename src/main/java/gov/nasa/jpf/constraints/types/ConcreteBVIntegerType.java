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
package gov.nasa.jpf.constraints.types;

import java.math.BigInteger;

public abstract class ConcreteBVIntegerType<T> extends ConcreteIntegerType<T>
    implements BVIntegerType<T> {

  private final int numBits;

  public ConcreteBVIntegerType(
      String name,
      Class<T> canonicalClass,
      T defaultValue,
      int numBits,
      boolean signed,
      BigInteger min,
      BigInteger max,
      Type<?> superType,
      String[] otherNames,
      Class<?>... otherClasses) {
    super(
        name, canonicalClass, defaultValue, signed, min, max, superType, otherNames, otherClasses);
    this.numBits = numBits;
  }

  @Override
  public int getNumBits() {
    return numBits;
  }
}
