package com.ckkloverdos.restie

import java.util.regex.Pattern
import scala.collection.{mutable}
import annotation.tailrec
import java.lang.String

object RestPathPattern {
  val RestPathSpecPattern = Pattern.compile("/([^/]+)")

  @tailrec
  def stripStartSlash(path: String): String =
    if(path.startsWith("/"))
      stripStartSlash(path.substring(1))
    else
      path

  @tailrec
  def stripStartMultipleSlashes(path: String): String =
    if(path.startsWith("//"))
      stripStartMultipleSlashes(path.substring(1))
    else
      path

  @tailrec
  def stripEndSlash(path: String): String =
    if(path.endsWith("/"))
      stripEndSlash(path.substring(0, path.length - 1))
    else
      path

  @tailrec
  def stripEndMultipleSlashes(path: String): String =
    if(path.endsWith("//"))
      stripEndMultipleSlashes(path.substring(0, path.length - 1))
    else
      path

  def stripStartEndMultipleSlashes(path: String) =
    stripStartMultipleSlashes(stripEndMultipleSlashes(path))

  def makePath(parts: String*) =
    parts.map(part => stripStartSlash(stripEndSlash(part))).mkString ("/", "/", "")
}

class RestPathPattern(val name: String, _pattern: String) extends Cloneable {
//  println("** _pattern = " + _pattern)
  if(null eq _pattern) {
    throw new IllegalArgumentException("null pattern");
  } else if(!_pattern.startsWith("/")) {
    throw new IllegalArgumentException("pattern " + pattern + " does not start with /");
  } else if(_pattern.indexOf("//") >= 0) {
    throw new IllegalArgumentException("pattern " + pattern + " contains invalid sequence //");
  }

  val pattern = if(_pattern.endsWith("/"))
    _pattern.substring(0, _pattern.length - 1)
  else
    _pattern
  
  private[this] val (_elements, _variables, _isVariableAtPosition) = {
    val elements = new mutable.ArrayBuffer[String]
    val variables = new mutable.ArrayBuffer[String]
    val elementIsVariable = new mutable.BitSet
    val matcher = RestPathPattern.RestPathSpecPattern.matcher(pattern)

    def findAll: Unit = {
      if(matcher.find) {
        val element = matcher.group(1)
        val startsWithBrace = element.startsWith("{");
        val endsWithBrace = element.endsWith("}");
        
        if(startsWithBrace && endsWithBrace) {
          elementIsVariable(elements.size) = true;
          val variable = element.substring(1, element.length - 1)
          elements  += variable
          variables += variable
        } else if(startsWithBrace || endsWithBrace) {
          throw new IllegalArgumentException(format("Error parsing element %s [%s] of pattern: %s", elements.size, element, pattern));
        } else {
          elements += element
        }

        findAll
      }
    }

    findAll

    (elements.toArray, variables.toArray, elementIsVariable)
  }

  override def clone = super.clone.asInstanceOf[RestPathPattern]


  override def hashCode = pattern.##
  override def equals(any: Any) =
    any.isInstanceOf[RestPathPattern] &&
    any.asInstanceOf[RestPathPattern].pattern == pattern
  
  override def toString = "RestPathPattern(" + pattern + ")"

  def elementsArray                = this._elements
  def elementAtPosition(pos: Int)  = this._elements(pos - 1)
  def elementCount                 = this._elements.size
  def variablesArray               = this._variables
  def variableCount                = this._variables.size
  def isVariablePosition(pos: Int) = this._isVariableAtPosition(pos - 1)
  def variableAtPosition(pos: Int) = this._elements(pos - 1)
}
