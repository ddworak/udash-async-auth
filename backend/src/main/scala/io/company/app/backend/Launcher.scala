package io.company.app.backend

import java.util.concurrent.TimeUnit
import com.typesafe.config.{Config, ConfigFactory}
import io.company.app.backend.server.ApplicationServer
import io.company.app.backend.services.{AuthService, ChatService, DomainServices, ExternalIdentityProvider, RpcClientsService}
import io.udash.logging.CrossLogging

import scala.io.StdIn

object Launcher extends CrossLogging {
  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime

    val config: Config = ConfigFactory.load()
    implicit val rpcClientsService: RpcClientsService = new RpcClientsService(RpcClientsService.defaultSendToClientFactory)
    implicit val authService: AuthService = new AuthService(new ExternalIdentityProvider)
    implicit val chatService: ChatService = new ChatService(rpcClientsService)
    val server = new ApplicationServer(config.getInt("server.port"), config.getString("server.statics"), new DomainServices)
    server.start()

    val duration: Long = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime - startTime)
    logger.info(s"Application started in ${duration}s.")

    // wait for user input and then stop the server
    logger.info("Click `Enter` to close application...")
    StdIn.readLine()
    logger.info("Stopping application")
    server.stop()
  }
}
