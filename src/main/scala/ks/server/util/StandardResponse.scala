package ks.server.util


case class StandardResponse(status: StatusResponse, message: Any)

object Statuses {
  val ERROR : StatusResponse = StatusResponse("Error")
  val SUCCESS : StatusResponse = StatusResponse("Success")
}
case class StatusResponse(status: String)