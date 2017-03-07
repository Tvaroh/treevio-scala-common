package io.treev.common.util

import java.math.BigInteger
import java.net.NetworkInterface
import java.nio.ByteBuffer
import java.util
import java.util.Base64
import java.util.concurrent.atomic.AtomicReference

/** UUID in 'flake' format.
  * <p>Flake ids are 128-bits wide described here from most significant to least significant bits.
  * <ul>
  * <li>64-bit timestamp - milliseconds since the epoch (Jan 1 1970);</li>
  * <li>48-bit worker id - MAC address from a configurable device;</li>
  * <li>
  *   16-bit sequence number - usually 0, incremented when more than one id is requested in the same millisecond
  *   and reset to 0 when the clock ticks forward.
  * </li>
  * </ul> */
trait UUID extends Serializable {

  /** Get time part in milliseconds since the epoch (Jan 1 1970). */
  def getTime: Long
  /** Get MAC address part. */
  def getMac: Array[Byte]
  /** Get sequence part. */
  def getSequence: Short

  /** Get ID value as byte array. */
  def toBytes: Array[Byte]
  /** Get base64 representation. Includes no padding. */
  def toBase64: String
  /** Convert to [[java.util.UUID]]. */
  def toJavaUUID: java.util.UUID

}

/** UUID implementation.
  * @param time underlying time component
  * @param mac underlying hardware address
  * @param sequence underlying sequence component */
private class UUIDImpl(time: Long, mac: Array[Byte], sequence: Short) extends UUID {

  override def equals(obj: Any): Boolean = obj match {
    case that: UUID =>
      that.getTime == this.time && util.Arrays.equals(that.getMac, this.mac) && that.getSequence == this.sequence
    case _ =>
      false
  }

  /** Get UUID hashcode.
    * Delegates to [[java.util.UUID#hashCode]]. */
  override def hashCode: Int = toJavaUUID.hashCode()

  /** Get string representation in standard UUID format.
    * Delegates to [[java.util.UUID#toString]].
    * @return string in UUID format */
  override def toString: String = toJavaUUID.toString

  @transient override lazy val toBytes: Array[Byte] =
    UUID.toBytes(time, mac, sequence)

  @transient override lazy val toBase64: String =
    UUID.base64Encoder.encodeToString(toBytes)

  @transient override lazy val toJavaUUID: java.util.UUID = {
    val buf = ByteBuffer.wrap(toBytes)
    new java.util.UUID(buf.getLong(), buf.getLong())
  }

  override def getTime: Long = time
  override def getMac: Array[Byte] = util.Arrays.copyOf(mac, mac.length)
  override def getSequence: Short = sequence

}

object UUID {

  /** Empty (all zeroes) UUID. */
  val Empty: UUID = fromBytes(Array.empty)

  /** Generate new UUID.
    * @see [[UUID]] */
  def apply(): UUID = {
    val TimeAndSequence(time, sequence) = timeAndSequenceGenerator.generate()
    new UUIDImpl(time, MacAddress, sequence.toShort)
  }

  /** Hand-craft a UUID.
    * @param time underlying time component (milliseconds since the epoch (Jan 1 1970))
    * @param mac underlying hardware address (6 bytes)
    * @param sequence underlying sequence component
    * @see [[UUID]] */
  def apply(time: Long, mac: Array[Byte], sequence: Short): UUID = {
    require(mac.length == MacAddressSize, s"MAC address should be $MacAddressSize bytes wide")

    new UUIDImpl(time, mac, sequence)
  }

  /** Parse UUID from string representation.
    * Delegates to [[java.util.UUID]]. */
  def apply(name: String): UUID = {
    val javaUUID = java.util.UUID.fromString(name)
    fromJavaUUID(javaUUID)
  }

  /** Create UUID from byte array.
    * @param bytes byte array of size 16
    * @see [[UUID]] */
  def fromBytes(bytes: Array[Byte]): UUID =
    if (bytes.length > 0) {
      require(bytes.length == UUIDSize, s"UUID should be $UUIDSize bytes wide")

      val buf = ByteBuffer.wrap(bytes)
      val mac = new Array[Byte](MacAddressSize)

      val time = buf.getLong()
      buf.get(mac)
      val sequence = buf.getShort()

      apply(time, mac, sequence)
    } else {
      apply(0, Array.fill[Byte](MacAddressSize)(0), 0)
    }

