/*
 * Copyright 2010-2011 Christos KK Loverdos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ckkloverdos.restie

import scala.collection.mutable
import java.lang.String
import annotation.tailrec

/**
 * @author Christos KK Loverdos.
 */

class RestPathAnalyzer(_path: String) {
  val path = RestPathPattern.stripStartEndMultipleSlashes(_path)
  
  private[this] val _pathParts = {
    val buf = new mutable.ArrayBuffer[String]
    val matcher = RestPathPattern.RestPathSpecPattern.matcher(path)

    @tailrec
    def findAll: Unit =
      if(matcher.find) {
        val found = matcher.group(1)
//        println("** Analyzer found = " + found)
        buf += found
        findAll
      }

    findAll

    buf.toArray
  }

  def pathParts = _pathParts
  
  def pathPartsCount = _pathParts.length

  /**
   * Positions always start with 1.
   */
  def pathPartAtPosition(pos: Int) = _pathParts(pos - 1)


  /**
   * Indexes always start with 0.
   */
  def pathPartAtIndex(index: Int) = _pathParts(index)
}
