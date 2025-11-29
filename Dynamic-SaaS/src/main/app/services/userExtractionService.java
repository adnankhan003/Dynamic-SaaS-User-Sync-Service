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