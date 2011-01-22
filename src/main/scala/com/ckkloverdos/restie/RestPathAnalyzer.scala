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
