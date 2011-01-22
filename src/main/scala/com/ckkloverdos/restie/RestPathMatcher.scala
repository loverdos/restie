package com.ckkloverdos.restie

class RestPathMatcher(val pattern: RestPathPattern, val valueMap: Map[String, String]) {
  def valueForName(name: String) =
    valueMap(name)

  override def toString =
    List(pattern, valueMap).mkString("RestPathMatcher(", ",", ")")

  override def clone = super.clone.asInstanceOf[RestPathMatcher]

  override def equals(any: Any) =
    any.isInstanceOf[RestPathMatcher] && {
      val other = any.asInstanceOf[RestPathMatcher]
      other.pattern  == this.pattern &&
      other.valueMap == this.valueMap
    }
}
