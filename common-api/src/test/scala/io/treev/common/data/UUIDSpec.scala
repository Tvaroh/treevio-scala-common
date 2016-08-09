package io.treev.common.data

import java.io._

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UUIDSpec
  extends FlatSpec
    with Matchers
    with ScalaFutures {

  "apply" should "generate ids in lexicographic order" in {
    val ids = gen(NumIDs)
    assert(isSorted(ids))
  }

  it should "generate ids in lexicographic order in concurrent setting" in {
    val f1 = Future(gen(NumIDs))
    val f2 = Future(gen(NumIDs))
    val f3 = Future(gen(NumIDs))

    whenReady {
      for {
        ids1 ← f1
        ids2 ← f2
        ids3 ← f3
      } yield (ids1, ids2, ids3)
    } {
      case (ids1, ids2, ids3) ⇒
        assert(isSorted(ids1))
        assert(isSorted(ids2))
        assert(isSorted(ids3))
    }
  }

  it should "generate no duplicate ids" in {
    val ids = gen(NumIDs)
    assert(hasNoDuplicates(ids))
  }

  it should "generate no duplicate ids in concurrent setting" in {
    val f1 = Future(gen(NumIDs))
    val f2 = Future(gen(NumIDs))
    val f3 = Future(gen(NumIDs))

    whenReady {
      for {
        ids1 ← f1
        ids2 ← f2
        ids3 ← f3
      } yield (ids1, ids2, ids3)
    } {
      case (ids1, ids2, ids3) ⇒
        assert(hasNoDuplicates(ids1 ++ ids2 ++ ids3))
    }
  }

  "toString" should "yield value that can be turned back into the original ID" in {
    val id = gen()
    UUID(id.toString) should equal (id)
  }

  "toBytes" should "yield value that can be turned back into the original ID" in {
    val id = gen()
    UUID.fromBytes(id.toBytes) should equal (id)
  }

  "toBase64" should "yield value that can be turned back into the original ID" in {
    val id = gen()
    UUID.fromBase64(id.toBase64) should equal (id)
  }

  "id" should "support serialization/deserialization" in {
    val id = gen()

    val baos = new ByteArrayOutputStream(1000)
    val oos = new ObjectOutputStream(baos)
    oos.writeObject(id)
    oos.close()

    val ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray))
    val deserializedId = ois.readObject().asInstanceOf[UUID]
    ois.close()

    id should equal (deserializedId)
  }

  "equal" should "return true for equal UUIDs, false otherwise" in {
    val id = UUID()
    assert(id == id)
    assert(id == UUID(id.getTime, id.getMac, id.getSequence))
    assert(id != UUID())
  }

  "toJavaUUID" should "yield value that can be turned back into the original UUID" in {
    val id = UUID()
    UUID.fromJavaUUID(id.toJavaUUID) should equal (id)
  }

  // helpers

  private val NumIDs = 100000

  private def gen(): UUID = UUID()

  private def gen(n: Int): Seq[UUID] = {
    Stream.continually(gen()).take(n)
  }

  private def isSorted[A: Ordering](as: Seq[A]): Boolean =
    as == as.sorted

  private def hasNoDuplicates(as: Seq[Any]): Boolean =
    as.size == as.distinct.size

}
