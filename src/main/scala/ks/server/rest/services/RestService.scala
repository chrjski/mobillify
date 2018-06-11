package ks.server.rest.services

trait RestService[T] {
  def all: List[T]

  def get(id: String): Option[T]

  def create(id: String, any: AnyRef): T

  def delete(id: String): Boolean

  //  def filter
}