  /** Create UUID from base64-encoded string.
    * @param s base64-encoded string */
  def fromBase64(s: String): UUID = {
    val bytes = base64Decoder.decode(s)
    fromBytes(bytes)
  }

  /** Create UUID from Java UUID.
    * @param javaUUID java UUID */
  def fromJavaUUID(javaUUID: java.util.UUID): UUID = {
    val buf = buffer.get()
    buf.rewind()
    buf.putLong(javaUUID.getMostSignificantBits)
    buf.putLong(javaUUID.getLeastSignificantBits)
    fromBytes(buf.array())
  }

  /** Implicit ordering for [[UUID]] values. */
  implicit object UUIDOrdering extends Ordering[UUID] {
    override def compare(x: UUID, y: UUID): Int =
      if (x eq y) 0
      else {
        if (x.getTime > y.getTime) 1
        else if (x.getTime < y.getTime) -1
        else {
          val result = new BigInteger(x.getMac).compareTo(new BigInteger(y.getMac))
          if (result != 0) result
          else {
            if (x.getSequence > y.getSequence) 1
            else if (x.getSequence < y.getSequence) -1
            else 0
          }
        }
      }
  }

  private val UUIDSize = 16
  private val MacAddressSize = 6

  private[util] val base64Encoder = Base64.getEncoder.withoutPadding()
  private val base64Decoder = Base64.getDecoder

  private val timeAndSequenceGenerator = new TimeAndSequenceGenerator(() => System.currentTimeMillis())

  private lazy val MacAddress: Array[Byte] = {
    import scala.collection.JavaConverters._

    val interfaces = NetworkInterface.getNetworkInterfaces
    interfaces.asScala.find { interface =>
      val mac = interface.getHardwareAddress
      mac != null && mac.length == MacAddressSize && mac(1) != 0xff
    } map {
      _.getHardwareAddress
    } getOrElse {
      sys.error("Cannot initialize flake id generator: MAC address not found")
    }
  }

  private[util] def toBytes(time: Long, mac: Array[Byte], sequence: Short): Array[Byte] = {
    val buf = buffer.get()

    buf.rewind()
    buf.putLong(time)
    buf.put(mac)
    buf.putShort(sequence)

    buf.rewind()
    val arr = new Array[Byte](UUIDSize)
    buf.get(arr)
    arr
  }

  private case class TimeAndSequence(time: Long, sequence: Short) {
    def incrementSequence: TimeAndSequence =
      if (sequence < Short.MaxValue) this.copy(sequence = (sequence + 1).toShort)
      else sys.error("Sequence value overflow")
  }

  private class TimeAndSequenceGenerator(
    getTime: () => Long,
    lastTimeAndSequence: AtomicReference[TimeAndSequence] = TimeAndSequenceGenerator.defaultTimeAndSequence
  ) {

    @annotation.tailrec
    final def generate(): TimeAndSequence =
      try {
        loop()
      } catch {
        case _: Throwable =>
          // can eat CPU cycles on high load, but it's better than sleep
          generate()
      }

    @annotation.tailrec
    private def loop(): TimeAndSequence = {
      val currentLastTimeAndSequence = lastTimeAndSequence.get()
      val currentTime = getTime()

      if (currentTime > currentLastTimeAndSequence.time) {
        val nextTimeAndSequence = TimeAndSequence(currentTime, 0)
        if (lastTimeAndSequence.compareAndSet(currentLastTimeAndSequence, nextTimeAndSequence)) nextTimeAndSequence
        else loop()
      } else if (currentTime == currentLastTimeAndSequence.time) {
        val nextTimeAndSequence = currentLastTimeAndSequence.incrementSequence
        if (lastTimeAndSequence.compareAndSet(currentLastTimeAndSequence, nextTimeAndSequence)) nextTimeAndSequence
        else loop()
      } else {
        loop()
      }
    }

  }

  private object TimeAndSequenceGenerator {
    def defaultTimeAndSequence: AtomicReference[TimeAndSequence] =
      new AtomicReference[TimeAndSequence](TimeAndSequence(0L, 0))
  }

  /** Per-thread byte buffer. */
  private val buffer: ThreadLocal[ByteBuffer] = new ThreadLocal[ByteBuffer] {
    override def initialValue(): ByteBuffer =
      ByteBuffer.allocate(UUIDSize)
  }

}

