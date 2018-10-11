package com.lightbend.akka.sample.example.backpressure



import java.io.File


import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.util.ByteString
import scala.concurrent.duration._
import akka.stream.scaladsl._
import scala.concurrent.Future

//https://chariotsolutions.com/blog/post/simply-explained-akka-streams-backpressure/
object App1  {

  def lineSink(filename: String): Sink[String, Future[IOResult]] = {
    Flow[String]
      .alsoTo(Sink.foreach(s => println(s"$filename: $s")))
      .map(s => ByteString(s + "\n"))
      .toMat(FileIO.toPath(new File(filename).toPath))(Keep.right)
  }

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("streamapp")
    implicit val materializer = ActorMaterializer()

    val source: Source[Int, NotUsed] = Source(1 to 100)
    val factorials: Source[BigInt, NotUsed] =
      source.scan(BigInt(1))((acc, next) => acc * next)
    val sink1 = lineSink("factorial1.txt")
    val sink2 = lineSink("factorial2.txt")


    val slowSink2 = Flow[String]
      .via(
        Flow[String]
          // sink signalize demand to source = 1 msg / 1 sec (back pressure)
          .throttle(1, 1.second, 1, ThrottleMode.shaping))
      .toMat(sink2)(Keep.right)

    // slow sink can buffer a preconfigured number of elements
    // handle fast&slow sinks
    // element will drop if buffer is full
    val bufferedSink2 = Flow[String].buffer(50, OverflowStrategy.dropNew)
      .via(Flow[String]
        .throttle(1, 1.second, 1, ThrottleMode.shaping))
      .toMat(sink2)(Keep.right)

    val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val bcast = b.add(Broadcast[String](2))
      factorials.map(_.toString) ~> bcast.in
      bcast.out(0) ~> sink1
      bcast.out(1) ~> sink2
      ClosedShape
    })

    g.run()
  }


}
