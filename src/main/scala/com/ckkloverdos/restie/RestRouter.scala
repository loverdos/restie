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

import scala.collection.JavaConverters._
import java.lang.Class
import java.lang.reflect.{InvocationTargetException, Method}

import RestLog.log

/**
 * Provides logic that routes a given URI path according to which [[com.ckkloverdos.restie.RestPathPattern]] can match the path.
 */
trait RestRouter {
  def pathManager: RestPathManager

  /**
   * The basic route method.
   */
  @throws(classOf[RestRoutingClientException])
  @throws(classOf[RestRoutingUnhandledPathException])
  @throws(classOf[RestRoutingUnknownPathException])
  def route(routingContext: RestRoutingContext, restMethod: String, path: String): Unit
}

/**
 * A marker interface for a rest routing context.
 * Such a context usually holds information about the request lifecycle.
 * For example, under a servlet environment, the context may hold the actual
 * HTTP request and response, any available HTTP session etc.
 */
trait RestRoutingContext

class ReflectiveRestRouter(_pathManager: RestPathManager, _handler: AnyRef) extends RestRouter {
  def pathManager = _pathManager

  def handler = _handler

  def findMethod(cls: Class[_], name: String, args: Array[Class[_]]): Option[Method] =
    try {
      val method = cls.getMethod(name, args: _*)
      log.debug("Found reflective " + method)
      Some(method)
    } catch {
      case _: NoSuchMethodException =>
        None
    }

  @throws(classOf[RestRoutingClientException])
  @throws(classOf[RestRoutingUnhandledPathException])
  @throws(classOf[RestRoutingUnknownPathException])
  def route(routingContext: RestRoutingContext, restMethod: String, path: String) = {
    try {
      pathManager.findMatcher(path) match {
        case Some(pathMatcher) =>
          log.debug(format("In route(\"%s\", \"%s\"), matcher = %s", restMethod, path, pathMatcher))
          val pathPattern = pathMatcher.pattern
          val handlerClass = handler.getClass
          val rcClass = routingContext.getClass
          val reflectiveMethodName = restMethod + "_" + pathPattern.name

          val argClasses: Array[Class[_]] = Array.tabulate(pathPattern.variableCount + 1)(index =>
            if(0 == index) rcClass else classOf[String]
          )

          findMethod(handlerClass, reflectiveMethodName, argClasses) match {
            case Some(reflectiveMethod) =>
              reflectiveMethod.setAccessible(true)
              reflectiveMethod.invoke(handler, (Array(routingContext) ++ pathPattern.variablesArray.map(pathMatcher.valueMap(_))): _*)
            case None =>
              findMethod(handlerClass, reflectiveMethodName, Array(rcClass, classOf[Map[String, String]])) match {
                case Some(reflectiveMethod) =>
                  reflectiveMethod.setAccessible(true)
                  reflectiveMethod.invoke(handler, routingContext, pathMatcher.valueMap)
                case None =>
                  findMethod(handlerClass, reflectiveMethodName, Array(rcClass, classOf[java.util.Map[String, String]])) match {
                    case Some(reflectiveMethod) =>
                      reflectiveMethod.setAccessible(true)
                      reflectiveMethod.invoke(handler, routingContext, pathMatcher.valueMap.asJava)
                    case None =>
                      throw new RestRoutingUnhandledPathException(restMethod, path)
                  }
              }
          }
        case None =>
          throw new RestRoutingUnknownPathException(restMethod, path)
      }
    }
    catch {
      case e: InvocationTargetException =>
        throw new RestRoutingClientException(e.getTargetException, format("While routing %s %s", restMethod, path))
    }
  }
}
