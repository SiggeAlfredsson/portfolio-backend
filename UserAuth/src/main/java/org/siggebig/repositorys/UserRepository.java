package org.siggebig.repositorys;

import org.siggebig.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // User findByEmail(String email);

    User findByUsername(String username);

    List<User> findByIdIn(List<Long> ids);

}
