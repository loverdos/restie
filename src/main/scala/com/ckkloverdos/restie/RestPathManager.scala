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

import java.util.concurrent

class RestPathManager {
  type Length = Int

  private[this] var _patternByName = Map[String, RestPathPattern]()
  private[this] var _patternListByPathLength = Map[Length, List[RestPathPattern]]()

  def add(pattern: RestPathPattern): RestPathManager  = synchronized {
    _patternByName += pattern.name -> pattern

    val patternElementCount = pattern.elementCount
//    println("** patternElementCount = " + patternElementCount)
    _patternListByPathLength.get(patternElementCount) match {
      case Some(list) =>
        _patternListByPathLength = _patternListByPathLength updated (patternElementCount, pattern :: list)
      case None =>
        _patternListByPathLength = _patternListByPathLength updated (patternElementCount, pattern :: Nil)
    }

//    println("** patternListByPathLength = " + patternListByPathLength)
    this
  }

  def findPatternByName(name: String) =
    _patternByName.get(name)

  def findMatcher(path: String): Option[RestPathMatcher] = {
//    println("**************** findMatcher [" + path + "] ****************")
    val analyzer = new RestPathAnalyzer(path)
    val pathPartsCount = analyzer.pathPartsCount
//    println("** pathPartsCount = " + pathPartsCount)

    this._patternListByPathLength.get(pathPartsCount) match {
      case Some(patternList) =>
//        println("** Checking patternList = " + patternList)
        // Find the first pattern
        var valueMap = null: Map[String, String]
        val patternO = patternList find { pattern =>
//          println("** Checking pattern = " + pattern)
          valueMap = Map[String, String]()
          // ... whose elements match all the analyzed path elements
          analyzer.pathParts.view.zipWithIndex.forall { case (element, index) =>
//            println("** checking element = " + element)
            if(pattern.isVariablePosition(index + 1)) {
              val variable = pattern.variableAtPosition(index + 1)
              valueMap += variable -> element
//              println("** found variable " + variable + ", now valueMap = " + valueMap)
              true
            } else {
              element == pattern.elementAtPosition(index + 1)
            }
          }
        }

        patternO map { pattern => new RestPathMatcher(pattern, valueMap) }
      case None =>
        None
    }
  }
}
