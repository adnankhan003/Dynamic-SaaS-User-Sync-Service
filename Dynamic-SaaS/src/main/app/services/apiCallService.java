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