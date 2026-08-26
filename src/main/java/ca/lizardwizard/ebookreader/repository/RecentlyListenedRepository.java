package ca.lizardwizard.ebookreader.repository;

import ca.lizardwizard.ebookreader.model.RecentlyListened;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentlyListenedRepository extends JpaRepository<RecentlyListened,Long> {
}
