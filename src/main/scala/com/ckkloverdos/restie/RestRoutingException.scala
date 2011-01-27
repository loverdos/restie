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

/**
 * The base class for all rest routing exceptions.
 *
 * @see RestRoutingUnhandledPathException
 * @see RestRoutingUnknownPathException
 * @see RestRoutingClientException
 * @author Christos KK Loverdos.
 */
class RestRoutingException(cause: Throwable, msg: String) extends Exception(msg, cause)


/**
 * This exception must be thrown from client code during rest path routing, if anything goes wrong.
 *
 * @author Christos KK Loverdos.
 */
class RestRoutingClientException(cause: Throwable, msg: String) extends RestRoutingException(cause, msg) {
  def this(msg: String) = this(null, msg)
  def this(cause: Throwable) = this(cause, "")
}

/**
 *
 * @author Christos KK Loverdos.
 */
class   RestRoutingUnhandledPathException(restMethod: String, path: String)
extends RestRoutingException(null, format("Path [%s] for method [%s] was recognized but not handled", path, restMethod))


/**
 * 
 * @author Christos KK Loverdos.
 */
class   RestRoutingUnknownPathException(restMethod: String, path: String)
extends RestRoutingException(null, format("Path [%s] for method [%s] is unrecognizeable", path, restMethod))
