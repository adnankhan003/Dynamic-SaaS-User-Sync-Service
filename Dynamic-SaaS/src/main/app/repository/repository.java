public interface ExternalSystemConfigRepository extends JpaRepository<ExternalSystemConfig, Long> {
    ExternalSystemConfig findBySystemName(String systemName);
}

public interface ExternalUsersTempRepository extends JpaRepository<ExternalUsersTemp, Long> {}
