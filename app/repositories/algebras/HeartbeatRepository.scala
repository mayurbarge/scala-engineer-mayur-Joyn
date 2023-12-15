package repositories.algebras

trait HeartbeatRepository[F[_], G[_], I, O] {
  def save(data: F[I]): G[O]
}
