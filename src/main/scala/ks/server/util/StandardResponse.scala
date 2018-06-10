package ks.server.util


case class StandardResponse(val status: StatusResponse, val message: String)

object Statuses {
  val ERROR : StatusResponse = StatusResponse("Error")
  val SUCCESS : StatusResponse = StatusResponse("Success")
}
case class StatusResponse(val status: String)