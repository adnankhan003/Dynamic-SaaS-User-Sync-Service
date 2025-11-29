public interface ExternalSystemConfigRepository extends JpaRepository<ExternalSystemConfig, Long> {
    ExternalSystemConfig findBySystemName(String systemName);
}