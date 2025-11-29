@Service
public class ApiCallService {

    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode callExternalApi(ExternalSystemConfig cfg) throws Exception {

        HttpMethod method = HttpMethod.valueOf(cfg.getHttpMethod().toUpperCase());
        HttpHeaders httpHeaders = new HttpHeaders();

        if (cfg.getHeaders() != null) {
            Map<String, String> headerMap =
                mapper.readValue(cfg.getHeaders(), new TypeReference<Map<String,String>>() {});
            headerMap.forEach(httpHeaders::add);
        }

        HttpEntity<?> requestEntity = new HttpEntity<>(null, httpHeaders);

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> response = rest.exchange(
                cfg.getUrl(),
                method,
                requestEntity,
                String.class
        );

        return mapper.readTree(response.getBody());
    }
}


@Service
public class UserExtractionService {

    public List<UserDto> extractUsers(JsonNode response, String jsonPath) {

        List<Map<String, Object>> extracted =
            JsonPath.read(response.toString(), jsonPath);

        List<UserDto> users = new ArrayList<>();

        for (Map<String, Object> u : extracted) {
            UserDto dto = new UserDto();
            dto.setId((String) u.get("uri"));  // Calendly field
            dto.setEmail((String) u.get("email"));
            dto.setName((String) u.get("name"));
            dto.setRawJson(u);

            users.add(dto);
        }
        return users;
    }
}

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

