import akka.routing.ConsistentHashingRouter.ConsistentHashable

case class SendInfo(title: String, url: String)
case object Flush
//case class Flush() extends ConsistentHashable {
//  override def consistentHashKey: Any = ""
//}
case object Done
case object START