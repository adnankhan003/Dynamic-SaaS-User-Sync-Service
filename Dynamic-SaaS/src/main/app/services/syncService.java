@Service
public class SyncService {

    @Autowired private ExternalSystemConfigRepository cfgRepo;
    @Autowired private ApiCallService apiService;
    @Autowired private UserExtractionService extractor;
    @Autowired private ExternalUsersTempRepository tempRepo;

    public int syncUsers(String system) throws Exception {

        ExternalSystemConfig cfg = cfgRepo.findBySystemName(system);

        JsonNode response = apiService.callExternalApi(cfg);

        List<UserDto> users =
            extractor.extractUsers(response, cfg.getJsonPath());

        for (UserDto u : users) {
            ExternalUsersTemp entity = new ExternalUsersTemp();
            entity.setSource(system);
            entity.setExternalUserId(u.getId());
            entity.setEmail(u.getEmail());
            entity.setName(u.getName());
            entity.setRawJson(u.getRawJson().toString());
            tempRepo.save(entity);
        }

        return users.size();
    }
}

