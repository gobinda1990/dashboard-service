package comtax.gov.webapp.repo;

import org.springframework.stereotype.Repository;

import comtax.gov.webapp.model.AssignRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Repository
@Slf4j
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	@Override
	public boolean saveAssignedUserData(AssignRequest assignRequest) {
		
		return false;
	}

}
