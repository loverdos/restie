package com.ckkloverdos.restie

import java.lang.reflect.Method
import scala.collection.JavaConverters._
import reflect.BeanProperty
import java.lang.Class

/**
 * Provides logic that routes a given URI path according to which {@link RestPathPattern} can match the path.
 */
trait RestRouter {
  def pathManager: RestPathManager
  def routingContext: RestRoutingContext

  /**
   * The basic route method. You should call this one.
   */
  def route(restMethod: String, path: String): Option[Exception]

  /**
   * Gets called when the path is recognized by the
   * path manager but no specialized handler method is found.
   */
  def routeDefault(restMethod: String, path: String): Unit

  /**
   * Gets called when the path is not recognized by
   * the path manager.
   */
  def routeUnmatched(restMethod: String, path: String): Unit
}

trait RestRoutingContext {
//  def getError: Exception
//  def setError(e: Exception): Unit
//  def hasError: Boolean
}

class DefaultRestRoutingContext extends RestRoutingContext {
//  private[this] var _error: Exception = _;
//
//  def hasError = null ne _error
//
//  def setError(e: Exception) = _error = e
//
//  def getError = _error
//
//  def error = _error
//  def error_=(e: Exception) = _error = e
}

class ReflectiveRestRouter(_routingContext: RestRoutingContext, _pathManager: RestPathManager, _handler: AnyRef) extends RestRouter {

  def pathManager = _pathManager
  def routingContext = _routingContext
  def handler = if(null eq _handler) this else _handler

  def findMethod(cls: Class[_], name: String, args: Array[Class[_]]): Option[Method] =
    try {
      Some(cls.getMethod(name, args: _*))
    } catch {
      case _: NoSuchMethodException =>
        None
      case e =>
        throw e
    }

  private[this] def _routeHandlerDefaultOrUnmatched(methodName: String, restMethod: String, path: String): Boolean = {
    val handlerClass = handler.getClass
    findMethod(handlerClass, methodName, Array(routingContext.getClass, classOf[String], classOf[String])) match {
      case Some(reflectiveMethod) =>
        reflectiveMethod.setAccessible(true)
        reflectiveMethod.invoke(handler, routingContext, restMethod, path)
        true
      case None =>
        println("**** DID NOT find reflective " + methodName)
        false
    }
  }

  private[this] def _routeDefault(restMethod: String, path: String): Unit = {
    if(!_routeHandlerDefaultOrUnmatched("routeDefault", restMethod, path))
      routeDefault(restMethod, path)
  }

  private[this] def _routeUnmatched(restMethod: String, path: String): Unit = {
    if(!_routeHandlerDefaultOrUnmatched("routeUnmatched", restMethod, path))
      routeUnmatched(restMethod, path)
  }

  def route(restMethod: String, path: String) = {
    try {
      pathManager.findMatcher(path) match {
        case Some(pathMatcher) =>
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
                      println("***** Routing Default [" + restMethod + "][" + path + "]")
                      _routeDefault(restMethod, path)
                  }
              }
          }
        case None =>
          println("***** Routing Unmatched[ " + restMethod + "][" + path + "]")
          _routeUnmatched(restMethod, path)
      }
      None
    }
    catch {
      case e: Exception =>
        Some(e)
    }
  }

  def routeDefault(restMethod: String, path: String) = {
    println("RestRouter::routeDefault [" + restMethod + "][" + path + "]")
  }

  def routeUnmatched(restMethod: String, path: String) = {
    println("RestRouter::routeUnmatched [" + restMethod + "][" + path + "]")
  }
}


class SelfReflectiveRestRouter(routingContext: RestRoutingContext, pathManager: RestPathManager) extends ReflectiveRestRouter(routingContext, pathManager, null)
