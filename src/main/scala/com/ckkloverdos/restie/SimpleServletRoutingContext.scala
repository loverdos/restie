package com.ckkloverdos.restie

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import reflect.BeanProperty

/**
 * A [[com.ckkloverdos.restie.RestRoutingContext]] for use in a servlet container.
 * The context is made of the servlet request and response.
 *
 * Both the request and the response are available as classic Java getters as well.
 * This is for convenience if you use he API from Java.
 *
 * @author Christos KK Loverdos.
 */
final class SimpleServletRoutingContext(
      val request: HttpServletRequest,
      val response: HttpServletResponse)
extends RestRoutingContext {
  def getRequest = request
  def getResponse = response
}

/**
 * A [[com.ckkloverdos.restie.RestRoutingContext]] for use in a servlet container.
 * The context is made of the servlet request and response as well as an application-specific
 * object.
 *
 * The request, the response and the application-specific data are all available as classic Java getters as well.
 * This is for convenience if you use he API from Java.
 * 
 * @author Christos KK Loverdos.
 */
final class RichServletRoutingContext[A](
      contextData: A,
      val request: HttpServletRequest,
      val response: HttpServletResponse)
extends RestRoutingContext {
  def getContextData = contextData
  def getRequest = request
  def getResponse = response
}
