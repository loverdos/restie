package com.ckkloverdos.restie

import com.ckkloverdos.restie.RestPathPattern.makePath

import org.junit.Test
import org.junit.Assert._

/**
 * @author Christos KK Loverdos.
 */

class GenericTest {
  val Rest1 = "rest1"
  val Rest2 = "rest2"
  val _Customer = "customer"
  val _Book = "book"
  val _Balances = "balances"

  val p1 = new RestPathPattern("CUST", makePath(Rest1, "{" + _Customer + "}", "{" + _Book + "}"))
  val p2 = new RestPathPattern("BALN", makePath(Rest2, "{" + _Balances + "}"))
  val mgr = new RestPathManager().add(p1).add(p2)


  val MyFirstCustomer = "MyFirstCustomer"
  val LordOfTheRings = "LordOfTheRings"
  val MyBalances = "MyBalances"

  val VarMap1 = Map(_Customer -> MyFirstCustomer, _Book -> LordOfTheRings)
  val VarMap2 = Map(_Balances -> MyBalances)

  val t1 = List(Rest1, MyFirstCustomer, LordOfTheRings).mkString("/", "/", "")
  val t2 = List(Rest2, MyBalances).mkString("/", "/", "")

  val rc = new DefaultRestRoutingContext

  private def assertSome[A](x: Option[A]) =
    assertTrue(x.isDefined)

  private def assertSome[A](x: Option[A], m: String) =
    assertTrue(m, x.isDefined)

  private def _testMatcher(tX: String, pX: RestPathPattern, mapX: Map[String, String]): Unit = {
    val mXOpt = mgr.findMatcher(tX)
    assertSome(mXOpt, "Could not find matcher for " + tX)

    val mX = mXOpt.get
    assertEquals ("Wrong pattern", pX, mX.pattern)

    val vm2 = mX.valueMap
    assertEquals(mapX, vm2)
  }

  @Test
  def testMatcher1 =
    _testMatcher(t1, p1, VarMap1)

  @Test
  def testMatcher2 =
    _testMatcher(t2, p2, VarMap2)

  @Test
  def routeDefault_1 = {
    var routed = false
    val handler = new {
      def routeDefault(rc: DefaultRestRoutingContext, method: String, path: String) {
        routed = true
      }
    }

    val router = new ReflectiveRestRouter(rc, mgr, handler)
    router.route("GET", t1)
    assertEquals(true, routed)
  }

  @Test
  def routeUnmatched_1 = {
    var routed = false
    val handler = new {
      def routeUnmatched(rc: DefaultRestRoutingContext, method: String, path: String) {
        routed = true
      }
    }

    val router = new ReflectiveRestRouter(rc, mgr, handler)
    router.route("GET", "/Some garbage that will not match/")
    assertEquals(true, routed)
  }

  @Test
  def testRouterVarArgs_1 = {
    var routed = false
    var customer: String = null
    var book: String = null
    val handler = new {
      def GET_CUST(rc: DefaultRestRoutingContext, _customer: String, _book: String): Unit = {
        customer = _customer
        book = _book
        routed = true
      }
    }
    val router = new ReflectiveRestRouter(rc, mgr, handler)
    
    router.route("GET", t1)
    assertEquals(true, routed)
    assertEquals(MyFirstCustomer, customer)
    assertEquals(LordOfTheRings, book)
  }

  @Test
  def testRouterMap_1 = {
    var routed = false
    var customer: String = null
    var book: String = null
    val handler = new {
      def GET_CUST(rc: DefaultRestRoutingContext, valueMap: Map[String, String]): Unit = {
        assertEquals(2, valueMap.size)
        customer = valueMap(_Customer)
        book     = valueMap(_Book)
        routed = true
      }
    }
    val router = new ReflectiveRestRouter(rc, mgr, handler)

    router.route("GET", t1)
    assertEquals(true, routed)
    assertEquals(MyFirstCustomer, customer)
    assertEquals(LordOfTheRings, book)
  }

  @Test
  def testRouterJavaMap_1 = {
    var routed = false
    var customer: String = null
    var book: String = null
    val handler = new {
      def GET_CUST(rc: DefaultRestRoutingContext, valueMap: java.util.Map[String, String]): Unit = {
        assertEquals(2, valueMap.size)
        customer = valueMap.get(_Customer)
        book     = valueMap.get(_Book)
        routed = true
      }
    }
    val router = new ReflectiveRestRouter(rc, mgr, handler)

    router.route("GET", t1)
    assertEquals(true, routed)
    assertEquals(MyFirstCustomer, customer)
    assertEquals(LordOfTheRings, book)
  }

  @Test
  def testRouterVarArgs_2 = {
    var routed = false
    var balances: String = null
    val handler = new {
      def GET_BALN(rc: DefaultRestRoutingContext, _balances: String): Unit = {
        balances = _balances
        routed = true
      }
    }
    val router = new ReflectiveRestRouter(rc, mgr, handler)

    router.route("GET", t2)
    assertEquals(true, routed)
    assertEquals(MyBalances, balances)
  }

  @Test
  def testRouterMap_2 = {
    var routed = false
    var balances: String = null
    val handler = new {
      def GET_BALN(rc: DefaultRestRoutingContext, valueMap: Map[String, String]): Unit = {
        assertEquals(1, valueMap.size)
        balances = valueMap(_Balances)
        routed = true
      }
    }
    val router = new ReflectiveRestRouter(rc, mgr, handler)

    router.route("GET", t2)
    assertEquals(true, routed)
    assertEquals(MyBalances, balances)
  }

  @Test
  def testRouterJavaMap_2 = {
    var routed = false
    var balances: String = null
    val handler = new {
      def GET_BALN(rc: DefaultRestRoutingContext, valueMap: java.util.Map[String, String]): Unit = {
        assertEquals(1, valueMap.size)
        balances = valueMap.get(_Balances)
        routed = true
      }
    }
    val router = new ReflectiveRestRouter(rc, mgr, handler)

    router.route("GET", t2)
    assertEquals(true, routed)
    assertEquals(MyBalances, balances)
  }
}
