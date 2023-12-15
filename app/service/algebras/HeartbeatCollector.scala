package service.algebras

import domain.Heartbeat

trait HeartbeatCollector[F[_], I, O] {
  def save(heartbeat: Heartbeat[I]): F[O]
}
