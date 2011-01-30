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

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
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
