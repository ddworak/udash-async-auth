package io.company.app.backend.services

import io.company.app.shared.model.auth.UserContext
import io.company.app.shared.rpc.client.MainClientRPC
import io.company.app.shared.rpc.client.chat.ChatNotificationsRPC
import io.udash.rpc.{ClientId, ClientRPCTarget}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RpcClientsServiceTest extends AnyWordSpec with Matchers with MockFactory {
  "RpcClientsService" should {
    "return active and authenticated clients ids" in {
      val chatNotificationsRpc = mock[ChatNotificationsRPC]
      (chatNotificationsRpc.connectionsCountUpdate _).expects(*).anyNumberOfTimes()

      val clientRpc = mock[MainClientRPC]
      (() => clientRpc.chat()).expects().anyNumberOfTimes().returning(chatNotificationsRpc)

      val sendToClient = mockFunction[ClientRPCTarget, MainClientRPC]
      sendToClient.expects(*).anyNumberOfTimes().returning(clientRpc)

      val service: RpcClientsService = new RpcClientsService(sendToClient)
      service.activeClients.size should be(0)
      service.authenticatedClients.size should be(0)

      service.registerConnection(ClientId("c1"))
      service.activeClients.size should be(1)
      service.authenticatedClients.size should be(0)

      service.registerAuthenticatedConnection(ClientId("c1"), mock[UserContext])
      service.activeClients.size should be(1)
      service.authenticatedClients.size should be(1)

      service.registerConnection(ClientId("c2"))
      service.activeClients.size should be(2)
      service.authenticatedClients.size should be(1)

      service.unregisterConnection(ClientId("c2"))
      service.activeClients.size should be(1)
      service.authenticatedClients.size should be(1)

      service.unregisterConnection(ClientId("c1"))
      service.activeClients.size should be(0)
      service.authenticatedClients.size should be(0)
    }
  }
}
